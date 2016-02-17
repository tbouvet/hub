package org.seedstack.hub.domain.model.document;

import org.seedstack.business.domain.BaseFactory;

import java.nio.charset.Charset;
import java.util.UUID;

public class DocumentFactoryImpl extends BaseFactory<Document> implements DocumentFactory {
    @Override
    public BinaryDocument createBinaryDocument(String contentType) {
        return new BinaryDocument(new DocumentId(UUID.randomUUID().toString()), contentType);
    }

    @Override
    public TextDocument createTextDocument(TextFormat textFormat, Charset charset) {
        return new TextDocument(new DocumentId(UUID.randomUUID().toString()), textFormat, charset);
    }

    @Override
    public TextDocument createTextDocument(TextFormat textFormat) {
        return new TextDocument(new DocumentId(UUID.randomUUID().toString()), textFormat);
    }

    @Override
    public TextDocument createMarkdownDocument(Charset charset) {
        return createTextDocument(InternallySupportedTextFormat.MARKDOWN, charset);
    }

    @Override
    public TextDocument createAsciiDocDocument(Charset charset) {
        return createTextDocument(InternallySupportedTextFormat.MARKDOWN, charset);
    }

    @Override
    public TextDocument createHTMLDocDocument(Charset charset) {
        return createTextDocument(InternallySupportedTextFormat.HTML, charset);
    }
}
