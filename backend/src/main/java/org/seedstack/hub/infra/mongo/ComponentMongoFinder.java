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
import org.seedstack.hub.rest.list.ComponentCard;
import org.seedstack.hub.rest.list.ComponentFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

class ComponentMongoFinder extends AbstractMongoFinder implements ComponentFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Result<ComponentCard> findCards(Range range, String searchName, String sort) {
        Query<Component> query = datastore.find(Component.class).order("name");
        if (searchName != null && !"".equals(searchName)) {
            query = query.field("_id.name").containsIgnoreCase(searchName);
        }
        return findPublishedComponentCards(query, range);
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
    public Result<ComponentCard> findCardsByState(Range range, State state) {
        Query<Component> query = datastore.find(Component.class).order("name").field("state").equal(state);
        return findComponentCards(query, range);
    }

    @Override
    public Result<ComponentCard> findRecentCards(Range range) {
        Query<Component> queryComponent = datastore.find(Component.class).order("-releases.date");
        return findPublishedComponentCards(queryComponent, range);
    }

    @Override
    public Result<ComponentCard> findPopularCards(Range range) {
        Query<Component> query = datastore.find(Component.class).order("-stars");
        return findPublishedComponentCards(query, range);
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
