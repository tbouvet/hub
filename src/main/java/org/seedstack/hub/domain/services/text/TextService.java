package org.seedstack.hub.domain.services.text;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.document.TextDocument;

@Service
public interface TextService {
    String renderToHtml(TextDocument textDocument);
}
