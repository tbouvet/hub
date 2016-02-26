/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.seedstack.business.domain.GenericFactory;
import org.seedstack.hub.domain.model.component.Component;

import java.nio.charset.Charset;

public interface DocumentFactory extends GenericFactory<Document> {
    BinaryDocument createBinaryDocument(Component component, String path, String contentType);

    TextDocument createTextDocument(Component component, String path, TextFormat textFormat, Charset charset);

    TextDocument createTextDocument(Component component, String path, TextFormat textFormat);

    TextDocument createMarkdownDocument(Component component, String path, Charset charset);

    TextDocument createAsciiDocDocument(Component component, String path, Charset charset);

    TextDocument createHTMLDocDocument(Component component, String path, Charset charset);
}
