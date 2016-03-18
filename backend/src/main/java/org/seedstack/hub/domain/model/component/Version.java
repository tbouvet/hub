/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Version extends BaseEntity<VersionId> implements Comparable<Version> {
    private VersionId versionId;
    private Date publicationDate;
    private String url;

    public Version(VersionId versionId) {
        this.versionId = versionId;
    }

    private Version() {
        // required by morphia
    }

    @Override
    public VersionId getEntityId() {
        return versionId;
    }

    public VersionId getId() {
        return versionId;
    }

    public LocalDate getPublicationDate() {
        return asLocalDate(publicationDate);
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = asDate(publicationDate);
    }

    public void setPublicationDate(String publicationDate) {
        try {
            this.publicationDate = asDate(LocalDate.parse(publicationDate, DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (DateTimeParseException e) {
            throw new ComponentException("Invalid publication date " + publicationDate, e);
        }
    }

    // Constructor LocalDate() required by Morphia does not exist, we use conversion from/to java.util.Date
    private LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
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

    @Override
    public String toString() {
        return versionId.toString();
    }
}
