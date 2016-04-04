/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it.mongo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.MockBuilder;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.hub.rest.user.UserFinder;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class UserMongoFinderIT {

    @Inject
    private UserFinder userFinder;
    @Inject
    private Repository<Component, ComponentId> componentRepository;

    private final List<Component> mockedComponents = IntStream.range(0, 23)
            .mapToObj(MockBuilder::mock)
            .collect(toList());

    @Before
    public void setUp() throws Exception {
        mockedComponents.forEach(componentRepository::persist);
    }

    @Test
    public void testInjection() {
        assertThat(userFinder).isNotNull();
    }

    @Test
    public void test_findUserCards_for_owner() {
        Result<ComponentCard> archived = userFinder.findUserComponents(new UserId("adrienlauer"), new Range(0,10));
        assertThat(archived.getResult()).hasSize(10);
        assertThat(archived.getResult().get(0).getId()).isEqualTo("Component0");
    }

    @Test
    public void test_findUserCards_for_maintainer() {
        Result<ComponentCard> archived = userFinder.findUserComponents(new UserId("pith"), new Range(0,10));
        assertThat(archived.getResult()).hasSize(10);
        assertThat(archived.getResult().get(0).getId()).isEqualTo("Component0");
    }

    @Test
    public void test_findUserCards_retrieve_archived_component() {
        Component mock = MockBuilder.mock("Archived", 0, State.ARCHIVED, "pith");
        componentRepository.persist(mock);

        Result<ComponentCard> archived = userFinder.findUserComponents(new UserId("pith"), new Range(0,10));
        assertThat(archived.getResult()).hasSize(10);
        assertThat(archived.getResult().get(0).getId()).isEqualTo("Archived0");

        componentRepository.delete(mock);
    }

    @Test
    public void test_findUserCards_include_organisation() {
        Component component = MockBuilder.mock(888, State.PENDING, "@seedstack");
        componentRepository.persist(component);

        Result<ComponentCard> archived = userFinder.findUserComponents(new UserId("admin"), new Range(0,10));
        assertThat(archived.getResult()).hasSize(1);
        assertThat(archived.getResult().get(0).getId()).isEqualTo("Component888");

        componentRepository.delete(component);
    }

    public Date getDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @After
    public void tearDown() throws Exception {
        mockedComponents.forEach(componentRepository::delete);
    }
}
