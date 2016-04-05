/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.detail;

import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.AbstractComponentAssembler;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.shared.DocumentRepresentation;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.seedstack.hub.rest.detail.ComponentResource.COMPONENT_ID;

public class ComponentDetailsAssembler extends AbstractComponentAssembler<ComponentDetails> {

    @Inject
    private StarringService starringService;

    @Override
    protected void doAssemble(ComponentDetails componentDetails, Component component) {
        String componentId = component.getId().getName();
        componentDetails.setId(componentId);
        componentDetails.setName(component.getName());
        componentDetails.setOwner(component.getOwner().toString());
        componentDetails.setStars(component.getStars());
        componentDetails.setState(component.getState());

        assembleDescription(componentDetails, component);
        List<Release> releases = component.getReleases();
        if (!releases.isEmpty()) {
            componentDetails.setVersion(releases.get(0).toString());
        }
        componentDetails.setReleases(component.getReleases().stream().map(ReleaseRepresentation::new).collect(toList()));
        componentDetails.setDocs(component.getDocs().stream().map(documentId -> new DocumentRepresentation(documentId, relRegistry)).collect(toList()));
        componentDetails.setMaintainers(component.getMaintainers().stream().map(UserId::getId).collect(toList()));
        componentDetails.setWikiPages(component.getWikiPages().stream().map(documentId -> new DocumentRepresentation(documentId, relRegistry)).collect(toList()));

        assembleHalLinks(componentDetails, componentId);
    }

    private void assembleDescription(ComponentDetails componentDetails, Component component) {
        Description description = component.getDescription();
        if (description != null) {
            componentDetails.setSummary(description.getSummary());
            componentDetails.setLicense(description.getLicense());
            if (description.getComponentUrl() != null) {
                componentDetails.setUrl(description.getComponentUrl().toString());
            }
            if (description.getIssues() != null) {
                componentDetails.setIssues(description.getIssues().toString());
            }
            componentDetails.setLicense(description.getLicense());
            if (description.getIcon() != null) {
                componentDetails.setIcon(new DocumentRepresentation(description.getIcon(), relRegistry));
            }
            if (description.getReadme() != null) {
                componentDetails.setReadme(new DocumentRepresentation(description.getReadme(), relRegistry));
            }
            componentDetails.setImages(description.getImages().stream().map(documentId -> new DocumentRepresentation(documentId, relRegistry)).collect(toList()));
        }
    }

    private void assembleHalLinks(ComponentDetails componentDetails, String componentId) {
        addStarLinkIfNotStarred(componentDetails, componentId);

        componentDetails.link(Rels.COMMENT, relRegistry
                .uri(Rels.COMMENT).set(COMPONENT_ID, componentId).templated());
    }

    private void addStarLinkIfNotStarred(ComponentDetails componentDetails, String componentId) {
        if (!starringService.hasStarred(new ComponentId(componentId))) {
            componentDetails.link(Rels.STAR, relRegistry
                    .uri(Rels.STAR).set(COMPONENT_ID, componentId).getHref());
        }
    }
}