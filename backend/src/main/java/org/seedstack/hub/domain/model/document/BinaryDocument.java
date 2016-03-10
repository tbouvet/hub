/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

public class BinaryDocument extends Document {
    private byte[] data;

    BinaryDocument(DocumentId documentId, String contentType) {
        super(documentId, contentType);
    }

    private BinaryDocument() {
        // required by morphia
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("BinaryDocument{id=%s, contentType=%s, size=%d}", getDocumentId(), getContentType(), data.length);
    }
}
