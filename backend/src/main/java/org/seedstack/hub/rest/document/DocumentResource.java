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
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.BinaryDocument;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.services.text.TextService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api
@Path("/components/{componentId}/files/{documentPath:.+}")
public class DocumentResource {
    @Inject
    private Repository<Document, DocumentId> documentRepository;
    @Inject
    private TextService textService;

    @GET
    public Response retrieveDocument(@PathParam("componentId") String componentId, @PathParam("documentPath") String path) {
        Document doc = documentRepository.load(new DocumentId(new ComponentId(componentId), path));
        if (doc == null) {
            throw new NotFoundException("Document not found");
        }

        if (doc instanceof TextDocument) {
            return Response.ok(textService.renderHtml((TextDocument) doc)).type(MediaType.TEXT_HTML_TYPE).build();
        } else if (doc instanceof BinaryDocument) {
            return Response.ok(((BinaryDocument) doc).getData()).type(doc.getContentType()).build();
        } else {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
