/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.component.detail;

import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.shared.Dates;

import java.util.Date;

public class ReleaseRepresentation {
    private String version;
    private Date date;
    private String url;

    ReleaseRepresentation() {
    }

    ReleaseRepresentation(Release release) {
        this.version = release.getVersion().toString();
        if (release.getDate() != null) {
            this.date = Dates.asDate(release.getDate());
        }
        if (release.getUrl() != null) {
            this.url = release.getUrl().toString();
        }
    }



    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
