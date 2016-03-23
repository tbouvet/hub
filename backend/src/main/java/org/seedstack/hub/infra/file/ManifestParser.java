package org.seedstack.hub.infra.file;

import org.seedstack.business.Service;

import java.io.File;

@Service
public interface ManifestParser {
    Manifest parseManifest(File repositoryDirectory);
}
