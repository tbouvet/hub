/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;

@Service
public interface OrganisationService {

    void addOwner(OrganisationId id, UserId user);
    void removeOwner(OrganisationId id, UserId user);

    void addMember(OrganisationId id, UserId user);
    void removeMember(OrganisationId id, UserId user);

    void addComponent(OrganisationId id, ComponentId componentId);
    void removeComponent(OrganisationId id, ComponentId componentId);
}
