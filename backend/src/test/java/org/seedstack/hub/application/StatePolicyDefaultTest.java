/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;

import java.util.Optional;

@RunWith(JMockit.class)
public class StatePolicyDefaultTest {

    public static final String OWNER = "owner";
    public static final String SOMEONE = "someone";

    @Tested
    private DefaultStatePolicy underTest;
    @Injectable
    private SecurityService securityService;
    @Mocked
    private Component component;
    @Mocked
    private User user;

    @Test
    public void testAdminCanPublish() throws Exception {
        givenCurrentUserIsAdmin(true);
        Assertions.assertThat(underTest.canPublish(component)).isTrue();
    }

    private void givenCurrentUserIsAdmin(boolean isAdmin) {
        new Expectations() {{
            securityService.isUserAdmin(); result = isAdmin;
        }};
    }

    @Test
    public void testBasicUserCannotPublish() throws Exception {
        givenCurrentUserIsAdmin(false);
        Assertions.assertThat(underTest.canPublish(component)).isFalse();
    }

    @Test
    public void testAdminCanArchive() throws Exception {
        givenCurrentUserIsAdmin(true);
        givenUser(OWNER);
        Assertions.assertThat(underTest.canArchive(component)).isTrue();
    }

    @Test
    public void testOwnerCanArchive() throws Exception {
        givenCurrentUserIsAdmin(false);
        givenUser(OWNER);
        Assertions.assertThat(underTest.canArchive(component)).isTrue();
    }

    private void givenUser(String name) {
        new Expectations() {{
            securityService.getAuthenticatedUser(); result = Optional.of(user);
            user.getEntityId(); result = new UserId(name);
        }};
        new NonStrictExpectations() {{
            component.getOwner(); result = new UserId(OWNER);
        }};
    }

    @Test
    public void testBasicUserCannotArchive() throws Exception {
        givenCurrentUserIsAdmin(false);
        givenUser(SOMEONE);
        Assertions.assertThat(underTest.canArchive(component)).isFalse();
    }
}
