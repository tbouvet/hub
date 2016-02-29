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
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.rest.ComponentCard;
import org.seedstack.hub.rest.ComponentFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.List;

public class ComponentMongoFinder implements ComponentFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName="hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public PaginatedView<ComponentCard> findCards(Page page, String searchName, String sort) {
        return executeQuery(datastore.find(Component.class), page, searchName);
    }

    private PaginatedView<ComponentCard> executeQuery(Query<Component> query, Page page, String searchName) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        if (searchName != null && !"".equals(searchName)) {
            query = query.field("_id.name").containsIgnoreCase(searchName);
        }
        List<ComponentCard> cards = fluentAssembler.assemble(paginateQuery(query, range)).to(ComponentCard.class);
        return toPaginatedView(cards, query.countAll(), page);
    }

    @Override
    public PaginatedView<ComponentCard> findRecentCards(Page page, String searchName, String sort) {
        Query<Component> query = datastore.find(Component.class).order("-publishDate");
        return executeQuery(query, page, searchName);
    }

    @Override
    public PaginatedView<ComponentCard> findPopularCards(Page page, String searchName, String sort) {
        Query<Component> query = datastore.find(Component.class).order("-stars");
        return executeQuery(query, page, searchName);
    }

    private List<Component> paginateQuery(Query<Component> query, Range range) {
        return query.offset(longToInt(range.getOffset()))
                .limit(longToInt(range.getSize()))
                .asList();
    }

    private int longToInt(long value) {
        return new Long(value).intValue();
    }


    private PaginatedView<ComponentCard> toPaginatedView(List<ComponentCard> cards, long totalItems, Page page) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        Result<ComponentCard> result = new Result<>(cards, range.getOffset(), totalItems);
        return new PaginatedView<>(result,page.getCapacity(), page.getIndex());
    }
}
