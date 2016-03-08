/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseEntity;

import java.time.LocalDate;

public class Version extends BaseEntity<VersionId> implements Comparable<Version> {
    private final VersionId versionId;
    private LocalDate publicationDate;
    private String url;

    public Version(VersionId versionId) {
        this.versionId = versionId;
    }

    @Override
    public VersionId getEntityId() {
        return versionId;
    }

    public VersionId getVersionId() {
        return versionId;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int compareTo(Version o) {
        return versionId.compareTo(o.versionId);
    }
}
