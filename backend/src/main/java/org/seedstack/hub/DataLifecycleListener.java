/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub;

import com.google.common.collect.Sets;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;
import java.util.Set;

public class DataLifecycleListener implements LifecycleListener {

    @Inject
    private UserRepository userRepository;
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;

    @Override
    public void started() {
        userRepository.persist(new User(new UserId("adrienlauer"), "adrien.lauer@mpsa.com"));
        userRepository.persist(new User(new UserId("pith"), "pierre.thirouin@ext.mpsa.com"));
        userRepository.persist(new User(new UserId("kavi87"), "kavi.ramyead@ext.mpsa.com"));

        User simple = new User(new UserId("simple"), "simple@ext.mpsa.com");
        simple.addEmail("simple@gmail.com");
        userRepository.persist(simple);

        userRepository.persist(new User(new UserId("admin"), "admin@ext.mpsa.com"));

        Set<UserId> owners = Sets.newHashSet(new UserId("adrienlauer"));
        Organisation organisation = new Organisation(new OrganisationId("@seedstack"), "SeedStack", owners);
        organisation.addMember(new UserId("admin"));
        organisationRepository.persist(organisation);
    }

    @Override
    public void stopping() {
    }
}
