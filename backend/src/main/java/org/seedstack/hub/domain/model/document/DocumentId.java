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

/**
 * Identifies a document globally in the hub.
 */
@Embedded
public class DocumentId extends BaseValueObject {
    @NotNull
    private ComponentId componentId;
    @NotNull
    private DocumentScope scope;
    @NotBlank
    private String path;

    /**
     * Creates a document identifier.
     *
     * @param componentId the component the document identified by this identifier is attached to.
     * @param scope       the scope of the document (attached file, wiki, ...).
     * @param path        the relative path of the document.
     */
    public DocumentId(ComponentId componentId, DocumentScope scope, String path) {
        this.componentId = componentId;
        this.scope = scope;
        this.path = path;
    }

    /**
     * Creates a document identifier by concatenating the path of another document identifier. The component identifier
     * and the scope are copied as-is.
     *
     * @param documentId the base document identifier.
     * @param path       the path suffix to concatenate to the path of the base document identifier.
     */
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

    /**
     * @return the component identifier the document identified by this identifier is attached to.
     */
    public ComponentId getComponentId() {
        return componentId;
    }

    /**
     * @return the document scope.
     */
    public DocumentScope getScope() {
        return scope;
    }

    /**
     * @return the relative path of the document.
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return UriBuilder.uri(componentId.toString(), path);
    }
}
