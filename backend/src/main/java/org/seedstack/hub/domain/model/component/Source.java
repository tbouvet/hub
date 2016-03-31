/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.services.fetch.VCSType;

import java.net.MalformedURLException;
import java.net.URL;

public class Source extends BaseValueObject {
    private VCSType vcsType;
    private String url;

    public Source(VCSType vcsType, URL url) {
        this.vcsType = vcsType;
        this.url = url.toString();
    }
    public Source(VCSType vcsType, String url) {
        this.vcsType = vcsType;
        this.url = url;
    }

    private Source() {
    }

    public VCSType getVcsType() {
        return vcsType;
    }

    public String getUrl() {
        return url;
    }

    public URL getActualUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
