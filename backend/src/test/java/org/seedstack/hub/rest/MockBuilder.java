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

public class MockBuilder {

    public static Component mock(int i) {
        return mock(i, State.PUBLISHED);
    }

    public static Component mock(int i, State state) {
        ComponentId componentId = new ComponentId("Component" + i);

        UserId john = new UserId("adrienlauer");

        DocumentId icon = new DocumentId(componentId, "/icon.png");
        DocumentId readme = new DocumentId(componentId, "/readme.md");
        Description description = new Description(componentId.getName(), "A little summary.", icon, readme);

        Component component = new Component(componentId, john, description);

        changeState(component, state);

        component.addVersion(mockVersion(i, 1));
        component.addVersion(mockVersion(i, 2));

        component.addMaintainer(new UserId("pith"));
        component.addMaintainer(new UserId("kavi87"));

        setStars(component, i);
        return component;
    }

    private static void setStars(Component component, int stars) {
        for (int j = 0; j <= stars; j++) {
            component.star();
        }
    }

    private static void changeState(Component component, State state) {
        switch (state) {
            case PUBLISHED:
                component.publish();
                break;
            case PENDING:
                break;
            case ARCHIVED:
                component.archive();
                break;
        }
    }

    private static Version mockVersion(Integer componentNumber, Integer versionNumber) {
        Version version = new Version(new VersionId(versionNumber, versionNumber + 1, versionNumber + 2, "Version ".concat(versionNumber.toString())));
        version.setPublicationDate(LocalDate.now().minusDays(componentNumber + versionNumber));
        return version;
    }
}
