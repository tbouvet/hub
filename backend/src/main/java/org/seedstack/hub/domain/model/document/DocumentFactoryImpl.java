/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.seedstack.business.domain.BaseFactory;
import org.seedstack.hub.domain.model.component.Component;

import java.nio.charset.Charset;

public class DocumentFactoryImpl extends BaseFactory<Document> implements DocumentFactory {
    @Override
    public BinaryDocument createBinaryDocument(Component component, String path, String contentType) {
        return new BinaryDocument(new DocumentId(component.getComponentId(), path), contentType);
    }

    @Override
    public TextDocument createTextDocument(Component component, String path, TextFormat textFormat, Charset charset) {
        return new TextDocument(new DocumentId(component.getComponentId(), path), textFormat, charset);
    }

    @Override
    public TextDocument createTextDocument(Component component, String path, TextFormat textFormat) {
        return new TextDocument(new DocumentId(component.getComponentId(), path), textFormat);
    }

    @Override
    public TextDocument createMarkdownDocument(Component component, String path, Charset charset) {
        return createTextDocument(component, path, InternallySupportedTextFormat.MARKDOWN, charset);
    }

    @Override
    public TextDocument createAsciiDocDocument(Component component, String path, Charset charset) {
        return createTextDocument(component, path, InternallySupportedTextFormat.MARKDOWN, charset);
    }

    @Override
    public TextDocument createHTMLDocDocument(Component component, String path, Charset charset) {
        return createTextDocument(component, path, InternallySupportedTextFormat.HTML, charset);
    }
}
