/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;

@RunWith(SeedITRunner.class)
public class ComponentRepositoryIT {

    @Inject
    private Repository<Component, ComponentId> componentRepository;

    @Test
    public void testRepo() {
        ComponentId componentId = new ComponentId("SeedStack Hub");
        Component component = new Component(componentId, new UserId("me"), buildDescription(componentId));
        componentRepository.persist(component);
        Assertions.assertThat(componentRepository.load(componentId)).isNotNull();
        componentRepository.delete(component);
        Assertions.assertThat(componentRepository.load(componentId)).isNull();
    }

    private Description buildDescription(ComponentId componentId) {
        return new Description("c1", "summary", new DocumentId(componentId, "/icon.png"), new DocumentId(componentId, "readme.md"));
    }
}
