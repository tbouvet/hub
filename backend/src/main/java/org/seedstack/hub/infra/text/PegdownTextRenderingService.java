/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.text;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.seedstack.hub.domain.services.text.TextRenderingService;

import javax.inject.Named;

@Named("markdown")
public class PegdownTextRenderingService implements TextRenderingService {
    @Override
    public String renderToHtml(String rawText) {
        PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.AUTOLINKS |
                Extensions.TABLES |
                Extensions.SMARTS |
                Extensions.QUOTES |
                Extensions.STRIKETHROUGH |
                Extensions.ATXHEADERSPACE |
                Extensions.TASKLISTITEMS |
                Extensions.EXTANCHORLINKS
        );

        return pegDownProcessor.markdownToHtml(rawText);
    }
}
