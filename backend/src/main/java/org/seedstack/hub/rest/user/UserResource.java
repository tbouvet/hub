/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.user;

import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.hub.rest.shared.RangeInfo;
import org.seedstack.hub.rest.shared.ResultHal;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.security.AuthorizationException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static org.seedstack.hub.rest.Rels.*;

@Api
@Path("/users/{userId}")
public class UserResource {

    public static final String USER_ID = "userId";

    @Inject
    private UserRepository userRepository;
    @Inject
    private UserFinder userFinder;
    @Inject
    private FluentAssembler fluentAssembler;
    @Inject
    private RelRegistry relRegistry;
    @Inject
    private SecurityService securityService;

    @PathParam(USER_ID)
    private String userId;

    @Rel(value = USER, home = true)
    @GET
    @Produces({"application/json", "application/hal+json"})
    public UserCard get() {
        User user = userRepository.findByName(userId).orElseThrow(NotFoundException::new);
        return fluentAssembler.assemble(user).to(UserCard.class);
    }

    @Rel(USER)
    @PUT
    @Produces({"application/json", "application/hal+json"})
    public UserCard update(UserCard userCard) {
        checkCanEdit();
        User user = securityService.getAuthenticatedUser();
        fluentAssembler.merge(userCard).into(user);
        userRepository.save(user);
        return fluentAssembler.assemble(user).to(UserCard.class);
    }

    private void checkCanEdit() {
        boolean isEditingHimself = securityService.getAuthenticatedUser().getId().getId().equals(userId);
        if (!isEditingHimself && !securityService.isUserAdmin()) {
            throw new AuthorizationException();
        }
    }

    @Rel(USER)
    @DELETE
    public void delete() {
        User user = userRepository.findByName(userId).orElseThrow(NotFoundException::new);
        if (securityService.getAuthenticatedUser().equals(user) || securityService.isUserAdmin()) {
            userRepository.delete(user);
        } else {
            throw new AuthorizationException();
        }
    }

    @Rel(Rels.USERS_ICON)
    @GET
    @Path("icon")
    public byte[] getIcon() {
        User user = userRepository.findByName(userId).orElseThrow(NotFoundException::new);
        byte[] icon = user.getIcon();
        if (icon == null) {
            throw new NotFoundException();
        }
        return icon;
    }

    @Rel(Rels.USERS_ICON)
    @PUT
    @Path("icon")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation uploadIcon(@FormDataParam("file") byte[] bytes) {
        checkCanEdit();
        User user = securityService.getAuthenticatedUser();
        user.setIcon(bytes);
        userRepository.save(user);
        return fluentAssembler.assemble(user).to(UserCard.class);
    }

    @Rel(Rels.USERS_ICON)
    @DELETE
    @Path("icon")
    public void deleteIcon() {
        checkCanEdit();
        User user = securityService.getAuthenticatedUser();
        user.setIcon(null);
        userRepository.save(user);
    }

    @Rel(USERS_COMPONENTS)
    @GET
    @Path("components")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getComponents(@BeanParam RangeInfo rangeInfo) {
        Result<ComponentCard> userComponents = userFinder.findUserComponents(new UserId(userId), rangeInfo.range());
        return new ResultHal<>(COMPONENTS, userComponents, relRegistry.uri(USERS_COMPONENTS).set("userId", userId));
    }

    @Rel(USERS_STARS)
    @GET
    @Path("/stars")
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getStars(@BeanParam RangeInfo rangeInfo) {
        UserId userId = securityService.getAuthenticatedUser().getEntityId();
        return new ResultHal<>(COMPONENTS, userFinder.findStarred(userId, rangeInfo.range()), relRegistry.uri(STARS));
    }
}
