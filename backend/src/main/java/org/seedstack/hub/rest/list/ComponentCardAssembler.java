/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.list;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.shared.UriBuilder;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;

public class ComponentCardAssembler extends BaseAssembler<Component, ComponentCard> {

    @Inject
    private RelRegistry relRegistry;

    @Override
    protected void doAssembleDtoFromAggregate(ComponentCard componentCard, Component component) {
        componentCard.setId(component.getId().getName());
        componentCard.setName(component.getEntityId().getName());
        componentCard.setSummary(component.getDescription().getSummary());
        componentCard.setIcon(documentIdToString(component.getDescription().getIcon()));
        componentCard.setOwner(component.getOwner().getId());
        componentCard.setStars(component.getStars());
        componentCard.setState(component.getState());

        componentCard.link("self", relRegistry
                .uri(Rels.COMPONENT)
                .set("componentId", component.getId().getName()).expand());
        componentCard.link(Rels.AUTHOR_COMPONENTS, relRegistry
                .uri(Rels.AUTHOR_COMPONENTS).set("userId", component.getOwner().getId()).expand());
    }

    @Override
    protected void doMergeAggregateWithDto(Component component, ComponentCard componentCard) {
        throw new UnsupportedOperationException();
    }

    private String documentIdToString(DocumentId documentId) {
        return UriBuilder.uri(Rels.COMPONENTS, documentId.getComponentId().toString(), "files", documentId.getPath());
    }
}
