/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.fetch;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.hub.domain.services.text.TextService;

import java.net.URL;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class ImportServiceImplTest {

    private URL url;
    private VCSType vcsType = VCSType.GIT;

    @Tested
    private ImportServiceImpl underTest;

    @Injectable
    private ManifestParser manifestParser;
    @Injectable
    private TextService textService;
    @Injectable
    private DomainRegistry domainRegistry;
    @Injectable
    private ComponentFactory componentFactory;
    @Injectable
    private DocumentFactory documentFactory;
    @Injectable
    private SecurityService securityService;
    @Injectable
    private Repository<Component, ComponentId> componentRepository;
    @Injectable
    private Repository<Document, DocumentId> documentRepository;

    @Mocked
    private FetchService fetchService;
    @Mocked
    private Component component;
    @Mocked
    private Document document;

    @Before
    public void setUp() throws Exception {
        url = new URL("https://github.com/seedstack/seed.git");

        new Expectations() {{
            domainRegistry.getService(FetchService.class, vcsType.qualifier());
            result = fetchService;
        }};
    }

    @Test
    public void testImportComponentWithDocuments() throws Exception {
        givenComponent();
        givenDocuments();
        givenIsOwner(true);

        Component component = underTest.importComponent(new Source(vcsType, url));

        assertThat(component).isNotNull();

        new Verifications() {{
            componentRepository.persist(withNotNull());
            times = 1;
            documentRepository.persist(withNotNull());
            times = 2;
        }};
    }

    private void givenComponent() {
        new Expectations() {{
            componentFactory.createComponent(withNotNull());
            result = component;
        }};
    }

    private void givenDocuments() {
        new Expectations() {{
            documentFactory.createDocuments(withNotNull(), withNotNull());
            result = Stream.of(document, document);
        }};
    }

    private void givenIsOwner(boolean isOwner) {
        new Expectations() {{
            securityService.isOwnerOf(component);
            result = isOwner;
        }};
    }

    @Test(expected = ImportException.class)
    public void testOnlyOwnerCanImport() throws Exception {
        givenComponent();
        givenIsOwner(false);

        underTest.importComponent(new Source(vcsType, url));
    }
}
