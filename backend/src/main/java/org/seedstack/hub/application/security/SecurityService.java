/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.security;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.user.User;

@Service
public interface SecurityService {
    User getAuthenticatedUser();

    void checkUserIsAdmin();

    boolean isUserAdmin();

    boolean isOwnerOf(Component component);

    boolean ownerExists(String owner);
}
