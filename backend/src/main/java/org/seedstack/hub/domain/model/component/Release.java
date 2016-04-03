/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static org.seedstack.hub.rest.shared.Dates.asDate;
import static org.seedstack.hub.rest.shared.Dates.asLocalDateTime;

@Embedded
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

    public LocalDateTime getDate() {
        return asLocalDateTime(date);
    }

    public void setDate(LocalDateTime date) {
        this.date = asDate(date);
    }

    public void setPublicationDate(String publicationDate) {
        try {
            this.date = asDate(LocalDateTime.parse(publicationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (DateTimeParseException e1) {
            try {
                this.date = asDate(LocalDate.parse(publicationDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay());
            } catch (DateTimeParseException e2) {
                throw new ComponentException("Invalid date " + publicationDate, e2);
            }
        }
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
