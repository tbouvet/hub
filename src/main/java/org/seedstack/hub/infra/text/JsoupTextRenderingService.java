package org.seedstack.hub.infra.text;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.seedstack.hub.domain.services.text.TextRenderingService;

import javax.inject.Named;

@Named("html")
public class JsoupTextRenderingService implements TextRenderingService {
    @Override
    public String renderToHtml(String rawText) {
        return Jsoup.clean(rawText, Whitelist.basic());
    }
}
