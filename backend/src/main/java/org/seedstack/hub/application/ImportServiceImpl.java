/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.business.domain.DomainRegistry;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentException;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.seed.Logging;
import org.seedstack.seed.security.AuthenticationException;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ImportServiceImpl implements ImportService {
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
    Logger logger;

    @Override
    public Component importComponent(VCSType vcsType, URL url) {
        File directory = getWorkingDirectory();
        Component component;

        try {
            domainRegistry.getService(FetchService.class, vcsType.qualifier()).fetchRepository(url, directory);
            component = componentFactory.createComponent(directory);
            // FIXME checkCurrentUserIs(component.getOwner());
            componentRepository.save(component);
            documentFactory.createDocuments(component, directory).forEach(documentRepository::save);
        } finally {
            deleteWorkingDirectory(directory);
        }

        return component;
    }

    private void checkCurrentUserIs(UserId owner) {
        UserId authenticatedUserId = securityService.getAuthenticatedUser().orElseThrow(() -> new AuthenticationException("No authenticated user available")).getId();

        if (!owner.equals(authenticatedUserId)) {
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
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn("Unable to delete working directory " + directory.getAbsolutePath(), e);
        }
    }
}
