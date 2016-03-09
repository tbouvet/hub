/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.junit.Test;
import org.seedstack.hub.domain.model.component.ComponentId;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentTest {
    public static final String IMAGE_PNG = "image/png";
    public static final String TEXT_MARKDOWN = "text/markdown";
    public static final String INVALID_CONTENT_TYPE = "image";

    @Test
    public void simple_content_type_is_accepted() {
        BinaryDocument binaryDocument = new BinaryDocument(createDocumentId(), IMAGE_PNG);
        assertThat(binaryDocument.getContentType()).isEqualTo(IMAGE_PNG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_content_type_is_rejected() {
        new BinaryDocument(createDocumentId(), INVALID_CONTENT_TYPE);
    }

    @Test
    public void parametered_content_type_is_accepted() {
        TextDocument textDocument = new TextDocument(createDocumentId(), TextFormat.MARKDOWN, Charset.forName("iso-8859-1"));
        assertThat(textDocument.getContentType()).isEqualTo(String.format("%s; charset=ISO-8859-1", TEXT_MARKDOWN));
    }

    @Test
    public void default_charset_is_utf8() {
        TextDocument textDocument = new TextDocument(createDocumentId(), TextFormat.MARKDOWN);
        assertThat(textDocument.getContentType()).isEqualTo(String.format("%s; charset=UTF-8", TEXT_MARKDOWN));
    }

    private DocumentId createDocumentId() {
        return new DocumentId(new ComponentId("c1"), "/path");
    }
}
