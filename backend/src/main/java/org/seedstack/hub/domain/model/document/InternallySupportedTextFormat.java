/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

public enum InternallySupportedTextFormat implements TextFormat {
    MARKDOWN("text/markdown", "md"),
    ASCIIDOC("text/asciidoc", "adoc", "asciidoc"),
    HTML("text/html", "htm", "html");

    private final String contentType;
    private final String[] validExtensions;

    InternallySupportedTextFormat(String contentType, String... validExtensions) {
        this.contentType = contentType;
        this.validExtensions = validExtensions;
    }


    @Override
    public String[] getValidExtensions() {
        return validExtensions;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTextRenderingServiceQualifier() {
        return this.name().toLowerCase();
    }
}
