/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.domain.model.component.VersionId;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.services.text.TextProcessingException;
import org.seedstack.hub.domain.services.text.TextService;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class ImportServiceImpl implements ImportService {
    public static final String FULL_MANIFEST_PATTERN = "seedstack-component.%s";
    public static final String SHORT_MANIFEST_PATTERN = "component.%s";

    @Inject
    private Validator validator;
    @Inject
    private TextService textService;

    @Override
    public Component importComponent(File directory) throws ImportException {
        Manifest manifest = parseManifest(findManifestFile(directory));

        ComponentId componentId = new ComponentId(manifest.id);
        Component component = new Component(
                componentId,
                new UserId(manifest.owner),
                buildDescription(directory, manifest, componentId)
        );

        Version version = new Version(new VersionId(manifest.version));
        if (manifest.publicationDate != null) {
            try {
                version.setPublicationDate(new SimpleDateFormat("yyyy-MM-dd").parse(manifest.publicationDate));
            } catch (ParseException e) {
                throw new ImportException("Invalid publication date " + manifest.publicationDate, e);
            }
        } else {
            version.setPublicationDate(new Date());
        }
        component.addVersion(version);

        return component;
    }

    private File findManifestFile(File repositoryDirectory) throws ImportException {
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

        throw new ImportException("Missing manifest file for component located in " + repositoryDirectory);
    }

    private Manifest parseManifest(File manifestFile) throws ImportException {
        Manifest manifest;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            manifest = objectMapper.readValue(manifestFile, Manifest.class);
        } catch (Exception e) {
            throw new ImportException("Unable to parse component manifest", e);
        }

        validator.validate(manifest);

        return manifest;
    }

    private Description buildDescription(File directory, Manifest manifest, ComponentId componentId) throws ImportException {
        File readme;
        try {
            readme = textService.findTextDocument(directory, "README");
        } catch (TextProcessingException e) {
            throw new ImportException("An error occurred searching for README file", e);
        }

        Description description = new Description(
                manifest.name,
                manifest.summary,
                manifest.icon != null ? buildDocumentId(componentId, directory, new File(manifest.icon)) : null,
                readme != null ? buildDocumentId(componentId, directory, readme) : null
        );

        if (manifest.images != null && !manifest.images.isEmpty()) {
            description.setImages(manifest.images.stream().map(s -> buildDocumentId(componentId, directory, new File(s))).collect(Collectors.toList()));
        }

        if (manifest.maintainers != null && !manifest.maintainers.isEmpty()) {
            description.setMaintainers(manifest.maintainers.stream().map(UserId::new).collect(Collectors.toList()));
        }

        return description;
    }

    private DocumentId buildDocumentId(ComponentId componentId, File root, File document) {
        return new DocumentId(componentId, document.toPath().relativize(root.toPath()).toString());
    }
}
