/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.hibernate.validator.constraints.NotBlank;
import org.seedstack.business.assembler.AssemblerTypes;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.application.ImportService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.seed.Logging;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.rest.hal.Link;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Path("/components")
public class ComponentResource {
    public static final String COMPONENTS = "components";
    public static final String COMPONENT = "component";

    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private RelRegistry relRegistry;
    @Inject
    private ImportService importService;
    @Inject
    private FluentAssembler fluentAssembler;
    @Logging
    private Logger logger;

    @Rel(value = COMPONENTS, home = true)
    @GET
    @Produces({"application/json", "application/hal"})
    public HalRepresentation getList(@QueryParam("search") String searchName, @BeanParam PageInfo pageInfo,
                                     @QueryParam("sort") String sort) {
        PaginatedView<ComponentCard> paginatedView = componentFinder.findCards(pageInfo.page(), searchName, sort);
        return buildListHALRepresentation(paginatedView, searchName, pageInfo);
    }

    @Rel(value = COMPONENT, home = true)
    @GET
    @Path("/{componentId}")
    @Produces({"application/json", "application/hal"})
    public HalRepresentation getList(@PathParam("componentId") String componentId) {
        Component component = componentRepository.load(new ComponentId(componentId));

        if (component == null) {
            throw new NotFoundException("Component " + componentId + " not found");
        }

        return fluentAssembler.assemble(component).with(AssemblerTypes.MODEL_MAPPER).to(ComponentDetails.class);
    }

    private HalRepresentation buildListHALRepresentation(PaginatedView<ComponentCard> paginatedView, String searchName, PageInfo pageInfo) {
        HalRepresentation halRepresentation = new HalRepresentation();
        halRepresentation.embedded(COMPONENTS, paginatedView.getView());

        halRepresentation.link("self", buildURI(pageInfo.page(), ofNullable(searchName)));

        if (paginatedView.hasNext()) {
            Page next = paginatedView.next();
            halRepresentation.link("next", buildURI(next, ofNullable(searchName)));
        }
        if (paginatedView.hasPrev()) {
            Page prev = paginatedView.prev();
            halRepresentation.link("prev", buildURI(prev, ofNullable(searchName)));
        }
        return halRepresentation;
    }

    private String buildURI(Page prev, Optional<String> searchName) {
        Link link = relRegistry.uri(COMPONENTS)
                .set(PageInfo.PAGE_INDEX, prev.getIndex()).set(PageInfo.PAGE_SIZE, prev.getCapacity());
        if (searchName.isPresent()) {
            link.set("search", searchName.get());
        }
        return link.expand();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({"application/json", "application/hal"})
    public Response post(@FormParam("vcs") @NotBlank String vcs, @FormParam("url") @NotBlank String sourceUrl) throws URISyntaxException {
        VCSType vcsType;
        try {
            vcsType = VCSType.valueOf(vcs.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unsupported VCS type " + vcs);
        }

        Component component;
        try {
            component = importService.importComponent(vcsType, new URL(sourceUrl));
        } catch (MalformedURLException e) {
            throw new BadRequestException("Malformed URL " + sourceUrl);
        } catch (Exception e) {
            logger.error("Error during component import", e);
            throw new WebApplicationException(400);
        }

        ComponentCard componentCard = fluentAssembler.assemble(component).with(AssemblerTypes.MODEL_MAPPER).to(ComponentCard.class);

        return Response.created(new URI(relRegistry.uri(COMPONENT).set("componentId", componentCard.getId()).expand())).entity(componentCard).build();
    }
}
