/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;

import java.util.Optional;

public class Owner extends BaseValueObject {
    private UserId userId;
    private OrganisationId organisationId;

    public Owner(String name) {
        if (OrganisationId.isValid(name)) {
            organisationId = new OrganisationId(name);
        } else {
            userId = new UserId(name);
        }
    }

    public Owner(UserId userId) {
        this.userId = userId;
    }

    public Owner(OrganisationId organisationId) {
        this.organisationId = organisationId;
    }

    private Owner() {
    }

    public boolean isUser() {
        return userId != null;
    }

    public boolean isOrganisation() {
        return organisationId != null;
    }

    public Optional<UserId> getUserId() {
        return Optional.ofNullable(userId);
    }

    public Optional<OrganisationId> getOrganisationId() {
        return Optional.ofNullable(organisationId);
    }

    @Override
    public String toString() {
        return isUser() ? userId.getId() : organisationId.getId();
    }
}
