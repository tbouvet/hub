/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.State;

import javax.inject.Inject;

class StatePolicyDefault implements StatePolicy {

    @Inject
    private SecurityService securityService;

    @Override
    public boolean canPublish(Component component) {
        return securityService.isUserAdmin() || (unArchived(component) && securityService.isOwnerOf(component));
    }

    private boolean unArchived(Component component) {
        return component.getState() == State.ARCHIVED;
    }

    @Override
    public boolean canArchive(Component component) {
        return securityService.isUserAdmin() || securityService.isOwnerOf(component);
    }
}
