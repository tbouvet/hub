/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.it.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.rest.ComponentCard;
import org.seedstack.hub.rest.ComponentFinder;
import org.seedstack.hub.rest.MockedComponentBuilder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class ComponentMongoFinderIT {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName="hub")
    private Datastore datastore;

    @Inject
    private ComponentFinder componentFinder;
    private final List<Component> mockedComponents = IntStream.range(0, 23)
            .mapToObj(MockedComponentBuilder::mock)
            .collect(toList());

    @Before
    public void setUp() throws Exception {
        mockedComponents.forEach(datastore::save);
    }

    @Test
    public void testInjection() {
        assertThat(componentFinder).isNotNull();
    }

    @Test
    public void testFindNotNull() {
        mockedComponents.forEach(datastore::delete); // clean the repo to test without data
        PaginatedView<ComponentCard> componentCards = componentFinder.findCards(new Page(0, 10), null, null);
        assertThat(componentCards).isNotNull();
    }

    @Test
    public void testFindListWithPagination() {
        PaginatedView<ComponentCard> componentCards = componentFinder.findCards(new Page(0, 10), "", "date");
        assertThat(componentCards.getView()).hasSize(10);
        assertThat(componentCards.getPagesCount()).isEqualTo(3);
        componentCards = componentFinder.findCards(new Page(2, 10), "", "date");
        assertThat(componentCards.getView()).hasSize(3);
    }

    @Test
    public void testFindListWithSearchCriteria() {
        PaginatedView<ComponentCard> componentCards = componentFinder.findCards(new Page(0, 20), "ponent1", "date");
        assertThat(componentCards.getView()).hasSize(11);
    }

    @After
    public void tearDown() throws Exception {
        mockedComponents.forEach(datastore::delete);
    }
}
