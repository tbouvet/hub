/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

public enum TextFormat {
    PLAIN("text/plain", "txt"),
    MARKDOWN("text/markdown", "md"),
    ASCIIDOC("text/asciidoc", "adoc", "asciidoc"),
    HTML("text/html", "htm", "html");

    private final String contentType;
    private final String[] validExtensions;

    TextFormat(String contentType, String... validExtensions) {
        this.contentType = contentType;
        this.validExtensions = validExtensions;
    }

    public String[] validExtensions() {
        return validExtensions;
    }

    public String contentType() {
        return contentType;
    }

    public String qualifier() {
        return this.name().toLowerCase();
    }

    public static TextFormat of(String textFormat) {
        for (TextFormat format : values()) {
            if (format.contentType().equals("text/" + textFormat)) {
                return format;
            }
        }

        throw new IllegalArgumentException("Unsupported text format " + textFormat);
    }
}
