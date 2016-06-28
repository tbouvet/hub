/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import com.google.common.collect.Sets;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;
import java.util.Set;

public class MockLifecycleListener implements LifecycleListener {
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
        userRepository.persist(new User(new UserId("admin"), "admin@email.com"));
        userRepository.persist(new User(new UserId("user1"), "user1@email.com"));
        userRepository.persist(new User(new UserId("user2"), "user2@email.com"));
        userRepository.persist(new User(new UserId("user3"), "user3@email.com"));
        User simple = new User(new UserId("simple"), "simple@email.com");
        simple.addEmail("simple@email.org");
        userRepository.persist(simple);

        Set<UserId> owners = Sets.newHashSet(new UserId("user2"));
        Organisation organisation = new Organisation(new OrganisationId("@seedstack"), "SeedStack", owners);
        organisation.addMember(new UserId("admin"));
        organisationRepository.persist(organisation);
    }

    @Override
    public void stopping() {
        componentRepository.clear();
        documentRepository.clear();
        organisationRepository.clear();
        userRepository.clear();
    }
}
