/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

        ComponentId componentId = new ComponentId(manifest.getId());
        Component component = new Component(
                componentId,
                new UserId(manifest.getOwner()),
                buildDescription(directory, manifest, componentId)
        );

        Version version = new Version(new VersionId(manifest.getVersion()));
        version.setUrl(manifest.getUrl());
        if (manifest.getPublicationDate ()!= null) {
            try {
                version.setPublicationDate(LocalDate.parse(manifest.getPublicationDate(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (DateTimeParseException e) {
                throw new ImportException("Invalid publication date " + manifest.getPublicationDate(), e);
            }
        } else {
            version.setPublicationDate(LocalDate.now());
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

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
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
                componentId.getName(),
                manifest.getSummary(),
                manifest.getIcon() != null ? buildDocumentId(componentId, directory, manifest.getIcon()) : null,
                readme != null ? buildDocumentId(componentId, directory, readme.getPath()) : null
        );

        if (manifest.getImages ()!= null && !manifest.getImages().isEmpty()) {
            description.setImages(manifest.getImages().stream().map(s -> buildDocumentId(componentId, directory, s)).collect(Collectors.toList()));
        }

        if (manifest.getMaintainers ()!= null && !manifest.getMaintainers().isEmpty()) {
            description.setMaintainers(manifest.getMaintainers().stream().map(UserId::new).collect(Collectors.toList()));
        }

        return description;
    }

    private DocumentId buildDocumentId(ComponentId componentId, File root, String documentPath) {
        return new DocumentId(componentId, new File(root, documentPath).getPath());
    }
}
