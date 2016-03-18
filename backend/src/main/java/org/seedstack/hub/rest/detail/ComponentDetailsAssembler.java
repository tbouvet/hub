/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.detail;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.shared.UriBuilder;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.seedstack.hub.rest.detail.ComponentResource.COMPONENT_ID;
import static org.seedstack.hub.rest.user.UserResource.USER_ID;

public class ComponentDetailsAssembler extends BaseAssembler<Component, ComponentDetails> {

    @Inject
    private RelRegistry relRegistry;

    @Override
    protected void doAssembleDtoFromAggregate(ComponentDetails componentDetails, Component component) {
        String componentId = component.getId().getName();
        componentDetails.setId(componentId);
        componentDetails.setName(component.getEntityId().getName());
        componentDetails.setSummary(component.getDescription().getSummary());
        componentDetails.setIcon(documentIdToString(component.getDescription().getIcon()));
        componentDetails.setOwner(component.getOwner().getId());
        componentDetails.setStars(component.getStars());
        componentDetails.setState(component.getState());

        List<Version> versions = component.getVersions();
        if (!versions.isEmpty()) {
            componentDetails.setVersion(versions.get(0).toString());
        }
        componentDetails.setReadme(documentIdToString(component.getDescription().getReadme()));
        componentDetails.setImages(component.getDescription().getImages().stream().map(this::documentIdToString).collect(toList()));
        componentDetails.setDocs(component.getDocs().stream().map(this::documentIdToString).collect(toList()));
        componentDetails.setMaintainers(component.getMaintainers().stream().map(UserId::getId).collect(toList()));

        componentDetails.link("self", relRegistry
                .uri(Rels.COMPONENT)
                .set(COMPONENT_ID, componentId).expand());

        componentDetails.link(Rels.AUTHOR_COMPONENTS, relRegistry
                .uri(Rels.AUTHOR_COMPONENTS).set(USER_ID, component.getOwner().getId()).expand());

        componentDetails.link(Rels.STARS, relRegistry
                .uri(Rels.STARS).set(COMPONENT_ID, componentId).expand());

        componentDetails.link(Rels.COMMENT, relRegistry
                .uri(Rels.COMMENT).set(COMPONENT_ID, componentId).expand());
    }

    @Override
    protected void doMergeAggregateWithDto(Component component, ComponentDetails componentDetails) {
        throw new UnsupportedOperationException();
    }

    private String documentIdToString(DocumentId documentId) {
        return UriBuilder.uri(Rels.COMPONENTS, documentId.getComponentId().toString(), "files", documentId.getPath());
    }
}
