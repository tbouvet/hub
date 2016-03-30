/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.fetch;

import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.security.SecurityService;
import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.hub.domain.services.text.TextService;
import org.seedstack.hub.infra.file.Manifest;
import org.seedstack.hub.infra.file.ManifestParser;
import org.seedstack.seed.Logging;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

class ImportServiceImpl implements ImportService {

    @Inject
    private ManifestParser manifestParser;
    @Inject
    private TextService textService;
    @Inject
    private DomainRegistry domainRegistry;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private DocumentFactory documentFactory;
    @Inject
    private SecurityService securityService;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private Repository<Document, DocumentId> documentRepository;
    @Logging
    private Logger logger;

    @Override
    public Component importComponent(Source source) {
        File directory = getWorkingDirectory();
        Component component;

        try {
            FetchService service = domainRegistry.getService(FetchService.class, source.getVcsType().qualifier());
            try {
                service.fetchRepository(new URL(source.getUrl()), directory);
            } catch (MalformedURLException e) {
                throw new ImportException("Invalid URL: " + source.getUrl());
            }

            Manifest manifest = manifestParser.parseManifest(directory);
            component = componentFactory.createComponent(manifest);
            // FIXME checkCurrentUserIs(component.getOwner());
            componentRepository.persist(component);
            documentFactory.createDocuments(component, directory).forEach(documentRepository::persist);
        } finally {
            deleteWorkingDirectory(directory);
        }

        return component;
    }

    private void checkCurrentUserIs(UserId owner) {
        if (!owner.equals(securityService.getAuthenticatedUser().getId())) {
            throw new ComponentException("Authenticated user is not the owner of component");
        }
    }

    private File getWorkingDirectory() {
        try {
            return Files.createTempDirectory("tempfiles").toFile();
        } catch (IOException e) {
            throw new ImportException("Unable to create work directory", e);
        }
    }

    private void deleteWorkingDirectory(File directory) {
        try {
            Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return safeDelete(file);
                }

                private FileVisitResult safeDelete(Path file) {
                    try {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    } catch (Exception e) {
                        logger.warn(e.getMessage());
                        return FileVisitResult.TERMINATE;
                    }
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return safeDelete(dir);
                }
            });
        } catch (IOException e) {
            logger.warn("Unable to delete working directory " + directory.getAbsolutePath(), e);
        }
    }
}
