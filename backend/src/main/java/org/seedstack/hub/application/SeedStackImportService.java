package org.seedstack.hub.application;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.component.Component;

@Service
public interface SeedStackImportService {
    Component importFromGithub(String organisationName, String componentName);
}
