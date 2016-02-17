package org.seedstack.hub.domain.model.document;

public enum InternallySupportedTextFormat implements TextFormat {
    MARKDOWN("text/markdown"),
    ASCIIDOC("text/asciidoc"),
    HTML("text/html");

    private final String contentType;

    InternallySupportedTextFormat(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTextRenderingServiceQualifier() {
        return this.name().toLowerCase();
    }
}
