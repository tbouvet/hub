/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.component.detail;

import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.component.AbstractComponentAssembler;
import org.seedstack.hub.rest.shared.DocumentRepresentation;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.seedstack.hub.rest.component.detail.ComponentResource.COMPONENT_ID;

public class ComponentDetailsAssembler extends AbstractComponentAssembler<ComponentDetails> {

    @Inject
    private StarringService starringService;

    @Override
    protected void doAssemble(ComponentDetails componentDetails, Component component) {
        componentDetails.setId(component.getId().getName());
        componentDetails.setName(component.getName());
        componentDetails.setOwner(component.getOwner().toString());
        componentDetails.setMaintainers(component.getMaintainers().stream().map(UserId::getId).collect(toList()));
        componentDetails.setStars(component.getStars());
        componentDetails.setState(component.getState());
        assembleDescription(componentDetails, component);
        assembleReleases(componentDetails, component);
        assembleEmbedded(componentDetails, component);
        assembleLinks(componentDetails, component);
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
            if (description.getIcon() != null) {
                componentDetails.embedded(Rels.ICON, new DocumentRepresentation(description.getIcon(), documentService, relRegistry));
            }
            if (description.getReadme() != null) {
                componentDetails.embedded(Rels.README, new DocumentRepresentation(description.getReadme(), documentService, relRegistry));
            }
            if (!description.getImages().isEmpty()) {
                componentDetails.embedded(Rels.IMAGES, description.getImages().stream().map(documentId -> new DocumentRepresentation(documentId, documentService, relRegistry)).collect(toList()));
            }
        }
    }

    private void assembleReleases(ComponentDetails componentDetails, Component component) {
        List<Release> releases = component.getReleases();
        if (!releases.isEmpty()) {
            componentDetails.setVersion(releases.get(0).toString());
        }
        componentDetails.setReleases(component.getReleases().stream().map(ReleaseRepresentation::new).collect(toList()));
    }

    private void assembleEmbedded(ComponentDetails componentDetails, Component component) {
        if (!component.getDocs().isEmpty()) {
            componentDetails.embedded(Rels.DOCS, component.getDocs().stream().map(documentId -> new DocumentRepresentation(documentId, documentService, relRegistry)).collect(toList()));
        }
        if (!component.getWikiPages().isEmpty()) {
            componentDetails.embedded(Rels.WIKI, component.getWikiPages().stream().map(documentId -> new DocumentRepresentation(documentId, documentService, relRegistry)).collect(toList()));
        }
    }

    private void assembleLinks(ComponentDetails componentDetails, Component component) {
        if (!starringService.hasStarred(component.getId())) {
            componentDetails.link(Rels.STAR, relRegistry.uri(Rels.STAR).set(COMPONENT_ID, component.getId().getName()));
        }
        componentDetails.link(Rels.COMMENT, relRegistry.uri(Rels.COMMENT).set(COMPONENT_ID, component.getId().getName()).templated());
        componentDetails.link(Rels.WIKI, relRegistry.uri(Rels.WIKI).set(COMPONENT_ID, component.getId().getName()).templated());
    }
}