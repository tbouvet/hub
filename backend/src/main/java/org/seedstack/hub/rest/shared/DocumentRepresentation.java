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
    private String title;

    public DocumentRepresentation(DocumentId documentId, RelRegistry relRegistry) {
        this.title = buildTitle(documentId);
        switch (documentId.getScope()) {
            case FILES:
                self(relRegistry
                        .uri(Rels.FILES)
                        .set("componentId", documentId.getComponentId().getName())
                        .set("filePath", documentId.getPath()));
                break;
            case WIKI:
                self(relRegistry
                        .uri(Rels.WIKI)
                        .set("componentId", documentId.getComponentId().getName())
                        .set("page", documentId.getPath()));
                break;
        }
    }

    public DocumentRepresentation() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String buildTitle(DocumentId documentId) {
        String path = documentId.getPath();
        if (path == null) {
            return "Untitled";
        }

        int lastSlash = path.lastIndexOf('/');
        if (lastSlash != -1) {
            path = path.substring(lastSlash + (lastSlash == path.length() - 1 ? 0 : 1));
        }

        int lastDot = path.lastIndexOf('.');
        if (lastDot != -1) {
            path = path.substring(0, lastDot);
        }

        if (path.length() > 0) {
            path = path.substring(0, 1).toUpperCase() + path.substring(1);
        } else {
            path = "Untitled";
        }

        return path;
    }
}
