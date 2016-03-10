/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.it;

import org.junit.Test;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.seed.security.WithUser;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

public class DocumentIT extends AbstractSeedIT {
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private DocumentFactory documentFactory;

    @Test
    @WithUser(id = "adrienlauer", password = "password")
    public void stream_documents_from_imported_component() throws Exception {
        File directory = new File("src/test/resources/components/component1");
        Component component = componentFactory.createComponent(directory);
        Map<DocumentId, Document> documents = documentFactory.createDocuments(component, directory).collect(toMap(Document::getId, Function.identity()));

        assertThat(documents.get(buildDocumentId("component1", "README.md"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "docs/intro.md"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "docs/integration.md"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "docs/usage.md"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "images/icon.png"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "images/screenshot-1.png"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "images/screenshot-2.png"))).isNotNull();
        assertThat(documents.get(buildDocumentId("component1", "images/screenshot-3.png"))).isNotNull();
    }


    private DocumentId buildDocumentId(String componentId, String path) {
        return new DocumentId(new ComponentId(componentId), path);
    }
}
