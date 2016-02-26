/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.model.component.ComponentId;

@Embedded
public class DocumentId extends BaseValueObject {
    private ComponentId componentId;
    private String path;

    public DocumentId(ComponentId componentId, String path) {
        this.componentId = componentId;
        this.path = path;
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
}
