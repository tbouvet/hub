/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.text;

import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.model.document.TextFormat;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class TextServiceImpl implements TextService {
    @Inject
    private DomainRegistry domainRegistry;

    @Override
    public String renderHtml(TextDocument textDocument) {
        return getTextFormatService(textDocument).renderHtml(textDocument);
    }

    @Override
    public DocumentId findTextDocument(ComponentId componentId, File componentDirectory, String name) {
        File[] files = componentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    for (TextFormat textFormat : TextFormat.values()) {
                        for (String validExtension : textFormat.getValidExtensions()) {
                            if (String.format("%s.%s", name, validExtension).equalsIgnoreCase(file.getName())) {
                                return new DocumentId(componentId, file.getName());
                            }
                        }
                    }
                }
            }
        } else {
            throw new TextProcessingException("Unable to list files in directory " + componentDirectory.getAbsolutePath());
        }

        return null;
    }

    @Override
    public Set<DocumentId> findReferences(TextDocument textDocument) {
        return getTextFormatService(textDocument)
                .findRelativeReferences(textDocument)
                .stream()
                .collect(toSet());
    }

    private TextFormatService getTextFormatService(TextDocument textDocument) {
        return domainRegistry.getService(
                TextFormatService.class,
                textDocument.getFormat().getTextRenderingServiceQualifier()
        );
    }
}
