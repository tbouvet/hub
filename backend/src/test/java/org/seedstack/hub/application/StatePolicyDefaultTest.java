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
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.user.User;

@RunWith(JMockit.class)
public class StatePolicyDefaultTest {

    private static final String OWNER = "owner";
    private static final String SOMEONE = "someone";

    @Tested
    private StatePolicyDefault underTest;
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
        Assertions.assertThat(underTest.canArchive(component)).isTrue();
    }

    @Test
    public void testOwnerCanArchive() throws Exception {
        givenCurrentUserIsAdmin(false);
        givenUserIsOwner(true);
        Assertions.assertThat(underTest.canArchive(component)).isTrue();
    }

    private void givenUserIsOwner(boolean isOwner) {
        new Expectations() {{
            securityService.isOwnerOf(component); result = isOwner;
        }};
    }

    @Test
    public void testBasicUserCannotArchive() throws Exception {
        givenCurrentUserIsAdmin(false);
        givenUserIsOwner(false);
        Assertions.assertThat(underTest.canArchive(component)).isFalse();
    }
}
