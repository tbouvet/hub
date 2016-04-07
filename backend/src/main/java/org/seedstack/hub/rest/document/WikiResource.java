/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.document;

import io.swagger.annotations.Api;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.hub.domain.model.document.WikiDocument;
import org.seedstack.hub.domain.services.text.TextService;
import org.seedstack.hub.rest.Rels;
import org.seedstack.seed.rest.Rel;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Api
@Path("/components/{componentId}/wiki/{page}")
public class WikiResource {
    @Inject
    private Repository<Document, DocumentId> documentRepository;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private DocumentFactory documentFactory;
    @Inject
    private TextService textService;
    @Inject
    private SecurityService securityService;
    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders httpHeaders;
    @PathParam("componentId")
    private String componentId;
    @PathParam("page")
    private String page;

    @GET
    @Produces("text/html")
    @Rel(Rels.WIKI)
    public Response getPage() {
        WikiDocument wikiDocument = getWikiDocument();
        return Response.ok(textService.renderHtml(wikiDocument)).build();
    }

    @PUT
    @Consumes("text/markdown")
    @Produces("text/html")
    @Rel(Rels.WIKI)
    public Response updatePage(@QueryParam("message") String message, String body) {
        WikiDocument wikiDocument = getWikiDocument();
        wikiDocument.addRevision(body, securityService.getAuthenticatedUser().getId(), message);
        documentRepository.save(wikiDocument);
        return Response.ok(textService.renderHtml(wikiDocument)).build();
    }

    @POST
    @Consumes("text/markdown")
    @Produces("text/html")
    @Rel(Rels.WIKI)
    public Response createPage(@QueryParam("message") String message, String body) {
        Component component = getComponent();
        DocumentId documentId = new DocumentId(new ComponentId(componentId), DocumentScope.WIKI, page);
        if (documentRepository.exists(documentId)) {
            throw new ClientErrorException("Wiki page " + page + " already exists for component " + componentId, Response.Status.CONFLICT);
        }

        WikiDocument wikiDocument = documentFactory.createWikiDocument(
                documentId,
                body,
                securityService.getAuthenticatedUser().getId(),
                message
        );
        component.addWikiPage(wikiDocument.getId());

        documentRepository.persist(wikiDocument);
        componentRepository.persist(component);

        return Response.created(UriBuilder.fromPath(page).build()).entity(textService.renderHtml(wikiDocument)).build();
    }

    @DELETE
    @Rel(Rels.WIKI)
    public Response deletePage() {
        Component component = getComponent();
        WikiDocument wikiDocument = getWikiDocument();
        component.removeWikiPage(wikiDocument.getId());
        componentRepository.persist(component);
        documentRepository.delete(wikiDocument);
        return Response.ok().build();
    }

    private Component getComponent() {
        Component component = componentRepository.load(new ComponentId(componentId));
        if (component == null) {
            throw new NotFoundException("Component " + componentId + " not found");
        }
        return component;
    }

    private WikiDocument getWikiDocument() {
        Document doc = documentRepository.load(new DocumentId(new ComponentId(componentId), DocumentScope.WIKI, page));
        if (doc == null) {
            throw new NotFoundException("Wiki page not found for component " + componentId + ": " + page);
        }
        if (doc instanceof WikiDocument) {
            return (WikiDocument) doc;
        } else {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
