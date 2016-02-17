/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.text;

import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.hub.domain.model.document.TextDocument;

import javax.inject.Inject;

public class TextServiceImpl implements TextService {
    @Inject
    DomainRegistry domainRegistry;

    @Override
    public String renderToHtml(TextDocument textDocument) {
        return domainRegistry.getService(
                TextRenderingService.class,
                textDocument.getFormat().getTextRenderingServiceQualifier()
        ).renderToHtml(textDocument.getText());
    }
}
