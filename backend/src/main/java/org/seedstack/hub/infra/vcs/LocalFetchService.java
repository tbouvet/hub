/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.application.fetch.Manifest;
import org.seedstack.hub.application.fetch.ManifestParser;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.services.fetch.FetchException;
import org.seedstack.hub.domain.services.fetch.FetchResult;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.seed.Logging;
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
import java.util.stream.Stream;

abstract class LocalFetchService implements FetchService {
    private final ThreadLocal<File> tempDir = new ThreadLocal<>();
    @Logging
    private Logger logger;
    @Inject
    private ManifestParser manifestParser;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private DocumentFactory documentFactory;

    @Override
    public FetchResult fetch(Source source) throws FetchException {
        File target = getWorkingDirectory();
        tempDir.set(target);
        doFetch(source.getActualUrl(), target);
        Manifest manifest = manifestParser.parseManifest(target);
        Component component = componentFactory.createComponent(manifest);
        component.setSource(source);
        Stream<Document> documents = documentFactory.createDocuments(component, target);
        return new FetchResult(component, documents);
    }

    private File getWorkingDirectory() {
        try {
            File tempDir = Files.createTempDirectory("tempfiles").toFile();
            checkLocalDirectory(tempDir);
            return tempDir;
        } catch (IOException e) {
            throw new ImportException("Unable to create work directory", e);
        }
    }

    private void checkLocalDirectory(File location) {
        if (!location.exists()) {
            logger.debug("Directory " + location.getAbsolutePath() + " doesn't exists, creating it");
            if (!location.mkdirs()) {
                throw new ImportException("Cannot create directory " + location.getAbsolutePath());
            }
        }

        if (!location.isDirectory()) {
            throw new ImportException("Location " + location.getAbsolutePath() + " does not denote a directory");
        }

        if (!location.canWrite()) {
            throw new ImportException("Location " + location.getAbsolutePath() + " is not writable");
        }
    }

    @Override
    public void clean() {
        File target = tempDir.get();
        if (target != null) {
            final Exception[] firstException = new Exception[1];
            try {
                Files.walkFileTree(target.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        return safeDelete(file);
                    }

                    private FileVisitResult safeDelete(Path file) {
                        try {
                            Files.delete(file);
                        } catch (Exception e) {
                            if (firstException[0] == null) {
                                firstException[0] = e;
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return safeDelete(dir);
                    }
                });
            } catch (IOException e) {
                if (firstException[0] == null) {
                    firstException[0] = e;
                }
            } finally {
                tempDir.remove();
            }

            if (firstException[0] != null) {
                logger.warn("Unable to delete temp directory " + target.getAbsolutePath() + ": ", firstException[0].getMessage());
            }
        }
    }

    abstract protected void doFetch(URL remote, File local) throws FetchException;
}
