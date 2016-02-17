/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;

import java.util.ArrayList;
import java.util.List;

public class Description extends BaseValueObject {
    private final DocumentId summary;
    private final DocumentId icon;
    private final DocumentId readme;
    private final List<DocumentId> images = new ArrayList<>();
    private final List<UserId> maintainers = new ArrayList<>();

    public Description(DocumentId summary, DocumentId icon, DocumentId readme) {
        this.summary = summary;
        this.readme = readme;
        this.icon = icon;
    }

    public DocumentId getSummary() {
        return summary;
    }

    public List<DocumentId> getImages() {
        return images;
    }

    public DocumentId getReadme() {
        return readme;
    }

    public DocumentId getIcon() {
        return icon;
    }

}
