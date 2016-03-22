/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.detail;

import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Length;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.application.StatePolicy;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.list.ComponentFinder;
import org.seedstack.hub.rest.shared.PageInfo;
import org.seedstack.hub.rest.shared.UriBuilder;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.security.AuthenticationException;
import org.seedstack.seed.security.AuthorizationException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.seedstack.hub.rest.Rels.COMPONENT;
import static org.seedstack.hub.rest.Rels.STATE;

@Api
@Path("/components/{componentId}")
@Produces({"application/json", "application/hal+json"})
public class ComponentResource {

    static final String COMPONENT_ID = "componentId";

    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private RelRegistry relRegistry;
    @Inject
    private FluentAssembler fluentAssembler;
    @Inject
    private SecurityService securityService;
    @Inject
    private ServletContext servletContext;
    @Inject
    private StatePolicy statePolicy;

    @PathParam(COMPONENT_ID)
    private String componentId;

    @GET
    @Rel(value = COMPONENT, home = true)
    public ComponentDetails details() {
        Component component = componentRepository.load(new ComponentId(componentId));
        if (component == null) {
            throw new NotFoundException("Component " + componentId + " not found");
        }

        ComponentDetails componentDetails = fluentAssembler.assemble(component).to(ComponentDetails.class);
        updateUrls(componentDetails);
        return componentDetails;
    }

    @PUT
    @Path("/state")
    @Rel(value = STATE)
    public void changeState(State state) {
        Component component = componentRepository.load(new ComponentId(componentId));
        switch (state) {
            case PUBLISHED:
                if (statePolicy.canPublish(component)) {
                    component.publish();
                } else {
                    throw new AuthorizationException();
                }
                break;
            case ARCHIVED:
                if (statePolicy.canArchive(component)) {
                    component.archive();
                } else {
                    throw new AuthorizationException();
                }
                break;
            default:
                throw new BadRequestException();
        }
    }

    @GET
    @Path("/comments")
    @Rel(value = Rels.COMMENT)
    public HalRepresentation getComments(@BeanParam PageInfo pageInfo) {
        HalRepresentation halRepresentation = new HalRepresentation();
        PaginatedView<Comment> comments = componentFinder.findComments(new ComponentId(componentId), pageInfo.page());
        halRepresentation.embedded(Rels.COMMENT, comments);
        halRepresentation.link("self", relRegistry.uri(Rels.COMMENT)
                .set(COMPONENT_ID, componentId)
                .set("pageIndex", pageInfo.getPageIndex())
                .set("pageSize", pageInfo.getPageSize())
                .expand());
        return halRepresentation;
    }

    @POST
    @Path("/comments")
    @Rel(value = Rels.COMMENT)
    public Response postComments(@Length(max = 144) String content) {
        Component component = componentRepository.load(new ComponentId(componentId));
        if (component == null) {
            throw new NotFoundException();
        }
        User user = securityService.getAuthenticatedUser().orElseThrow(AuthenticationException::new);
        Comment comment = new Comment(user.getId().getId(), content, new Date());
        component.addComment(comment);
        componentRepository.persist(component);
        return Response.created(URI.create(relRegistry.uri(Rels.COMMENT).set(COMPONENT_ID, componentId).expand()))
                .entity(comment).build();
    }

    private void updateUrls(ComponentDetails componentDetails) {
        componentDetails.setImages(addContextPath(componentDetails.getImages()));
        componentDetails.setDocs(addContextPath(componentDetails.getDocs()));
        componentDetails.setIcon(addContextPath(componentDetails.getIcon()));
        componentDetails.setReadme(addContextPath(componentDetails.getReadme()));
    }

    private List<String> addContextPath(List<String> urls) {
        return urls.stream().map(this::addContextPath).collect(toList());
    }

    private String addContextPath(String url) {
        return UriBuilder.uri(servletContext.getContextPath(), url);
    }
}
