/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.OrganisationService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.hub.MockBuilder;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.security.AuthorizationException;
import org.seedstack.seed.security.WithUser;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class OrganisationServiceIT {

    @Inject
    private OrganisationService organisationService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;

    private UserId admin;
    private UserId john;
    private OrganisationId seedStack;
    private ComponentId component0;

    @Before
    public void setUp() throws Exception {
        admin = new UserId("admin");
        john = new UserId("john");
        seedStack = new OrganisationId("@seedstack");
        component0 = new ComponentId("Component0");

        userRepository.persist(new User(admin, "john@gmail.com"));
        userRepository.persist(new User(john, "john@gmail.com"));

        componentRepository.persist(MockBuilder.mock(0));

        organisationRepository.persist(new Organisation(seedStack, "SeedStack", Sets.newHashSet(admin)));
    }

    @WithUser(id = "admin", password = "password")
    @Test
    public void manipulateOrganisation() {
        organisationService.addMember(seedStack, john);
        organisationService.addComponent(seedStack, component0);

        Organisation organisation = organisationRepository.load(seedStack);
        assertThat(organisation.getComponents()).containsExactly(component0);
        assertThat(organisation.getOwners()).containsExactly(admin);
        assertThat(organisation.getMembers()).containsExactly(john);
        assertThat(organisation.isMember(john)).isTrue();

        organisationService.removeComponent(seedStack, component0);
        organisationService.removeMember(seedStack, john);
        organisationService.addOwner(seedStack, john);
        organisationService.removeOwner(seedStack, admin);

        organisation = organisationRepository.load(seedStack);
        assertThat(organisation.getComponents()).isEmpty();
        assertThat(organisation.getOwners()).containsExactly(john);
        assertThat(organisation.getMembers()).isEmpty();
    }

    @WithUser(id = "admin", password = "password")
    @Test
    public void onlyAddOnce() {
        organisationService.addMember(seedStack, john);
        organisationService.addMember(seedStack, john);
        organisationService.addComponent(seedStack, component0);
        organisationService.addComponent(seedStack, component0);
        organisationService.addOwner(seedStack, admin);
        organisationService.addOwner(seedStack, admin);

        Organisation organisation = organisationRepository.load(seedStack);
        assertThat(organisation.getComponents()).containsExactly(component0);
        assertThat(organisation.getOwners()).containsExactly(admin);
        assertThat(organisation.getMembers()).containsExactly(john);
    }

    @WithUser(id = "john", password = "password")
    @Test(expected = AuthorizationException.class)
    public void onlyOrganisationOwnerHasWriteAccess() {
        organisationService.addMember(seedStack, john);
    }
}
