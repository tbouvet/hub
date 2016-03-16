/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.text;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.seedstack.hub.domain.services.text.TextFormatService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

abstract class AbstractTextFormatService implements TextFormatService {
    protected String cleanHtml(String rawHtml) {
        return Jsoup.clean(rawHtml, Whitelist.relaxed());
    }

    protected boolean isRelative(String rawPath) {
        try {
            URL src = new URL(rawPath);
            String path = src.getPath();
            return (src.getProtocol() == null || src.getProtocol().isEmpty()) &&
                    !path.startsWith("/") &&
                    !new File(path).toPath().normalize().toString().contains("..");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
