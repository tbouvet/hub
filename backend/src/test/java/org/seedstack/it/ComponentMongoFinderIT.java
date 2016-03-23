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
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.MockBuilder;
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
        mockedComponents.forEach(componentRepository::save);
    }

    @Test
    public void testInjection() {
        assertThat(componentFinder).isNotNull();
    }

    @Test
    public void testFindNotNull() {
        mockedComponents.forEach(componentRepository::delete); // clean the repo to test without data
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

    @Test
    public void testFindRecent() {
        Result<ComponentCard> componentCards = componentFinder.findRecentCards(new Range(0,6));
        List<ComponentCard> recentCards = componentCards.getResult();
        assertThat(recentCards).hasSize(6);
        assertThat(recentCards.get(0).getId()).isEqualToIgnoringCase("Component0");
    }

    @Test
    public void testFindOnlyPublishedRecent() {
        componentRepository.save(MockBuilder.mock("archived", 0, State.ARCHIVED, "zzz"));

        Result<ComponentCard> componentCards = componentFinder.findRecentCards(new Range(0,6));

        // assert archived component is not returned
        List<ComponentCard> recentCards = componentCards.getResult();
        assertThat(recentCards).hasSize(6);
        assertThat(recentCards.get(0).getId()).isEqualToIgnoringCase("Component0");

        componentRepository.delete(new ComponentId("archived0"));
    }

    @Test
    public void testFindCardsByState() {
        componentRepository.save(MockBuilder.mock("archived", 0, State.ARCHIVED, "zzz"));
        componentRepository.save(MockBuilder.mock("pending", 0, State.PENDING, "zzz"));

        PaginatedView<ComponentCard> archived = componentFinder.findCardsByState(new Page(0,10), State.ARCHIVED);
        assertThat(archived.getView()).hasSize(1);
        assertThat(archived.getView().get(0).getId()).isEqualTo("archived0");

        PaginatedView<ComponentCard> pending = componentFinder.findCardsByState(new Page(0,10), State.PENDING);
        assertThat(pending.getView()).hasSize(1);
        assertThat(pending.getView().get(0).getId()).isEqualTo("pending0");

        PaginatedView<ComponentCard> published = componentFinder.findCardsByState(new Page(0,10), State.PUBLISHED);
        assertThat(published.getView()).hasSize(10);
        assertThat(published.getView().get(0).getId()).isEqualTo("Component0");

        componentRepository.delete(new ComponentId("archived0"));
        componentRepository.delete(new ComponentId("pending0"));
    }

    @Test
    public void test_findUserCards_for_owner() {
        PaginatedView<ComponentCard> archived = componentFinder.findUserCards(new UserId("adrienlauer"), new Page(0,10));
        assertThat(archived.getView()).hasSize(10);
        assertThat(archived.getView().get(0).getId()).isEqualTo("Component0");
    }

    @Test
    public void test_findUserCards_for_maintainer() {
        PaginatedView<ComponentCard> archived = componentFinder.findUserCards(new UserId("pith"), new Page(0,10));
        assertThat(archived.getView()).hasSize(10);
        assertThat(archived.getView().get(0).getId()).isEqualTo("Component0");
    }

    @Test
    public void test_findUserCards_retrieve_archived_component() {
        Component mock = MockBuilder.mock("Archived", 0, State.ARCHIVED, "pith");
        componentRepository.save(mock);

        PaginatedView<ComponentCard> archived = componentFinder.findUserCards(new UserId("pith"), new Page(0,10));
        assertThat(archived.getView()).hasSize(10);
        assertThat(archived.getView().get(0).getId()).isEqualTo("Archived0");

        componentRepository.delete(mock);
    }

    @Test
    public void test_findUserCards_include_organisation() {
        Component component = MockBuilder.mock(888, State.PENDING, "@seedstack");
        componentRepository.save(component);

        PaginatedView<ComponentCard> archived = componentFinder.findUserCards(new UserId("admin"), new Page(0,10));
        assertThat(archived.getView()).hasSize(1);
        assertThat(archived.getView().get(0).getId()).isEqualTo("Component888");

        componentRepository.delete(component);
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

        PaginatedView<Comment> componentCards = componentFinder.findComments(c1, new Page(1,2));

        // Expecting to find the last comment, i.e the first published comment
        List<Comment> popular = componentCards.getView();
        assertThat(popular).hasSize(1);
        assertThat(popular.get(0).getAuthor()).isEqualTo("pith");
        assertThat(popular.get(0).getText()).isEqualTo("So cool this component");
        assertThat(popular.get(0).getPublicationDate()).isEqualTo(getDate(LocalDate.of(2015, 10, 11)));
    }

    public Date getDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @After
    public void tearDown() throws Exception {
        mockedComponents.forEach(componentRepository::delete);
    }
}
