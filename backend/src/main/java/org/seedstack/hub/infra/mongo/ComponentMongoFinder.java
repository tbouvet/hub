/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Projection;
import org.mongodb.morphia.query.Query;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.rest.ComponentCard;
import org.seedstack.hub.rest.ComponentFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.seedstack.business.assembler.AssemblerTypes.MODEL_MAPPER;

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
        return executeQuery(query, page);
    }

    private PaginatedView<ComponentCard> executeQuery(Query<Component> query, Page page) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        return new PaginatedView<>(executeQuery(query, range), page);
    }

    private Result<ComponentCard> executeQuery(Query<Component> query, Range range) {
        List<Component> list = paginateQuery(query, range);
        List<ComponentCard> cards = fluentAssembler.assemble(list).with(MODEL_MAPPER).to(ComponentCard.class);
        return new Result<>(cards, range.getOffset(), query.countAll());
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
    public Result<ComponentCard> findRecentCards(int howMany) {
        Query<Component> queryComponent = datastore.find(Component.class).order("-versions.publicationDate");
        return executeQuery(queryComponent, new Range(0, howMany));
    }

    @Override
    public Result<ComponentCard> findPopularCards(int howMany) {
        Query<Component> query = datastore.find(Component.class).order("-stars");
        return executeQuery(query, new Range(0, howMany));
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
