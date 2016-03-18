/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.hibernate.validator.constraints.NotBlank;
import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.rest.shared.UriBuilder;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

@Embedded
public class DocumentId extends BaseValueObject {
    @NotNull
    private ComponentId componentId;
    @NotBlank
    private String path;

    public DocumentId(ComponentId componentId, String path) {
        this.componentId = componentId;
        this.path = path;
    }

    public DocumentId(DocumentId documentId, String path) {
        this.componentId = documentId.componentId;
        try {
            this.path = new URL(new URL(documentId.path), path).getPath();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to build a document id from " + documentId.getPath() + " and " + path);
        }
    }

    private DocumentId() {
        // required by morphia
    }

    public ComponentId getComponentId() {
        return componentId;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return UriBuilder.uri(componentId.toString(), path);
    }
}
