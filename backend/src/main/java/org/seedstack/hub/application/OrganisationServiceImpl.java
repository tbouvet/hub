/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.security.AuthorizationException;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.function.Consumer;

class OrganisationServiceImpl implements OrganisationService {

    @Inject
    private Repository<Organisation, OrganisationId> repository;
    @Inject
    private SecurityService securityService;
    
    @Override
    public void addOwner(OrganisationId id, UserId user) {
        organisationUpdate(id, organisation -> organisation.addOwner(user));
    }

    private void organisationUpdate(OrganisationId id, Consumer<Organisation> updateFunction) {
        Organisation organisation = loadOrganisation(id);
        checkIfAuthenticatedUserIsOwner(id, organisation);
        updateFunction.accept(organisation);
        repository.persist(organisation);
    }

    private void checkIfAuthenticatedUserIsOwner(OrganisationId id, Organisation organisation) {
        UserId authenticatedUser = securityService.getAuthenticatedUser().getId();
        if (!organisation.isOwner(authenticatedUser)) {
            throw new AuthorizationException("User " + authenticatedUser.getId() + " is not owner of " + id.getId());
        }
    }

    private Organisation loadOrganisation(OrganisationId id) {
        Organisation organisation = repository.load(id);
        if (organisation == null) {
            throw new NotFoundException("Organisation " + id.getId() + " not found.");
        }
        return organisation;
    }

    @Override
    public void removeOwner(OrganisationId id, UserId user) {
        organisationUpdate(id, organisation -> organisation.removeOwner(user));
    }

    @Override
    public void addMember(OrganisationId id, UserId user) {
        organisationUpdate(id, organisation -> organisation.addMember(user));
    }

    @Override
    public void removeMember(OrganisationId id, UserId user) {
        organisationUpdate(id, organisation -> organisation.removeMember(user));
    }

    @Override
    public void addComponent(OrganisationId id, ComponentId component) {
        organisationUpdate(id, organisation -> organisation.addComponent(component));
    }

    @Override
    public void removeComponent(OrganisationId id, ComponentId component) {
        organisationUpdate(id, organisation -> organisation.removeComponent(component));
    }
}
