/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.user;

import io.swagger.annotations.Api;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.hub.rest.shared.RangeInfo;
import org.seedstack.hub.rest.shared.ResultHal;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static org.seedstack.hub.rest.Rels.*;

@Api
@Path("/user")
public class UserResource {
    public static final String USER_ID = "userId";
    @Inject
    private StarringService starringService;
    @Inject
    private UserFinder userFinder;
    @Inject
    private FluentAssembler fluentAssembler;
    @Inject
    private RelRegistry relRegistry;
    @Inject
    private SecurityService securityService;

    @Rel(value = USER_COMPONENTS, home = true)
    @GET
    @Path("/components")
    @Produces({"application/json", "application/hal+json"})
    public ResultHal getComponents(@BeanParam RangeInfo rangeInfo) {
        UserId userId = securityService.getAuthenticatedUser().getEntityId();
        Result<ComponentCard> userComponents = userFinder.findUserCards(userId, rangeInfo.range());
        return new ResultHal<>(COMPONENTS, userComponents, relRegistry.uri(USER_COMPONENTS));
    }

    @Rel(AUTHOR_COMPONENTS)
    @GET
    @Path("{userId}/components")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getComponents(@PathParam(USER_ID) String userId, @BeanParam RangeInfo rangeInfo) {
        Result<ComponentCard> userComponents = userFinder.findUserCards(new UserId(userId), rangeInfo.range());
        return new ResultHal<>(COMPONENTS, userComponents, relRegistry.uri(AUTHOR_COMPONENTS).set("userId", userId));
    }

    @Rel(value = STARS, home = true)
    @GET
    @Path("/stars")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getStars(@BeanParam RangeInfo rangeInfo) {
        UserId userId = securityService.getAuthenticatedUser().getEntityId();
        return new ResultHal<>(COMPONENTS, userFinder.findStarred(userId, rangeInfo.range()), relRegistry.uri(STARS));
    }

    @Rel(STAR)
    @GET
    @Path("/stars/{componentId}")
    public Response isStarredComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        boolean hasStarred = starringService.hasStarred(new ComponentId(componentId));
        return Response.ok(String.format("{isStarred: %s}", hasStarred)).build();
    }

    @Rel(STAR)
    @POST
    @Path("/stars/{componentId}")
    public Response starComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        starringService.star(new ComponentId(componentId));
        return Response.created(new URI(relRegistry.uri(STARS).set("componentId", componentId).getHref())).build();
    }

    @Rel(STAR)
    @DELETE
    @Path("/stars/{componentId}")
    public void unstarComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        starringService.unstar(new ComponentId(componentId));
    }
}
