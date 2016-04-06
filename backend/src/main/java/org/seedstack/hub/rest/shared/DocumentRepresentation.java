/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.shared;

import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.rest.Rels;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

public class DocumentRepresentation extends HalRepresentation {

    public DocumentRepresentation(DocumentId documentId, RelRegistry relRegistry) {
        switch (documentId.getScope()) {
            case FILES:
                self(relRegistry
                        .uri(Rels.FILES)
                        .set("componentId", documentId.getComponentId().getName())
                        .set("filePath", documentId.getPath()));
                break;
            case WIKI:
                self(relRegistry
                        .uri(Rels.WIKI_PAGES)
                        .set("componentId", documentId.getComponentId().getName())
                        .set("page", documentId.getPath()));
                break;
        }
    }

    public DocumentRepresentation() {
    }
}
