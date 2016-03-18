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
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.list.ComponentCard;
import org.seedstack.hub.rest.list.ComponentFinder;
import org.seedstack.hub.rest.shared.PageInfo;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.security.AuthenticationException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static java.util.stream.Collectors.toList;
import static org.seedstack.hub.rest.Rels.*;

@Api
@Path("/user")
public class UserResource {
    public static final String USER_ID = "userId";
    @Inject
    private StarringService starringService;
    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private FluentAssembler fluentAssembler;
    @Inject
    private RelRegistry relRegistry;
    @Inject
    private SecurityService securityService;

    @Rel(USER_COMPONENTS)
    @GET
    @Path("/components")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getComponents(@BeanParam PageInfo pageInfo) {
        UserId userId = securityService.getAuthenticatedUser().orElseThrow(AuthenticationException::new).getEntityId();
        PaginatedView<ComponentCard> userComponents = componentFinder.findCurrentUserCards(userId, pageInfo.page());
        return new HalRepresentation()
                .embedded("components", userComponents)
                .link("self", relRegistry.uri(USER_COMPONENTS)
                        .set("pageIndex", pageInfo.getPageIndex())
                        .set("pageSize", pageInfo.getPageSize())
                        .expand());
    }

    @Rel(AUTHOR_COMPONENTS)
    @GET
    @Path("{userId}/components")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getComponents(@PathParam(USER_ID) String userId, @BeanParam PageInfo pageInfo) {
        PaginatedView<ComponentCard> userComponents = componentFinder.findCurrentUserCards(new UserId(userId), pageInfo.page());
        return new HalRepresentation()
                .embedded("components", userComponents)
                .link("self", relRegistry.uri(USER_COMPONENTS)
                        .set("pageIndex", pageInfo.getPageIndex())
                        .set("pageSize", pageInfo.getPageSize())
                        .expand());
    }

    @Rel(STARS)
    @GET
    @Path("/stars")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getStars() {
        return new HalRepresentation()
                .embedded("components",
                        starringService.starredComponents()
                                .map(c -> fluentAssembler.assemble(c).to(ComponentCard.class))
                                .collect(toList()))
                .link("self", relRegistry.uri(STARS).expand());
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
        return Response.created(new URI(relRegistry.uri(STARS).set("componentId", componentId).expand())).build();
    }

    @Rel(STAR)
    @DELETE
    @Path("/stars/{componentId}")
    public void unstarComponent(@PathParam("componentId") String componentId) throws URISyntaxException {
        starringService.unstar(new ComponentId(componentId));
    }
}
