/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.user;

import io.swagger.annotations.Api;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.hub.rest.shared.RangeInfo;
import org.seedstack.hub.rest.shared.ResultHal;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static org.seedstack.hub.rest.Rels.COMPONENTS;
import static org.seedstack.hub.rest.Rels.STAR;
import static org.seedstack.hub.rest.Rels.STARS;
import static org.seedstack.hub.rest.Rels.USER_COMPONENTS;

@Api(tags = {"User", "Component"})
@Path("/user")
public class CurrentUserResource {
    @Inject
    private StarringService starringService;
    @Inject
    private UserFinder userFinder;
    @Inject
    private RelRegistry relRegistry;

    private final UserId currentUser;

    @Inject
    public CurrentUserResource(SecurityService securityService) {
        currentUser = securityService.getAuthenticatedUser().getEntityId();
    }

    @Rel(value = USER_COMPONENTS, home = true)
    @GET
    @Path("/components")
    @Produces({"application/json", "application/hal+json"})
    public ResultHal getComponents(@BeanParam RangeInfo rangeInfo) {
        Result<ComponentCard> userComponents = userFinder.findUserComponents(currentUser, rangeInfo.range());
        return new ResultHal<>(COMPONENTS, userComponents, relRegistry.uri(USER_COMPONENTS));
    }

    @Rel(value = STARS, home = true)
    @GET
    @Path("/stars")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getStars(@BeanParam RangeInfo rangeInfo) {
        return new ResultHal<>(COMPONENTS, userFinder.findStarred(currentUser, rangeInfo.range()), relRegistry.uri(STARS));
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
        return Response.created(new URI(relRegistry.uri(STAR).set("componentId", componentId).getHref())).build();
    }

    @Rel(STAR)
    @DELETE
    @Path("/stars/{componentId}")
    public void unstarComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        starringService.unstar(new ComponentId(componentId));
    }
}
