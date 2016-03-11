/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.text;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.services.text.TextFormatService;

import javax.inject.Named;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Named("html")
public class HtmlTextFormatService implements TextFormatService {
    @Override
    public String renderHtml(TextDocument textDocument) {
        return cleanHtml(textDocument.getText());
    }

    @Override
    public Set<DocumentId> findRelativeReferences(TextDocument textDocument) {
        return Jsoup.parse(cleanHtml(textDocument.getText()))
                .getElementsByTag("img")
                .stream()
                .map(Element::toString)
                .filter(this::isRelative)
                .map(path -> new DocumentId(textDocument.getId(), path))
                .collect(toSet());
    }

    static String cleanHtml(String rawHtml) {
        return rawHtml;
    }
}
