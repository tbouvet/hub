/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import io.swagger.annotations.Api;
import org.seedstack.business.assembler.AssemblerTypes;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.application.StarringService;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static java.util.stream.Collectors.toList;

@Api
@Path("/user")
public class UserResource {

    public static final String STARS = "stars";

    @Inject
    private StarringService starringService;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private FluentAssembler fluentAssembler;
    @Inject
    private RelRegistry relRegistry;

    @Rel(STARS)
    @GET
    @Path("/stars")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getStars() {
        return new HalRepresentation()
                .embedded("components",
                        starringService.starredComponents()
                                .map(c -> fluentAssembler.assemble(c).with(AssemblerTypes.MODEL_MAPPER).to(ComponentCard.class))
                                .collect(toList()))
                .link("self", relRegistry.uri(STARS).expand());
    }

    @Rel(STARS)
    @POST
    @Path("/stars/{componentId}")
    public Response starComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        starringService.star(new ComponentId(componentId));
        return Response.created(new URI(relRegistry.uri(STARS).set("componentId", componentId).expand())).build();
    }

    @Rel(STARS)
    @DELETE
    @Path("/stars/{componentId}")
    public void unstarComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        starringService.unstar(new ComponentId(componentId));
    }
}
