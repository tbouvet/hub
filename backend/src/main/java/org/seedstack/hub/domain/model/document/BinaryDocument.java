/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

/**
 * A document type containing arbitrary binary data.
 */
public class BinaryDocument extends Document {
    private byte[] data;

    /**
     * Creates a binary document with the specified identifier and content type.
     *
     * @param documentId  the document identifier.
     * @param contentType the content type.
     */
    public BinaryDocument(DocumentId documentId, String contentType) {
        super(documentId, contentType);
    }

    private BinaryDocument() {
        // required by morphia
    }

    /**
     * @return the binary data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the binary data of the document.
     *
     * @param data the binary data.
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("BinaryDocument{id=%s, contentType=%s, size=%d}", getId(), getContentType(), data.length);
    }
}
