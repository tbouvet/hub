/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.text;

import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.hub.domain.model.document.InternallySupportedTextFormat;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.model.document.TextFormat;

import javax.inject.Inject;
import java.io.File;

public class TextServiceImpl implements TextService {
    @Inject
    private DomainRegistry domainRegistry;

    @Override
    public String renderHtml(TextDocument textDocument) throws TextProcessingException {
        return domainRegistry.getService(
                TextRenderingService.class,
                textDocument.getFormat().getTextRenderingServiceQualifier()
        ).renderHtml(textDocument.getText());
    }

    @Override
    public File findTextDocument(File directory, String name) throws TextProcessingException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                for (TextFormat textFormat : InternallySupportedTextFormat.values()) {
                    for (String validExtension : textFormat.getValidExtensions()) {
                        if (String.format("%s.%s", name, validExtension).equalsIgnoreCase(file.getName())) {
                            return file;
                        }
                    }
                }
            }
        } else {
            throw new TextProcessingException("Unable to list files in directory " + directory.getAbsolutePath());
        }

        return null;
    }
}
