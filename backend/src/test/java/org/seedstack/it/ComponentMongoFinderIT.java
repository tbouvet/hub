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
import org.seedstack.business.domain.Repository;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.MockBuilder;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.rest.list.ComponentCard;
import org.seedstack.hub.rest.list.ComponentFinder;
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
public class ComponentMongoFinderIT {

    @Inject
    private ComponentFinder componentFinder;
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
        assertThat(componentFinder).isNotNull();
    }

    @Test
    public void testFindNotNull() {
        mockedComponents.forEach(componentRepository::delete); // clean the repo to test without data
        Result<ComponentCard> componentCards = componentFinder.findCards(new Range(0, 10), null, null);
        assertThat(componentCards).isNotNull();
    }

    @Test
    public void testFindListWithPagination() {
        Result<ComponentCard> componentCards = componentFinder.findCards(new Range(0, 10), "", "date");
        assertThat(componentCards.getResult()).hasSize(10);
        assertThat(componentCards.getFullSize()).isEqualTo(23);
        componentCards = componentFinder.findCards(new Range(20, 10), "", "date");
        assertThat(componentCards.getResult()).hasSize(3);
    }

    @Test
    public void testFindListWithSearchCriteria() {
        Result<ComponentCard> componentCards = componentFinder.findCards(new Range(0, 20), "ponent1", "date");
        assertThat(componentCards.getResult()).hasSize(11);
    }

    @Test
    public void testFindRecent() {
        Result<ComponentCard> componentCards = componentFinder.findRecentCards(new Range(0,6));
        List<ComponentCard> recentCards = componentCards.getResult();
        assertThat(recentCards).hasSize(6);
        assertThat(recentCards.get(0).getId()).isEqualToIgnoringCase("Component0");
    }

    @Test
    public void testFindOnlyPublishedRecent() {
        componentRepository.persist(MockBuilder.mock("archived", 0, State.ARCHIVED, "zzz"));

        Result<ComponentCard> componentCards = componentFinder.findRecentCards(new Range(0,6));

        // assert archived component is not returned
        List<ComponentCard> recentCards = componentCards.getResult();
        assertThat(recentCards).hasSize(6);
        assertThat(recentCards.get(0).getId()).isEqualToIgnoringCase("Component0");

        componentRepository.delete(new ComponentId("archived0"));
    }

    @Test
    public void testFindCardsByState() {
        componentRepository.persist(MockBuilder.mock("archived", 0, State.ARCHIVED, "zzz"));
        componentRepository.persist(MockBuilder.mock("pending", 0, State.PENDING, "zzz"));

        Result<ComponentCard> archived = componentFinder.findCardsByState(new Range(0,10), State.ARCHIVED);
        assertThat(archived.getResult()).hasSize(1);
        assertThat(archived.getResult().get(0).getId()).isEqualTo("archived0");

        Result<ComponentCard> pending = componentFinder.findCardsByState(new Range(0,10), State.PENDING);
        assertThat(pending.getResult()).hasSize(1);
        assertThat(pending.getResult().get(0).getId()).isEqualTo("pending0");

        Result<ComponentCard> published = componentFinder.findCardsByState(new Range(0,10), State.PUBLISHED);
        assertThat(published.getResult()).hasSize(10);
        assertThat(published.getResult().get(0).getId()).isEqualTo("Component0");

        componentRepository.delete(new ComponentId("archived0"));
        componentRepository.delete(new ComponentId("pending0"));
    }

    @Test
    public void testFindPopular() {
        Result<ComponentCard> componentCards = componentFinder.findPopularCards(new Range(0,6));
        List<ComponentCard> popular = componentCards.getResult();
        assertThat(popular).hasSize(6);
        assertThat(popular.get(0).getId()).isEqualToIgnoringCase("Component22");
        assertThat(popular.get(0).getStars()).isEqualTo(23);
        assertThat(popular.get(1).getId()).isEqualToIgnoringCase("Component21");
        assertThat(popular.get(1).getStars()).isEqualTo(22);
    }

    @Test
    public void testFindComments() {
        // Given these 3 comments
        ComponentId c1 = new ComponentId("Component1");
        Component component = componentRepository.load(c1);
        component.addComment(new Comment("pith", "So cool this component", getDate(LocalDate.of(2015, 10, 11))));
        component.addComment(new Comment("kavi87", "Nice version !", getDate(LocalDate.of(2016, 2, 22))));
        component.addComment(new Comment("adrienlauer", "Thanks for this component", getDate(LocalDate.of(2016, 4, 8))));
        componentRepository.persist(component);

        List<Comment> comments = componentFinder.findComments(c1, new Range(2,2)).getResult();

        // Expecting to find the last comment, i.e the first published comment
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getAuthor()).isEqualTo("pith");
        assertThat(comments.get(0).getText()).isEqualTo("So cool this component");
        assertThat(comments.get(0).getPublicationDate()).isEqualTo(getDate(LocalDate.of(2015, 10, 11)));
    }

    public Date getDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @After
    public void tearDown() throws Exception {
        mockedComponents.forEach(componentRepository::delete);
    }
}
