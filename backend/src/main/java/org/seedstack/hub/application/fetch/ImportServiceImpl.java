/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.fetch;

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
import org.seedstack.seed.Logging;
import org.slf4j.Logger;

import javax.inject.Inject;

class ImportServiceImpl implements ImportService {

    @Logging
    private Logger logger;

    @Inject
    private DomainRegistry domainRegistry;
    @Inject
    private SecurityService securityService;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private Repository<Document, DocumentId> documentRepository;

    @Override
    public Component importComponent(Source source) {
        FetchService fetchService = domainRegistry.getService(FetchService.class, source.getVcsType().qualifier());
        FetchResult result = fetchService.fetch(source);
        try {
            checkCurrentUserOwns(result.getComponent());
            componentRepository.persist(result.getComponent());
            result.getDocuments().forEach(documentRepository::persist);
        } finally {
            fetchService.clean();
        }
        return result.getComponent();
    }

    private void checkCurrentUserOwns(Component component) {
        if (!securityService.isOwnerOf(component)) {
            throw new ImportException("Authenticated user is not the owner of component");
        }
    }
}
