/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.it.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;

@RunWith(SeedITRunner.class)
public class ComponentRepository {

    @Inject
    @MorphiaDatastore(clientName = "client", dbName="hub")
    private Datastore datastore;

    @Test
    public void testRepo() {
        Component component = new Component(new ComponentId("SeedStack Hub"));
        Key<Component> componentKey = datastore.save(component);
        Assertions.assertThat(componentKey).isNotNull();
    }
}
