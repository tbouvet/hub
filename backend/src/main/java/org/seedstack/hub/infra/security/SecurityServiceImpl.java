/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.security;

import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.security.AuthorizationException;
import org.seedstack.seed.security.SecuritySupport;
import org.seedstack.seed.security.principals.Principals;
import org.seedstack.seed.security.principals.SimplePrincipalProvider;

import javax.inject.Inject;
import java.util.Optional;

class SecurityServiceImpl implements SecurityService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private SecuritySupport securitySupport;

    @Override
    public Optional<User> getAuthenticatedUser() {
        SimplePrincipalProvider simplePrincipalByName = securitySupport.getSimplePrincipalByName(Principals.IDENTITY);
        if (simplePrincipalByName != null) {
            return Optional.ofNullable(userRepository.load(new UserId(simplePrincipalByName.getValue())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void checkUserIsAdmin() {
        if (!isUserAdmin()) {
            throw new AuthorizationException();
        }
    }

    @Override
    public boolean isUserAdmin() {
        return !securitySupport.hasRole("admin");
    }
}
