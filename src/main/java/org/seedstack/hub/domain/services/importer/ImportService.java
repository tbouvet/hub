package org.seedstack.hub.domain.services.importer;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.component.Component;

import java.io.File;

@Service
public interface ImportService {
    Component importComponent(File directory) throws ImportException;
}
