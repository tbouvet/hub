package org.seedstack.hub.infra.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.seedstack.hub.domain.model.component.ComponentException;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.File;

public class ManifestParserImpl implements ManifestParser {
    private static final String FULL_MANIFEST_PATTERN = "seedstack-component.%s";
    private static final String SHORT_MANIFEST_PATTERN = "component.%s";
    @Inject
    private Validator validator;

    @Override
    public Manifest parseManifest(File repositoryDirectory) {
        File manifestFile = findManifestFile(repositoryDirectory);

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
}
