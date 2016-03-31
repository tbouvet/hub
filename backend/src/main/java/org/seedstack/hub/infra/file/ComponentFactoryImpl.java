/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.file;

import org.seedstack.business.domain.BaseFactory;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.fetch.Manifest;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentException;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Owner;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

class ComponentFactoryImpl extends BaseFactory<Component> implements ComponentFactory {

    @Inject
    private UserRepository userRepository;
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;

    @Override
    public Component createComponent(Manifest manifest) {
        ComponentId componentId = new ComponentId(manifest.getId());
        Owner owner = getAndCheckOwner(manifest);

        Component component = new Component(
                componentId,
                manifest.getName(),
                owner,
                buildDescription(manifest, componentId)
        );

        addReleases(manifest, component);

        if (manifest.getDocs() != null) {
            component.replaceDocs(manifest.getDocs().stream().map(s -> new DocumentId(componentId, DocumentScope.FILE, s)).collect(Collectors.toList()));
        }

        if (manifest.getMaintainers() != null && !manifest.getMaintainers().isEmpty()) {
            component.replaceMaintainers(manifest.getMaintainers().stream().map(this::lookupUserIdByEmail).collect(Collectors.toList()));
        }

        return component;
    }

    private void addReleases(Manifest manifest, Component component) {
        if (manifest.getReleases() != null) {
            manifest.getReleases().forEach(releaseDTO -> {
                Release release = new Release(new Version(releaseDTO.getVersion()));
                try {
                    release.setUrl(new URL(releaseDTO.getUrl()));
                } catch (MalformedURLException e) {
                    throw new ComponentException("Malformed release URL: " + releaseDTO.getUrl(), e);
                }
                if (releaseDTO.getDate() != null) {
                    release.setPublicationDate(releaseDTO.getDate());
                }
                component.addRelease(release);
            });
        }
    }

    private Owner getAndCheckOwner(Manifest manifest) {
        String owner;
        if (OrganisationId.isValid(manifest.getOwner())) {
            owner = manifest.getOwner();
            if (organisationRepository.load(new OrganisationId(owner)) == null) {
                throw new ComponentException("Unknown organisation: " + owner);
            }
        } else {
            owner = lookupUserIdByEmail(manifest.getOwner()).getId();
        }
        return new Owner(owner);
    }

    private UserId lookupUserIdByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ComponentException("Cannot find hub user for email: " + email)).getId();
    }

    private Description buildDescription(Manifest manifest, ComponentId componentId) {
        Description description = new Description(
                componentId.getName(),
                manifest.getSummary(),
                manifest.getLicense(),
                manifest.getIcon() != null ? new DocumentId(componentId, DocumentScope.FILE, manifest.getIcon()) : null,
                manifest.getReadme() != null ? new DocumentId(componentId, DocumentScope.FILE, manifest.getReadme()) : null
        );

        if (manifest.getUrl() != null) {
            try {
                description.setComponentUrl(new URL(manifest.getUrl()));
            } catch (MalformedURLException e) {
                throw new ComponentException("Malformed component URL: " + manifest.getUrl(), e);
            }
        }
        if (manifest.getIssues() != null) {
            try {
                description.setIssues(new URL(manifest.getIssues()));
            } catch (MalformedURLException e) {
                throw new ComponentException("Malformed issues URL " + manifest.getIssues(), e);
            }
        }

        List<String> images = manifest.getImages();
        if (images != null && !images.isEmpty()) {
            description = description.replaceImages(images.stream().map(s -> new DocumentId(componentId, DocumentScope.FILE, s)).collect(Collectors.toList()));
        }

        return description;
    }
}
