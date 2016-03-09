/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.domain.model.component.VersionId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.hub.domain.services.text.TextService;
import org.seedstack.seed.security.AuthenticationException;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportServiceImpl implements ImportService {
    public static final String FULL_MANIFEST_PATTERN = "seedstack-component.%s";
    public static final String SHORT_MANIFEST_PATTERN = "component.%s";

    private final Validator validator;
    private final TextService textService;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final DocumentFactory documentFactory;

    @Inject
    public ImportServiceImpl(Validator validator, TextService textService, UserRepository userRepository, SecurityService securityService, DocumentFactory documentFactory) {
        this.validator = validator;
        this.textService = textService;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.documentFactory = documentFactory;
    }

    @Override
    public Component importComponent(File directory) {
        Manifest manifest = parseManifest(findManifestFile(directory));
        ComponentId componentId = new ComponentId(manifest.getId());
        UserId ownerId = lookupUserId(manifest.getOwner());

        checkCurrentUserIs(ownerId);

        Component component = new Component(
                componentId,
                ownerId,
                buildDescription(directory, manifest, componentId)
        );

        Version version = new Version(new VersionId(manifest.getVersion()));
        version.setUrl(manifest.getUrl());
        if (manifest.getPublicationDate() != null) {
            version.setPublicationDate(manifest.getPublicationDate());
        } else {
            version.setPublicationDate(LocalDate.now());
        }
        component.addVersion(version);

        if (manifest.getDocs() != null) {
            component.replaceDocs(manifest.getDocs().stream().map(s -> new DocumentId(componentId, s)).collect(Collectors.toList()));
        }

        return component;
    }

    @Override
    public Stream<Document> streamDocuments(Component component, File directory) {
        Set<DocumentId> documents = new HashSet<>();

        documents.add(component.getDescription().getReadme());
        documents.add(component.getDescription().getIcon());
        documents.addAll(component.getDescription().getImages());
        documents.addAll(component.getDocs());

        return documents.stream()
                .map(documentId -> buildDocumentStream(documentId, directory))
                .flatMap(Function.identity());
    }

    private Stream<Document> buildDocumentStream(DocumentId documentId, File directory) {
        Document document = documentFactory.createDocument(documentId, directory);
        if (document instanceof TextDocument) {
            return Stream.concat(
                    Stream.of(document),
                    textService.findReferences((TextDocument) document).stream().map(referenceDocumentId -> documentFactory.createDocument(referenceDocumentId, directory))
            );
        } else {
            return Stream.of(document);
        }
    }

    private UserId lookupUserId(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ImportException("Cannot find hub user for email: " + email)).getUserId();
    }

    private void checkCurrentUserIs(UserId owner) {
        UserId authenticatedUserId = securityService.getAuthenticatedUser().orElseThrow(() -> new AuthenticationException("No authenticated user available")).getUserId();

        if (!owner.equals(authenticatedUserId)) {
            throw new ImportException("Authenticated user is not the owner of component");
        }
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

        throw new ImportException("Missing manifest file for component located in " + repositoryDirectory);
    }

    private Manifest parseManifest(File manifestFile) {
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

    private Description buildDescription(File directory, Manifest manifest, ComponentId componentId) {
        Description description = new Description(
                componentId.getName(),
                manifest.getSummary(),
                manifest.getIcon() != null ? new DocumentId(componentId, manifest.getIcon()) : null,
                textService.findTextDocument(componentId, directory, "README")
        );

        List<String> images = manifest.getImages();
        if (images != null && !images.isEmpty()) {
            description = description.replaceImages(images.stream().map(s -> new DocumentId(componentId, s)).collect(Collectors.toList()));
        }

        if (manifest.getMaintainers() != null && !manifest.getMaintainers().isEmpty()) {
            description = description.replaceMaintainers(manifest.getMaintainers().stream().map(this::lookupUserId).collect(Collectors.toList()));
        }

        return description;
    }
}
