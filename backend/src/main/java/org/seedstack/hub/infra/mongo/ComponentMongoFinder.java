/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.list.ComponentCard;
import org.seedstack.hub.rest.list.ComponentFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

class ComponentMongoFinder implements ComponentFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public PaginatedView<ComponentCard> findCards(Page page, String searchName, String sort) {
        Query<Component> query = datastore.find(Component.class);
        if (searchName != null && !"".equals(searchName)) {
            query = query.field("_id.name").containsIgnoreCase(searchName);
        }
        return findPublishedComponentCards(query, page);
    }

    private PaginatedView<ComponentCard> findPublishedComponentCards(Query<Component> query, Page page) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        return new PaginatedView<>(findPublishedComponentCards(query, range), page);
    }

    private Result<ComponentCard> findPublishedComponentCards(Query<Component> query, Range range) {
        query.field("state").equal(State.PUBLISHED);
        return findComponentCards(query, range);
    }

    private Result<ComponentCard> findComponentCards(Query<Component> query, Range range) {
        List<Component> list = paginateQuery(query, range);
        List<ComponentCard> cards = fluentAssembler.assemble(list).to(ComponentCard.class);
        return new Result<>(cards, range.getOffset(), query.countAll());
    }

    @Override
    public PaginatedView<ComponentCard> findCurrentUserCards(UserId user, Page page) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        String userId = user.getId();
        Query<Component> q = datastore.find(Component.class);
        q.or(
                q.criteria("owner").equal(userId),
                q.criteria("maintainers.id").contains(userId)
        );
        return new PaginatedView<>(findComponentCards(q, range), page);
    }

    @Override
    public PaginatedView<ComponentCard> findCardsByState(Page page, State state) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        Query<Component> query = datastore.find(Component.class).field("state").equal(state);
        return new PaginatedView<>(findComponentCards(query, range), page);
    }

    private List<Component> paginateQuery(Query<Component> query, Range range) {
        return query.offset(longToInt(range.getOffset()))
                .limit(longToInt(range.getSize()))
                .asList();
    }

    private int longToInt(long value) {
        return new Long(value).intValue();
    }

    @Override
    public Result<ComponentCard> findRecentCards(Range range) {
        Query<Component> queryComponent = datastore.find(Component.class).order("-versions.publicationDate");
        return findPublishedComponentCards(queryComponent, range);
    }

    @Override
    public Result<ComponentCard> findPopularCards(Range range) {
        Query<Component> query = datastore.find(Component.class).order("-stars");
        return findPublishedComponentCards(query, range);
    }

    @Override
    public PaginatedView<Comment> findComments(ComponentId componentId, Page page) {
        Component component = datastore.find(Component.class)
                .field("_id.name").equal(componentId.getName())
                .order("-comments.publicationDate")
                .retrievedFields(true, "comments")
                .get();
        if (component != null) {
            return new PaginatedView<>(component.getComments(), page.getCapacity(), page.getIndex());
        } else {
            return new PaginatedView<>(new ArrayList<>(), 0, 0);
        }
    }

}
