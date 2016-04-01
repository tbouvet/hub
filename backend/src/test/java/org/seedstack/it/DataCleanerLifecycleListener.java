/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;

public class DataCleanerLifecycleListener implements LifecycleListener {

    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private Repository<Document, DocumentId> documentRepository;
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;
    @Inject
    private UserRepository userRepository;

    @Override
    public void started() {
    }

    @Override
    public void stopping() {
        componentRepository.clear();
        documentRepository.clear();
        organisationRepository.clear();
        userRepository.clear();
    }
}
