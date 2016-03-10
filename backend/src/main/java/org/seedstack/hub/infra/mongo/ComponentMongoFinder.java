/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.CriteriaContainer;
import org.mongodb.morphia.query.CriteriaContainerImpl;
import org.mongodb.morphia.query.Query;
import org.seedstack.business.assembler.AssemblerTypes;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.rest.ComponentCard;
import org.seedstack.hub.rest.ComponentFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.core.utils.SeedLoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

public class ComponentMongoFinder implements ComponentFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentMongoFinder.class);

    @Override
    public PaginatedView<ComponentCard> findCards(Page page, String searchName, String sort) {
        return executeQuery(datastore.find(Component.class), page, searchName);
    }

    private PaginatedView<ComponentCard> executeQuery(Query<Component> query, Page page, String searchName) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        if (searchName != null && !"".equals(searchName)) {
            query = query.field("_id.name").containsIgnoreCase(searchName);
        }
        List<ComponentCard> cards = fluentAssembler.assemble(paginateQuery(query, range)).with(AssemblerTypes.MODEL_MAPPER).to(ComponentCard.class);
        return toPaginatedView(cards, query.countAll(), page);
    }

    @Override
    public PaginatedView<ComponentCard> findRecentCards(int howMany) {
        Query<Component> queryComponent = datastore
                .find(Component.class)
                .order("-versions.publicationDate")
                .limit(howMany);
        return executeQuery(queryComponent, new Page(0, howMany), "");
    }

    @Override
    public PaginatedView<ComponentCard> findPopularCards(int howMany) {
        Query<Component> query = datastore.find(Component.class).order("-stars");
        return executeQuery(query, new Page(0, howMany), "");
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
        return new PaginatedView<>(result, page.getCapacity(), page.getIndex());
    }
}
