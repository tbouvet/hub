/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentTest {
    private String newName = "Component 1";
    private String newOwner = "jane";
    private String newSummary = "Summary";
    private String newLicense = "MPL 2";

    @Test
    public void testMergeImportedData() {
        Component current = givenCurrentComponent();
        Component sync = givenNewComponent();

        current.mergeWith(sync);

        assertThat(current.getName()).isEqualTo(newName);
        assertThat(current.getOwner().toString()).isEqualTo(newOwner);
        assertThat(current.getDescription().getSummary()).isEqualTo(newSummary);
        assertThat(current.getDescription().getLicense()).isEqualTo(newLicense);
        assertThat(current.getDescription().getLicense()).isEqualTo(newLicense);
    }

    @Test
    public void testMergeKeepAppData() {
        Component current = givenCurrentComponent();
        Component sync = givenNewComponent();

        current.mergeWith(sync);

        assertThat(current.getState()).isEqualTo(State.PUBLISHED);
        assertThat(current.getStars()).isEqualTo(2);
        assertThat(current.getComments()).hasSize(1);
    }

    private Component givenNewComponent() {
        Description newDescription = new Description(newName, newSummary, newLicense, null, null);
        return new Component(new ComponentId("comp1"), newName, new Owner(newOwner), newDescription);
    }

    private Component givenCurrentComponent() {
        Description description = new Description("component 1", "little summary", "MLP", null, null);
        Component current = new Component(new ComponentId("comp1"), "Component1", new Owner("john"), description);
        current.publish();
        current.star();
        current.star();
        current.addComment(new Comment("someone", "blabla", new Date()));
        return current;
    }
}
