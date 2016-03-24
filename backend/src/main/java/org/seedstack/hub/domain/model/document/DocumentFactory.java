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

import java.io.File;
import java.util.stream.Stream;

public interface DocumentFactory extends GenericFactory<Document> {
    Document createDocument(DocumentId documentId, File directory);

    Document createTextDocument(DocumentId documentId, TextFormat textFormat, File file);

    Document createTextDocument(DocumentId documentId, TextFormat textFormat, String body);

    BinaryDocument createBinaryDocument(DocumentId documentId, File file);

    Stream<Document> createDocuments(Component component, File directory);
}
