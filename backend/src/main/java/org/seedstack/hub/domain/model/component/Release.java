/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Release extends BaseEntity<Version> implements Comparable<Release> {
    @NotNull
    private Version version;
    private Date date;
    private String url;

    public Release(Version version) {
        this.version = version;
    }

    private Release() {
        // required by morphia
    }

    @Override
    public Version getEntityId() {
        return version;
    }

    public Version getVersion() {
        return version;
    }

    public LocalDate getDate() {
        return asLocalDate(date);
    }

    public void setDate(LocalDate date) {
        this.date = asDate(date);
    }

    public void setPublicationDate(String publicationDate) {
        try {
            this.date = asDate(LocalDate.parse(publicationDate, DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (DateTimeParseException e) {
            throw new ComponentException("Invalid date " + publicationDate, e);
        }
    }

    // Constructor LocalDate() required by Morphia does not exist, we use conversion from/to java.util.Date
    public LocalDate asLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Date asDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setUrl(URL url) {
        this.url = url.toString();
    }

    @Override
    public int compareTo(Release o) {
        return version.compareTo(o.version);
    }

    @Override
    public String toString() {
        return version.toString();
    }
}
