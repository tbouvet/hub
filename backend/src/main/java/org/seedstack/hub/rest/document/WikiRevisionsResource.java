/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.document;

import io.swagger.annotations.Api;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentException;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.hub.domain.model.document.WikiDocument;
import org.seedstack.hub.rest.shared.Dates;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Api
@Path("/components/{componentId}/wiki/{page:[^/]+}")
public class WikiRevisionsResource {
    @Inject
    private Repository<Document, DocumentId> documentRepository;
    @Inject
    private FluentAssembler fluentAssembler;
    @Inject
    private RelRegistry relRegistry;
    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders httpHeaders;
    @PathParam("componentId")
    private String componentId;
    @PathParam("page")
    private String page;

    @GET
    @Path("/revisions")
    @Produces({"application/json", "application/hal+json"})
    public Response getRevisions() {
        return Response.ok(fluentAssembler.assemble(getWikiDocument()).to(WikiPageInfo.class)).build();
    }

    @GET
    @Path("/revisions/{revisionId}")
    @Produces({"application/json", "application/hal+json"})
    public Response getRevision(@PathParam("revisionId") int revisionId) {
        org.seedstack.hub.domain.model.document.Revision revision = getWikiDocument().getRevision(revisionId);
        if (revision == null) {
            throw new NotFoundException("Revision of wiki page " + page + " for component " + componentId + " not found: " + revisionId);
        }

        WikiPageInfo.Revision wikiPageRevision = new WikiPageInfo.Revision();
        wikiPageRevision.setMessage(revision.getMessage());
        wikiPageRevision.setDate(Dates.asDate(revision.getDate()));
        wikiPageRevision.setAuthor(revision.getAuthor().getId());

        return Response.ok(wikiPageRevision).build();
    }

    @GET
    @Path("/revisions/{revisionId}/diff")
    @Produces({"application/json", "application/hal+json"})
    public Response getDiff(@PathParam("revisionId") int revisionId) {
        try {
            return Response.ok(getWikiDocument().diff(revisionId - 1, revisionId, true)).build();
        } catch (DocumentException e) {
            throw new BadRequestException("Invalid revision index");
        }
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
