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
