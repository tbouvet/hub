/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.component;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.application.StarringService;
import org.seedstack.hub.application.StatePolicy;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.component.detail.ComponentDetails;
import org.seedstack.hub.rest.component.detail.ComponentDetailsAssembler;
import org.seedstack.seed.rest.RelRegistry;

@RunWith(JMockit.class)
public class AbstractComponentAssemblerTest {

    @Injectable
    protected RelRegistry relRegistry;
    @Tested
    private AbstractComponentAssembler<ComponentDetails> underTest;
    @Injectable
    private StatePolicy statePolicy;
    @Injectable
    private StarringService starringService;
    @Mocked
    private Component component;

    private ComponentDetails details;

    @Before
    public void setUp() throws Exception {
        underTest = new ComponentDetailsAssembler();
        details = new ComponentDetails();
    }

    @Test
    public void test_publish_link_for_admins() throws Exception {
        givenState(State.PENDING);
        givenCanPublish(true);

        underTest.doAssembleDtoFromAggregate(details, component);

        Assertions.assertThat(details.getLink(Rels.STATE)).isNotNull();
    }

    private void givenState(State state) {
        new Expectations() {{
            component.getState();
            result = state;
        }};
    }

    private void givenCanPublish(boolean canPublish) {
        new Expectations() {{
            statePolicy.canPublish(component);
            result = canPublish;
        }};
    }

    @Test
    public void test_publish_link_for_user() throws Exception {
        givenState(State.PENDING);
        givenCanPublish(false);

        underTest.doAssembleDtoFromAggregate(details, component);

        Assertions.assertThat(details.getLink(Rels.STATE)).isNull();
    }

    @Test
    public void test_archive_link_for_owner() throws Exception {
        givenState(State.PUBLISHED);
        givenCanArchive(true);

        underTest.doAssembleDtoFromAggregate(details, component);

        Assertions.assertThat(details.getLink(Rels.STATE)).isNotNull();
    }

    private void givenCanArchive(boolean canArchive) {
        new Expectations() {{
            statePolicy.canArchive(component);
            result = canArchive;
        }};
    }

    @Test
    public void test_archive_link_for_user() throws Exception {
        givenState(State.PUBLISHED);
        givenCanArchive(false);

        underTest.doAssembleDtoFromAggregate(details, component);

        Assertions.assertThat(details.getLink(Rels.STATE)).isNull();
    }

    @Test
    public void test_unarchive_link_for_owner() throws Exception {
        givenState(State.ARCHIVED);
        givenCanArchive(true);

        underTest.doAssembleDtoFromAggregate(details, component);

        Assertions.assertThat(details.getLink(Rels.STATE)).isNotNull();
    }

    @Test
    public void test_unarchive_link_for_user() throws Exception {
        givenState(State.ARCHIVED);
        givenCanArchive(false);

        underTest.doAssembleDtoFromAggregate(details, component);

        Assertions.assertThat(details.getLink(Rels.STATE)).isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCannotMerge() throws Exception {
        underTest.doMergeAggregateWithDto(null, null);
    }
}
