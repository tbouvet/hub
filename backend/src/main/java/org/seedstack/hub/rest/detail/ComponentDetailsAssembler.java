/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.detail;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.AbstractComponentAssembler;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.shared.UriBuilder;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.util.List;
import java.util.stream.Collectors;

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
        componentDetails.setReleases(component.getReleases().stream().map(ReleaseRepresentation::new).collect(Collectors.toList()));
        componentDetails.setDocs(component.getDocs().stream().map(this::documentIdToString).collect(toList()));
        componentDetails.setMaintainers(component.getMaintainers().stream().map(UserId::getId).collect(toList()));

        assembleHalLinks(componentDetails, componentId);

        updateUrls(componentDetails);
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
            componentDetails.setIcon(documentIdToString(description.getIcon()));
            componentDetails.setReadme(documentIdToString(description.getReadme()));
            componentDetails.setImages(description.getImages().stream().map(this::documentIdToString).collect(toList()));
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
                    .uri(Rels.STAR).set(COMPONENT_ID, componentId).expand());
        }
    }

    private void updateUrls(ComponentDetails componentDetails) {
        componentDetails.setImages(addContextPath(componentDetails.getImages()));
        componentDetails.setDocs(addContextPath(componentDetails.getDocs()));
        componentDetails.setIcon(addContextPath(componentDetails.getIcon()));
        componentDetails.setReadme(addContextPath(componentDetails.getReadme()));
    }

    private List<String> addContextPath(List<String> urls) {
        return urls.stream().map(this::addContextPath).collect(toList());
    }

}