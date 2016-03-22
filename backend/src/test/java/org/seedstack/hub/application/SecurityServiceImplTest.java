/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.security.SecuritySupport;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class SecurityServiceImplTest {

    @Tested
    private SecurityServiceImpl underTest;

    @Injectable
    private UserRepository userRepository;
    @Injectable
    private SecuritySupport securitySupport;
    @Injectable
    private Repository<Organisation, OrganisationId> organisationRepository;

    @Mocked
    private Component component;

    @Test
    public void testIsOwner() {
        givenOwner("pith");
        assertThat(underTest.isOwnerOf(component)).isTrue();
    }

    private void givenOwner(String owner) {
        new Expectations() {{
            component.getOwner();
            result = owner;
        }};
    }

    @Test
    public void testIsNotOwner() {
        givenOwner("pith");
        new Expectations() {{
            userRepository.load(new UserId("pith")); result = null;
        }};
        assertThat(underTest.isOwnerOf(component)).isFalse();
    }

    @Test
    public void testIsOrganisationMember(@Mocked Organisation organisation, @Mocked User user) {
        givenOwner("@seedstack");

        UserId pith = new UserId("pith");
        new Expectations() {{
            user.getId(); result = pith;

            organisation.isMember(pith); result = true;
        }};

        assertThat(underTest.isOwnerOf(component)).isTrue();
    }

    @Test
    public void testIsNotOrganisationMember(@Mocked Organisation organisation, @Mocked User user) {
        givenOwner("@seedstack");

        UserId pith = new UserId("pith");
        new Expectations() {{
            user.getId(); result = pith;

            organisation.isMember(pith); result = false;
        }};

        assertThat(underTest.isOwnerOf(component)).isFalse();
    }
}
