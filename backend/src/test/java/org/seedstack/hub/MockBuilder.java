/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub;

import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

public class MockBuilder {

    public static Component mock(int i) {
        return mock(i, State.PUBLISHED, "adrienlauer");
    }

    public static Component mock(int i, State state) {
        return mock(i, state, "adrienlauer");
    }

    public static Component mock(int i, State state, String owner) {
        return mock("Component", i, state, owner);
    }

    public static Component mock(String name, int i, State state, String owner) {
        ComponentId componentId = new ComponentId(name + i);

        DocumentId icon = new DocumentId(componentId, "/icon.png");
        DocumentId readme = new DocumentId(componentId, "/readme.md");
        Description description = new Description(componentId.getName(), "A little summary.", "MPL2", icon, readme);

        Component component = new Component(componentId, name + " " + i, new Owner(owner), description);
        changeState(component, state);

        component.addRelease(mockVersion(i, 1));
        component.addRelease(mockVersion(i, 2));

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
                component.publish();
                component.archive();
                break;
        }
    }

    private static Release mockVersion(Integer componentNumber, Integer versionNumber) {
        Release release = new Release(new Version(versionNumber, versionNumber + 1, versionNumber + 2, "M".concat(versionNumber.toString())));
        release.setDate(LocalDate.now().minusDays(componentNumber + versionNumber));
        try {
            release.setUrl(new URL("http://github.com/seedstack/Component1/releases"));
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        return release;
    }
}
