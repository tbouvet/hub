/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.rest.hal.Link;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Path("/components")
public class ComponentResource {

    public static final String COMPONENTS = "components";

    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private RelRegistry relRegistry;

    @Rel(value = COMPONENTS, home = true)
    @GET
    @Produces({"application/json", "application/hal"})
    public HalRepresentation getList(@QueryParam("search") String searchName, @BeanParam PageInfo pageInfo,
                                     @QueryParam("sort") String sort) {
        PaginatedView<ComponentCard> paginatedView = componentFinder.findCards(pageInfo.page(), searchName, sort);
        return buildHALRepresentation(paginatedView, searchName, pageInfo);
    }

    private HalRepresentation buildHALRepresentation(PaginatedView<ComponentCard> paginatedView, String searchName, PageInfo pageInfo) {
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
    @Consumes({"application/json", "application/hal"})
    public Response post(@FormParam("owner") String owner, @FormParam("url") String sourceUrl,
                         @FormParam("icon") String componentIcon) throws URISyntaxException {
        // TODO call a service
        return Response.created(new URI(relRegistry.uri(COMPONENTS).expand())).build();
    }


}
