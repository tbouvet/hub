/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import java.nio.charset.Charset;

public class TextDocument extends Document {
    private final TextFormat format;
    private final Charset charset;
    private String text;

    public TextDocument(DocumentId documentId, TextFormat format) {
        this(documentId, format, Charset.forName("UTF-8"));
    }

    public TextDocument(DocumentId documentId, TextFormat format, Charset charset) {
        super(documentId, String.format("%s; charset=%s", format.getContentType(), charset.name()));
        this.format = format;
        this.charset = charset;
    }

    public TextFormat getFormat() {
        return format;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Charset getCharset() {
        return charset;
    }
}
