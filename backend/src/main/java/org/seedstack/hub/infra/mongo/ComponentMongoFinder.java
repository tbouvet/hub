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
import org.seedstack.business.view.ChunkedView;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.hub.rest.component.list.ComponentFinder;
import org.seedstack.hub.rest.component.list.SortType;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

class ComponentMongoFinder extends AbstractMongoFinder implements ComponentFinder {

    private static final String RECENT_ORDER = "-releases.date";
    private static final String STARS_ORDER = "-stars";
    private static final String NAME_ORDER = "name";

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Result<ComponentCard> findPublishedCards(Range range, SortType sort, String searchName) {
        Query<Component> query = datastore.find(Component.class);
        addSort(sort, query);
        if (searchName != null && !"".equals(searchName)) {
            query = query.search(searchName);
        }
        query.field("state").equal(State.PUBLISHED);
        return findComponentCards(query, range);
    }

    private void addSort(SortType sort, Query<Component> query) {
        if (sort != null) {
            switch (sort) {
                case DATE:
                    query.order(RECENT_ORDER);
                    break;
                case NAME:
                    query.order(NAME_ORDER);
                    break;
                case STARS:
                    query.order(STARS_ORDER);
                    break;
            }
        } else {
            query.order(STARS_ORDER);
        }
    }

    private Result<ComponentCard> findComponentCards(Query<Component> query, Range range) {
        List<Component> list = paginateQuery(query, range);
        List<ComponentCard> cards = fluentAssembler.assemble(list).to(ComponentCard.class);
        return new Result<>(cards, range.getOffset(), query.countAll());
    }

    @Override
    public Result<ComponentCard> findCardsByState(Range range, State state) {
        Query<Component> query = datastore.find(Component.class).order(NAME_ORDER).field("state").equal(state);
        return findComponentCards(query, range);
    }

    @Override
    public Result<Comment> findComments(ComponentId componentId, Range range) {
        Component component = datastore.find(Component.class)
                .field("_id.name").equal(componentId.getName())
                .order("-comments.publicationDate")
                .retrievedFields(true, "comments")
                .get();
        if (component != null) {
            ChunkedView<Comment> chunkedView = new ChunkedView<>(component.getComments(), range.getOffset(), range.getSize());
            return Result.rangeResult(chunkedView.getView(), chunkedView.getChunkOffset(), chunkedView.getResultSize());
        } else {
            return new Result<>(new ArrayList<>(), 0, 0);
        }
    }

}
