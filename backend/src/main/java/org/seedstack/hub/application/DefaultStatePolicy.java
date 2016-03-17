/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.security.AuthenticationException;

import javax.inject.Inject;

public class DefaultStatePolicy implements StatePolicy {

    @Inject
    private SecurityService securityService;

    @Override
    public boolean canPublish(Component component) {
        return securityService.isUserAdmin();
    }

    @Override
    public boolean canArchive(Component component) {
        UserId userId = securityService.getAuthenticatedUser()
            .orElseThrow(AuthenticationException::new).getEntityId();
        return securityService.isUserAdmin() || component.getOwner().equals(userId);
    }
}
