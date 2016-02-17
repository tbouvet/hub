/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
