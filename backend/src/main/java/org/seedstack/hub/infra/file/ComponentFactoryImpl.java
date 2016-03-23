/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.seedstack.business.domain.BaseFactory;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.hub.domain.services.text.TextService;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

class ComponentFactoryImpl extends BaseFactory<Component> implements ComponentFactory {
    private static final String FULL_MANIFEST_PATTERN = "seedstack-component.%s";
    private static final String SHORT_MANIFEST_PATTERN = "component.%s";

    @Inject
    private Validator validator;
    @Inject
    private TextService textService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;

    @Override
    public Component createComponent(File directory) {
        Manifest manifest = parseManifest(findManifestFile(directory));
        ComponentId componentId = new ComponentId(manifest.getId());
        Owner owner = getAndCheckOwner(manifest);

        Component component = new Component(
                componentId,
                manifest.getName(),
                owner,
                buildDescription(directory, manifest, componentId)
        );

        manifest.getReleases().forEach(releaseDTO -> {
            Release release = new Release(new Version(releaseDTO.getVersion()));
            try {
                release.setUrl(new URL(releaseDTO.getUrl()));
            } catch (MalformedURLException e) {
                throw new ComponentException("Malformed release URL: " + releaseDTO.getUrl(), e);
            }
            if (releaseDTO.getDate() != null) {
                release.setPublicationDate(releaseDTO.getDate());
            } else {
                release.setDate(LocalDate.now());
            }
            component.addRelease(release);
        });

        if (manifest.getDocs() != null) {
            component.replaceDocs(manifest.getDocs().stream().map(s -> new DocumentId(componentId, s)).collect(Collectors.toList()));
        }

        if (manifest.getMaintainers() != null && !manifest.getMaintainers().isEmpty()) {
            component.replaceMaintainers(manifest.getMaintainers().stream().map(this::lookupUserIdByEmail).collect(Collectors.toList()));
        }

        return component;
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

    private File findManifestFile(File repositoryDirectory) {
        File fileToCheck;

        // JSON full manifest
        fileToCheck = new File(repositoryDirectory, String.format(FULL_MANIFEST_PATTERN, "json"));
        if (fileToCheck.canRead()) {
            return fileToCheck;
        }

        // YAML full manifest
        fileToCheck = new File(repositoryDirectory, String.format(FULL_MANIFEST_PATTERN, "yaml"));
        if (fileToCheck.canRead()) {
            return fileToCheck;
        }

        // JSON short manifest
        fileToCheck = new File(repositoryDirectory, String.format(SHORT_MANIFEST_PATTERN, "json"));
        if (fileToCheck.canRead()) {
            return fileToCheck;
        }

        // YAML short manifest
        fileToCheck = new File(repositoryDirectory, String.format(SHORT_MANIFEST_PATTERN, "yaml"));
        if (fileToCheck.canRead()) {
            return fileToCheck;
        }

        throw new ComponentException("Missing manifest file for component located in " + repositoryDirectory);
    }

    private Manifest parseManifest(File manifestFile) {
        Manifest manifest;

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            manifest = objectMapper.readValue(manifestFile, Manifest.class);
        } catch (Exception e) {
            throw new ComponentException("Unable to parse component manifest", e);
        }

        validator.validate(manifest);

        return manifest;
    }

    private Description buildDescription(File directory, Manifest manifest, ComponentId componentId) {
        Description description = new Description(
                componentId.getName(),
                manifest.getSummary(),
                manifest.getLicense(),
                manifest.getIcon() != null ? new DocumentId(componentId, manifest.getIcon()) : null,
                textService.findTextDocument(componentId, directory, "README")
        );
        try {
            description.setComponentUrl(new URL(manifest.getUrl()));
        } catch (MalformedURLException e) {
            throw new ComponentException("Malformed component URL: " + manifest.getUrl(), e);
        }
        try {
            description.setIssues(new URL(manifest.getIssues()));
        } catch (MalformedURLException e) {
            throw new ComponentException("Malformed issues URL " + manifest.getIssues(), e);
        }

        List<String> images = manifest.getImages();
        if (images != null && !images.isEmpty()) {
            description = description.replaceImages(images.stream().map(s -> new DocumentId(componentId, s)).collect(Collectors.toList()));
        }

        return description;
    }
}
