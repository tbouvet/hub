package org.seedstack.hub.domain.services.text;

import org.seedstack.business.Service;

@Service
public interface TextRenderingService {
    String renderToHtml(String rawText);
}
