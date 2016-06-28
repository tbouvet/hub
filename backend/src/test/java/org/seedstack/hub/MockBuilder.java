/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub;

import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.component.Owner;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.hub.domain.model.document.WikiDocument;
import org.seedstack.hub.domain.model.user.UserId;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

public class MockBuilder {

    public static Component mock(int i) {
        return mock(i, State.PUBLISHED, "user2");
    }

    public static Component mock(int i, State state) {
        return mock(i, state, "user2");
    }

    public static Component mock(int i, State state, String owner) {
        return mock("Component", i, state, owner);
    }

    public static Component mock(String name, int i, State state, String owner) {
        ComponentId componentId = new ComponentId(name + i);

        DocumentId icon = new DocumentId(componentId, DocumentScope.FILES, "/icon.png");
        DocumentId readme = new DocumentId(componentId, DocumentScope.FILES, "/readme.md");
        Description description = new Description(componentId.getName(), "A little summary.", "MPL2", icon, readme);

        Component component = new Component(componentId, name + " " + i, new Owner(owner), description);
        changeState(component, state);

        component.addRelease(mockVersion(i, 1));
        component.addRelease(mockVersion(i, 2));

        component.addMaintainer(new UserId("user3"));
        component.addMaintainer(new UserId("user1"));

        setStars(component, i);
        return component;
    }

    public static WikiDocument mockWikiDocument(Component component, String path, String... bodies) {
        WikiDocument wikiDocument = new WikiDocument(new DocumentId(component.getId(), DocumentScope.WIKI, path));
        int revCount = 0;
        for (String body : bodies) {
            wikiDocument.addRevision(body, new UserId("user2"), "rev" + revCount++);
        }
        return wikiDocument;
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
        release.setDate(LocalDateTime.now().minusDays(componentNumber + versionNumber));
        try {
            release.setUrl(new URL("http://github.com/seedstack/Component1/releases"));
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        return release;
    }
}
