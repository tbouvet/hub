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
