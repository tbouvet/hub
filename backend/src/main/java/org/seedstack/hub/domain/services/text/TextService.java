/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.text;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextDocument;

import java.io.File;
import java.util.Set;

@Service
public interface TextService {
    String renderHtml(TextDocument textDocument);

    DocumentId findTextDocument(ComponentId componentId, File componentDirectory, String name);

    Set<DocumentId> findReferences(TextDocument textDocument);
}
