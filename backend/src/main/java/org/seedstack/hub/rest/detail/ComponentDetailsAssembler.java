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

public class ComponentDetailsAssembler extends BaseAssembler<Component, ComponentDetails> {
    //    @Override
//    protected PropertyMap<Component, ComponentDetails> providePropertyMap() {
//        return new PropertyMap<Component, ComponentDetails>() {
//            @Override
//            protected void configure() {
//                map().setName(source.getDescription().getName());
//                map().setSummary(source.getDescription().getSummary());
//                map(source.getDescription().getIcon()).setIcon(null);
//                map(source.getDescription().getReadme()).setReadme(null);
//                map(source.getDescription().getImages()).setImages(null);
//                map(source.getDocs()).setDocs(null);
//                List<Version> versions = source.getVersions();
//                if (!versions.isEmpty()) {
//                    map(versions.get(0).toString()).setVersion(null);
//                }
//            }
//        };
//    }

    @Inject
    private RelRegistry relRegistry;

    @Override
    protected void doAssembleDtoFromAggregate(ComponentDetails componentDetails, Component component) {
        componentDetails.setId(component.getId().getName());
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
                .set("componentId", component.getId().getName()).expand());
        componentDetails.link(Rels.AUTHOR_COMPONENTS, relRegistry
                .uri(Rels.AUTHOR_COMPONENTS).set("userId", component.getOwner().getId()).expand());
    }

    @Override
    protected void doMergeAggregateWithDto(Component component, ComponentDetails componentDetails) {
        throw new UnsupportedOperationException();
    }

    private String documentIdToString(DocumentId documentId) {
        return UriBuilder.uri(Rels.COMPONENTS, documentId.getComponentId().toString(), "files", documentId.getPath());
    }
}
