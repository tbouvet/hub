/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;

import java.time.LocalDate;

public class MockedComponentBuilder {

    public static Component mock(int i) {
        ComponentId componentId = new ComponentId("Component" + i);
        UserId john = new UserId("John");
        DocumentId icon = new DocumentId(componentId, "/icon.png");
        DocumentId readme = new DocumentId(componentId, "/readme.md");
        Description description = new Description(componentId.getName(), "A little summary.", icon, readme);
        Component component = new Component(componentId, john, description);
        component.addVersion(mockVersion(i, 1));
        component.addVersion(mockVersion(i, 2));
        for (int j = 0; j <= i; j++) {
            component.star();
        }
        return component;
    }

    private static Version mockVersion(Integer componentNumber, Integer versionNumber) {
        Version version = new Version(new VersionId(versionNumber, versionNumber + 1, versionNumber + 2, "Version ".concat(versionNumber.toString())));
        version.setPublicationDate(LocalDate.now().minusDays(componentNumber + versionNumber));
        return version;
    }
}
