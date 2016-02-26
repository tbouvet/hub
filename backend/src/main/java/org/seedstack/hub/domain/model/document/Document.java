/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.hub.domain.model.document;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.seedstack.business.domain.BaseAggregateRoot;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.validation.constraints.NotNull;

@Entity("documents")
public abstract class Document extends BaseAggregateRoot<DocumentId> {
    @Id
    @NotNull
    private DocumentId documentId;
    @NotNull
    private String contentType;

    Document(DocumentId documentId, String contentType) {
        this.documentId = documentId;
        try {
            this.contentType = new MimeType(contentType).toString();
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException("Provided content type is invalid: " + contentType, e);
        }
    }

    protected Document() {
        // required by morphia
    }

    @Override
    public DocumentId getEntityId() {
        return documentId;
    }

    public DocumentId getDocumentId() {
        return documentId;
    }

    public String getContentType() {
        return contentType;
    }
}
