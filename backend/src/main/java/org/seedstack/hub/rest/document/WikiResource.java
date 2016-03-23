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
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.model.document.WikiDocument;
import org.seedstack.hub.domain.services.text.TextService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
@Path("/components/{componentId}/wiki")
public class WikiResource {
    @Inject
    private Repository<Document, DocumentId> documentRepository;
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

    @GET
    @Path("{page:[^/]+}")
    @Produces("text/html")
    public Response getPage(@PathParam("page") String page) {
        Document doc = documentRepository.load(new DocumentId(new ComponentId(componentId), DocumentScope.WIKI, page));
        if (doc == null) {
            throw new NotFoundException("Wiki page not found for component " + componentId + ": " + page);
        }

        if (doc instanceof WikiDocument) {
            return Response.ok(textService.renderHtml((WikiDocument) doc)).build();
        } else {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("{page:[^/]+}")
    @Consumes("text/markdown")
    @Produces("text/html")
    public Response createPage(@PathParam("page") String page, @QueryParam("message") String message, String body) {
        WikiDocument wikiDocument = documentFactory.createWikiDocument(
                new DocumentId(
                        new ComponentId(componentId),
                        DocumentScope.WIKI,
                        page
                ),
                body,
                securityService.getAuthenticatedUser().getId(),
                message
        );

        documentRepository.persist(wikiDocument);

        return Response.created(UriBuilder.fromPath(page).build()).entity(textService.renderHtml(wikiDocument)).build();
    }

    @PUT
    @Path("{page:[^/]+}")
    @Consumes("text/markdown")
    @Produces("text/html")
    public Response updatePage(@PathParam("page") String page, @QueryParam("message") String message, String body) {
        DocumentId documentId = new DocumentId(new ComponentId(componentId), DocumentScope.WIKI, page);
        Document doc = documentRepository.load(documentId);

        if (doc instanceof WikiDocument) {
            ((WikiDocument) doc).addRevision(body, securityService.getAuthenticatedUser().getId(), message);
            documentRepository.save(doc);
            return Response.ok(textService.renderHtml((TextDocument) doc)).build();
        } else {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
