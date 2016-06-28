/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.fetch.Manifest;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.security.WithUser;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class DocumentIT {
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private DocumentFactory documentFactory;
    private File directory;
    private Manifest manifest;

    @Before
    public void setUp() throws Exception {
        directory = new File("src/test/resources/components/component1");
        manifest = new Manifest();
        manifest.setId("component1");
        manifest.setOwner("user3@email.com");
    }

    @Test
    @WithUser(id = "user2", password = "password")
    public void stream_documents_from_component() throws Exception {
        manifest.setReadme("README.md");
        manifest.setIcon("images/icon.png");
        manifest.setDocs(Lists.newArrayList(
                "docs/intro.md", "docs/integration.md", "docs/usage.md",
                "images/icon.png", "images/screenshot-1.png", "images/screenshot-2.png",
                "images/screenshot-3.png"));

        Component component = componentFactory.createComponent(manifest);
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

    @Test
    @WithUser(id = "user2", password = "password")
    public void stream_documents_from_component_without_docs() throws Exception {
        Component component = componentFactory.createComponent(manifest);
        Map<DocumentId, Document> documents = documentFactory.createDocuments(component, directory).collect(toMap(Document::getId, Function.identity()));
        assertThat(documents).isNotNull();
    }

    private DocumentId buildDocumentId(String componentId, String path) {
        return new DocumentId(new ComponentId(componentId), DocumentScope.FILES, path);
    }
}
