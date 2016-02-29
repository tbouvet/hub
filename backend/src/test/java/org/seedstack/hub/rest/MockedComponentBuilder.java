/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;

public class MockedComponentBuilder {

    public static Component mock(int i) {
        ComponentId componentId = new ComponentId("Component" + i);
        UserId john = new UserId("John");
        DocumentId icon = new DocumentId(componentId, "/icon.png");
        DocumentId readme = new DocumentId(componentId, "/readme.md");
        Description description = new Description(componentId.getName(), "A little summary.", icon, readme);
        return new Component(componentId, john, description);
    }
}
