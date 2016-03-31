/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.services.fetch.SourceType;

import java.net.MalformedURLException;
import java.net.URL;

public class Source extends BaseValueObject {
    private SourceType sourceType;
    private String url;

    public Source(SourceType sourceType, URL url) {
        this.sourceType = sourceType;
        this.url = url.toString();
    }
    public Source(SourceType sourceType, String url) {
        this.sourceType = sourceType;
        this.url = url;
    }

    private Source() {
    }

    public SourceType getSourceType() {
        return sourceType;
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
