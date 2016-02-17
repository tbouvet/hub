package org.seedstack.hub.domain.model.document;

import org.seedstack.business.domain.GenericFactory;

import java.nio.charset.Charset;

public interface DocumentFactory extends GenericFactory<Document> {
    BinaryDocument createBinaryDocument(String contentType);

    TextDocument createTextDocument(TextFormat textFormat, Charset charset);

    TextDocument createTextDocument(TextFormat textFormat);

    TextDocument createMarkdownDocument(Charset charset);

    TextDocument createAsciiDocDocument(Charset charset);

    TextDocument createHTMLDocDocument(Charset charset);
}
