package org.seedstack.hub.domain.model.document;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentTest {
    public static final String IMAGE_PNG = "image/png";
    public static final String TEXT_MARKDOWN = "text/markdown";
    public static final String INVALID_CONTENT_TYPE = "image";

    @Test
    public void simple_content_type_is_accepted() {
        BinaryDocument binaryDocument = new BinaryDocument(new DocumentId("1"), IMAGE_PNG);
        assertThat(binaryDocument.getContentType()).isEqualTo(IMAGE_PNG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_content_type_is_rejected() {
        new BinaryDocument(new DocumentId("1"), INVALID_CONTENT_TYPE);
    }

    @Test
    public void parametered_content_type_is_accepted() {
        TextDocument textDocument = new TextDocument(new DocumentId("1"), InternallySupportedTextFormat.MARKDOWN, Charset.forName("iso-8859-1"));
        assertThat(textDocument.getContentType()).isEqualTo(String.format("%s; charset=ISO-8859-1", TEXT_MARKDOWN));
    }

    @Test
    public void default_charset_is_utf8() {
        TextDocument textDocument = new TextDocument(new DocumentId("1"), InternallySupportedTextFormat.MARKDOWN);
        assertThat(textDocument.getContentType()).isEqualTo(String.format("%s; charset=UTF-8", TEXT_MARKDOWN));
    }
}
