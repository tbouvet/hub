/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;

public class TextDocument extends Document {
    @NotNull
    private TextFormat format;
    @NotNull
    private Charset charset;
    private String text;

    TextDocument(DocumentId documentId, TextFormat format) {
        this(documentId, format, Charset.forName("UTF-8"));
    }

    TextDocument(DocumentId documentId, TextFormat format, Charset charset) {
        super(documentId, String.format("%s; charset=%s", format.getContentType(), charset.name()));
        this.format = format;
        this.charset = charset;
    }

    private TextDocument() {
        // required by morphia
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

    @Override
    public String toString() {
        return String.format("TextDocument{id=%s, format=%s, charset=%s, size='%d'}", getDocumentId(), format, charset, text.length());
    }
}
