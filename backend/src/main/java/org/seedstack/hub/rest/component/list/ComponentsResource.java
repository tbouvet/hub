/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.component.list;

import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.application.fetch.ImportService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.services.fetch.SourceType;
import org.seedstack.hub.rest.admin.ImportReport;
import org.seedstack.hub.rest.shared.RangeInfo;
import org.seedstack.hub.rest.shared.ResultHal;
import org.seedstack.seed.Logging;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.rest.hal.Link;
import org.seedstack.seed.security.RequiresRoles;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.seedstack.hub.rest.Rels.*;

@Api
@Path("/")
@Produces({"application/json", "application/hal+json"})
public class ComponentsResource {
    @Logging
    private Logger logger;

    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private RelRegistry relRegistry;
    @Inject
    private ImportService importService;
    @Inject
    private FluentAssembler fluentAssembler;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/components")
    public Response post(
            @FormParam("sourceType") @NotBlank @Length(max = 10) String vcs,
            @FormParam("sourceUrl") @URL @NotBlank String sourceUrl) {
        final ImportReport importReport = new ImportReport();
        Component component = null;
        try {
            component = importService.importComponent(new Source(SourceType.from(vcs), sourceUrl));
            ComponentCard componentCard = fluentAssembler.assemble(component).to(ComponentCard.class);
            URI componentURI = URI.create(relRegistry.uri(COMPONENT).set("componentId", componentCard.getId()).getHref());
            importReport.addComponentCard(componentCard);
            return Response.created(componentURI).entity(importReport).build();
        } catch (ImportException e) {
            logger.debug(e.getMessage(), e);
            importReport.addFailedSource(e);
            return Response.status(Response.Status.BAD_REQUEST).entity(importReport).build();
        }
    }

    @GET
    @Path("/components")
    @Rel(value = COMPONENTS, home = true)
    public ResultHal<ComponentCard> list(
            @BeanParam RangeInfo rangeInfo,
            @QueryParam("sort") @Length(max = 64) String sort,
            @QueryParam("search") @Length(max = 64) String searchName) {

        Result<ComponentCard> result = componentFinder.findPublishedCards(rangeInfo.range(), SortType.fromOrDefault(sort), searchName);
        Link self = relRegistry.uri(COMPONENTS);
        if (searchName != null && !searchName.equals("")) {
            self.set("search", searchName);
        }
        if (sort != null && !sort.equals("")) {
            self.set("sort", sort);
        }
        return new ResultHal<>(COMPONENTS, result, self);
    }

    @RequiresRoles("admin")
    @GET
    @Path("/pending")
    @Rel(value = PENDING, home = true)
    public ResultHal listPendingComponents(@BeanParam RangeInfo rangeInfo) {
        Result<ComponentCard> result = componentFinder.findCardsByState(rangeInfo.range(), State.PENDING);
        return new ResultHal<>(COMPONENTS, result, relRegistry.uri(PENDING));
    }

    @GET
    @Path("/popular")
    @Rel(value = POPULAR, home = true)
    public ResultHal<ComponentCard> popularCards(@BeanParam RangeInfo rangeInfo) {
        return list(rangeInfo, SortType.STARS.name(), null);
    }

    @GET
    @Path("/recent")
    @Rel(value = RECENT, home = true)
    public HalRepresentation recentCards(@BeanParam RangeInfo rangeInfo) {
        return list(rangeInfo, SortType.DATE.name(), null);
    }
}
