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

import org.seedstack.business.domain.BaseAggregateRoot;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public abstract class Document extends BaseAggregateRoot<DocumentId> {
    private final DocumentId documentId;
    private final MimeType mimeType;

    public Document(DocumentId documentId, String contentType) {
        this.documentId = documentId;
        try {
            this.mimeType = new MimeType(contentType);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException("Provided content type is invalid: " + contentType, e);
        }
    }

    @Override
    public DocumentId getEntityId() {
        return documentId;
    }

    public DocumentId getDocumentId() {
        return documentId;
    }

    public String getContentType() {
        return mimeType.toString();
    }
}
