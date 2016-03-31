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
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.services.fetch.FetchResult;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.hub.domain.services.fetch.VCSType;

import java.net.URL;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class ImportServiceImplTest {

    private URL url;
    private VCSType vcsType = VCSType.GIT;
    private Source source;

    @Tested
    private ImportServiceImpl underTest;

    @Injectable
    private DomainRegistry domainRegistry;
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
        source = new Source(vcsType, url);

        new Expectations() {{
            domainRegistry.getService(FetchService.class, vcsType.qualifier());
            result = fetchService;
        }};
    }

    @Test
    public void testImportComponentWithDocuments() throws Exception {
        givenFetchResult(source);
        givenIsOwner(true);

        Component component = underTest.importComponent(source);

        assertThat(component).isNotNull();

        new Verifications() {{
            componentRepository.persist(withNotNull());
            times = 1;
            documentRepository.persist(withNotNull());
            times = 2;
        }};
    }

    private void givenFetchResult(Source source) {
        new Expectations() {{
            fetchService.fetch(source);
            result = new FetchResult(component, Stream.of(document, document));
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
        givenFetchResult(source);
        givenIsOwner(false);

        underTest.importComponent(new Source(vcsType, url));
    }
}
