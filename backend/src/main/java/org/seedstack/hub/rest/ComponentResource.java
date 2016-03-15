/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.NotBlank;
import org.seedstack.business.assembler.AssemblerTypes;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.application.ImportService;
import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.seed.Logging;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.rest.hal.Link;
import org.seedstack.seed.security.AuthenticationException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Api
@Path("/")
public class ComponentResource {
    public static final String DEFAULT_QUANTITY = "6";
    public static final String RECENT = "recent";
    public static final String POPULAR = "popular";
    public static final String COMPONENTS = "components";
    public static final String COMPONENT = "component";
    public static final String COMMENT = "comment";

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
    @Inject
    private SecurityService securityService;
    @Inject
    private ServletContext servletContext;
    @Logging
    private Logger logger;

    @Rel(value = COMPONENTS, home = true)
    @GET
    @Produces({"application/json", "application/hal+json"})
    @Path("/components")
    public HalRepresentation list(@QueryParam("search") String searchName, @BeanParam PageInfo pageInfo,
                                     @QueryParam("sort") String sort) {
        PaginatedView<ComponentCard> paginatedView = componentFinder.findCards(pageInfo.page(), searchName, sort);
        updateUrls(paginatedView);
        return buildHALRepresentation(paginatedView, searchName, pageInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json", "application/hal+json"})
    @Path("/components")
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
        updateUrls(componentCard);
        return Response.created(new URI(relRegistry.uri(COMPONENT).set("componentId", componentCard.getId()).expand())).entity(componentCard).build();
    }

    @Rel(value = COMPONENT, home = true)
    @GET
    @Produces({"application/json", "application/hal+json"})
    @Path("/components/{componentId}")
    public ComponentDetails details(@PathParam("componentId") String componentId) {
        Component component = componentRepository.load(new ComponentId(componentId));

        if (component == null) {
            throw new NotFoundException("Component " + componentId + " not found");
        }

        ComponentDetails componentDetails = fluentAssembler.assemble(component).with(AssemblerTypes.MODEL_MAPPER).to(ComponentDetails.class);
        updateUrls(componentDetails);
        return componentDetails;
    }

    @Rel(value = COMMENT)
    @GET
    @Produces({"application/json", "application/hal+json"})
    @Path("/components/{componentId}/comments")
    public HalRepresentation getComments(@PathParam("componentId") String componentId, @BeanParam PageInfo pageInfo) {
        HalRepresentation halRepresentation = new HalRepresentation();
        PaginatedView<Comment> comments = componentFinder.findComments(new ComponentId(componentId), pageInfo.page());
        halRepresentation.embedded(COMMENT, comments);
        halRepresentation.link("self", relRegistry.uri(COMMENT)
                .set("componentId", componentId)
                .set("pageIndex", pageInfo.getPageIndex())
                .set("pageSize", pageInfo.getPageSize())
                .expand());
        return halRepresentation;
    }

    @Rel(value = COMMENT)
    @POST
    @Produces({"application/json", "application/hal+json"})
    @Path("/components/{componentId}/comments")
    public Response postComments(@PathParam("componentId") String componentId, String comment) {
        Component component = componentRepository.load(new ComponentId(componentId));
        if (component == null) {
            throw new NotFoundException();
        }
        User user = securityService.getAuthenticatedUser().orElseThrow(AuthenticationException::new);
        component.addComment(new Comment(user.getId().getId(), comment, new Date()));
        componentRepository.persist(component);
        return Response.created(URI.create(relRegistry.uri(COMMENT).set("componentId", componentId).expand())).build();
    }

    @Rel(value = POPULAR, home = true)
    @GET
    @Produces({"application/json", "application/hal+json"})
    @Path("/popular")
    public HalRepresentation popularCards(@QueryParam("size") @DefaultValue(DEFAULT_QUANTITY) int size) {
        HalRepresentation halRepresentation = new HalRepresentation();
        Result<ComponentCard> popularCards = componentFinder.findPopularCards(size);
        updateUrls(popularCards);
        halRepresentation.embedded(POPULAR, popularCards);
        halRepresentation.link("self", relRegistry.uri(POPULAR).expand());

        return halRepresentation;
    }

    @Rel(value = RECENT, home = true)
    @GET
    @Produces({"application/json", "application/hal+json"})
    @Path("/recent")
    public HalRepresentation recentCards(@QueryParam("size") @DefaultValue(DEFAULT_QUANTITY) int size) {
        HalRepresentation halRepresentation = new HalRepresentation();
        Result<ComponentCard> recentCards = componentFinder.findRecentCards(size);
        updateUrls(recentCards);
        halRepresentation.embedded(RECENT, recentCards);
        halRepresentation.link("self", relRegistry.uri(RECENT).expand());

        return halRepresentation;
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

    private void updateUrls(ComponentDetails componentDetails) {
        componentDetails.setImages(componentDetails.getImages().stream().map(url -> servletContext.getContextPath() + "/" + url).collect(toList()));
        componentDetails.setDocs(componentDetails.getDocs().stream().map(url -> servletContext.getContextPath() + "/" + url).collect(toList()));
        componentDetails.setIcon(servletContext.getContextPath() + "/" + componentDetails.getIcon());
        componentDetails.setReadme(servletContext.getContextPath() + "/" + componentDetails.getReadme());
    }

    private void updateUrls(ComponentCard componentCard) {
        componentCard.setIcon(servletContext.getContextPath() + "/" + componentCard.getIcon());
    }

    private void updateUrls(PaginatedView<ComponentCard> paginatedView) {
        paginatedView.getView().stream().forEach(this::updateUrls);
    }

    private void updateUrls(Result<ComponentCard> result) {
        result.getResult().stream().forEach(this::updateUrls);
    }
}
