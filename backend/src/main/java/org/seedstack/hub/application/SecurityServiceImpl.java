/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Owner;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.security.AuthenticationException;
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
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;

    @Override
    public User getAuthenticatedUser() {
        SimplePrincipalProvider simplePrincipalByName = securitySupport.getSimplePrincipalByName(Principals.IDENTITY);
        if (simplePrincipalByName != null) {
            return userRepository.load(new UserId(simplePrincipalByName.getValue()));
        } else {
            throw new AuthenticationException();
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
        return securitySupport.hasRole("admin");
    }

    @Override
    public boolean isOwnerOf(Component component) {
        User user = getAuthenticatedUser();

        Owner owner = component.getOwner();
        Optional<OrganisationId> organisationId = owner.getOrganisationId();
        Optional<UserId> userId = owner.getUserId();

        if (organisationId.isPresent()) {
            Organisation organisation = organisationRepository.load(organisationId.get());
            return organisation.isOwner(user.getId()) || organisation.isMember(user.getId());
        } else {
            return userId.isPresent() && userRepository.load(userId.get()) != null;
        }
    }

    @Override
    public boolean ownerExists(String owner) {
        return (OrganisationId.isValid(owner) && organisationRepository.load(new OrganisationId(owner)) != null)
                || userRepository.findByEmail(owner).isPresent();
    }
}
