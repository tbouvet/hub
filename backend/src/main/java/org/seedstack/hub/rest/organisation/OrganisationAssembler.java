/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.organisation;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.component.list.ComponentCardAssembler;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static org.seedstack.hub.rest.organisation.OrganisationResource.ORGANISATION_ID;

public class OrganisationAssembler extends BaseAssembler<Organisation, OrganisationRepresentation> {

    @Inject
    private RelRegistry relRegistry;
    @Inject
    private ComponentCardAssembler componentCardAssembler;
    @Inject
    private Repository<Component, ComponentId> componentRepository;

    @Override
    protected void doAssembleDtoFromAggregate(OrganisationRepresentation organisationRepresentation, Organisation organisation) {
            organisationRepresentation.setId(organisation.getEntityId().getId());
            organisationRepresentation.setName(organisation.getName());

            organisationRepresentation.setOwners(organisation.getOwners().stream()
                    .map(UserId::getId).collect(Collectors.toList()));

            organisationRepresentation.setMembers(organisation.getMembers().stream()
                    .map(UserId::getId).collect(Collectors.toList()));

            organisationRepresentation.setComponents(organisation.getComponents().stream()
                    .map(componentRepository::load)
                    .map(componentCardAssembler::assembleDtoFromAggregate)
                    .collect(Collectors.toList()));

        organisationRepresentation.self(relRegistry.uri(Rels.ORGANISATION)
                .set(ORGANISATION_ID, organisation.getEntityId().getId())
                .getHref());
    }

    @Override
    protected void doMergeAggregateWithDto(Organisation organisation, OrganisationRepresentation organisationRepresentation) {
        organisation.changeName(organisationRepresentation.getName());
    }
}
