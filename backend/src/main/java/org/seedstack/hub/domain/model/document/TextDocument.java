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

/**
 * A document containing textual data in one of the supported {@link TextFormat}.
 */
public class TextDocument extends Document {
    @NotNull
    private TextFormat format;
    @NotNull
    private String charset;
    private String text;

    /**
     * Creates a text document with a specified identifier and {@link TextFormat}.
     *
     * @param documentId the document identifier.
     * @param format     the text format.
     */
    public TextDocument(DocumentId documentId, TextFormat format) {
        this(documentId, format, Charset.forName("UTF-8"));
    }

    /**
     * Creates a text document with a specified identifier, {@link TextFormat} and {@link Charset}.
     *
     * @param documentId the document identifier.
     * @param format     the text format.
     * @param charset    the charset of the textual data.
     */
    public TextDocument(DocumentId documentId, TextFormat format, Charset charset) {
        super(documentId, String.format("%s; charset=%s", format.contentType(), charset.name()));
        this.format = format;
        this.charset = charset.name();
    }

    protected TextDocument() {
        // required by morphia
    }

    /**
     * @return the format of the document.
     */
    public TextFormat getFormat() {
        return format;
    }

    /**
     * @return the textual data of the document.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the textual data of the document.
     *
     * @param text the textual data.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the charset of the document.
     */
    public Charset getCharset() {
        return Charset.forName(charset);
    }

    @Override
    public String toString() {
        return String.format("TextDocument{id=%s, format=%s, charset=%s, size='%d'}", getId(), format, charset, text == null ? 0 : text.length());
    }
}
