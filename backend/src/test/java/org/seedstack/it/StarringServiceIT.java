/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.MockBuilder;
import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.security.WithUser;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class StarringServiceIT {

    private final List<Component> mockedComponents = IntStream.range(0, 23)
            .mapToObj(MockBuilder::mock)
            .collect(toList());
    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;
    @Inject
    private StarringService starringService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private Repository<Component, ComponentId> componentRepository;

    @Before
    public void setUp() throws Exception {
        mockedComponents.forEach(datastore::save);
    }

    @WithUser(id = "user3", password = "password")
    @Test
    public void testStarComponent() throws Exception {
        ComponentId c12 = new ComponentId("Component12");
        assertThat(componentRepository.load(c12).getStars()).isEqualTo(13);
        starringService.star(c12);
        assertThat(userRepository.findByName("user3").get().hasStarred(c12)).isTrue();
        assertThat(componentRepository.load(c12).getStars()).isEqualTo(14);
    }

    @WithUser(id = "user3", password = "password")
    @Test
    public void testGetStars() throws Exception {
        starringService.star(new ComponentId("Component1"));
        starringService.star(new ComponentId("Component2"));
        starringService.star(new ComponentId("Component3"));
        List<ComponentId> starred = userRepository.findByName("user3").get().getStarred();
        assertThat(starred).hasSize(3);
        assertThat(starred).containsOnly(new ComponentId("Component1"), new ComponentId("Component2"), new ComponentId("Component3"));
    }

    @WithUser(id = "user3", password = "password")
    @Test
    public void testUnStarComponent() throws Exception {
        ComponentId c13 = new ComponentId("Component13");
        assertThat(componentRepository.load(c13).getStars()).isEqualTo(14);
        starringService.star(c13);
        assertThat(componentRepository.load(c13).getStars()).isEqualTo(15);
        starringService.unstar(c13);
        assertThat(userRepository.findByName("user3").get().hasStarred(c13)).isFalse();
        assertThat(componentRepository.load(c13).getStars()).isEqualTo(14);
    }

    @After
    public void tearDown() throws Exception {
        mockedComponents.forEach(datastore::delete);
    }
}
