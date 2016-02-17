/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.seedstack.business.domain.Factory;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.document.Document;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.File;
import java.util.ArrayList;

public class ImportServiceImpl implements ImportService {
    public static final String MANIFEST_JSON = "manifest.json";

    @Inject
    Factory<Component> componentFactory;
    @Inject
    Factory<Document> documentFactory;
    @Inject
    Validator validator;

    @Override
    public Component importComponent(File directory) throws ImportException {
        Manifest manifest = parseManifest(directory);
        Component component = componentFactory.create(new ComponentId(manifest.name));



        return component;
    }

    private Manifest parseManifest(File location) throws ImportException {
        Manifest manifest;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            manifest = objectMapper.readValue(getManifestFile(location), Manifest.class);
        } catch (Exception e) {
            throw new ImportException("Unable to parse component manifest", e);
        }

        validator.validate(manifest);

        return manifest;
    }

    private File getManifestFile(File repositoryDirectory) {
        return new File(repositoryDirectory, MANIFEST_JSON);
    }
}
