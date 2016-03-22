/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import com.google.common.collect.Lists;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;
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
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.list.ComponentCard;
import org.seedstack.hub.rest.list.ComponentFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

class ComponentMongoFinder extends AbstractMongoFinder implements ComponentFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public PaginatedView<ComponentCard> findCards(Page page, String searchName, String sort) {
        Query<Component> query = datastore.find(Component.class).order("name");
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
    public PaginatedView<ComponentCard> findUserCards(UserId user, Page page) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        Query<Component> query = datastore.find(Component.class).order("name");

        List<Criteria> criteria = addCriteriaForOwnerOrMaintainer(user.getId(), query);
        addCriteriaForOrganisation(user.getId(), query, criteria);
        query.or(criteria.toArray(new Criteria[criteria.size()]));

        return new PaginatedView<>(findComponentCards(query, range), page);
    }

    private List<Criteria> addCriteriaForOwnerOrMaintainer(String userName, Query<Component> q) {
        return Lists.newArrayList(
                    q.criteria("owner.userId.id").equal(userName),
                    q.criteria("maintainers.id").equal(userName)
            );
    }

    private void addCriteriaForOrganisation(String userName, Query<Component> q, List<Criteria> criteria) {
        List<Organisation> userOrganisations = getOrganisationsOf(userName);
        List<String> organisationNames = userOrganisations.stream().map(o -> o.getEntityId().getId()).collect(toList());
        if (!organisationNames.isEmpty()) {
            criteria.add(q.criteria("owner.organisationId.id").hasAnyOf(organisationNames));
        }
    }

    private List<Organisation> getOrganisationsOf(String userName) {
        Query<Organisation> orgQuery = datastore.find(Organisation.class);
        orgQuery.or(
                orgQuery.criteria("owners.id").equal(userName),
                orgQuery.criteria("members.id").equal(userName)
        );
        return orgQuery.asList();
    }

    @Override
    public PaginatedView<ComponentCard> findCardsByState(Page page, State state) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        Query<Component> query = datastore.find(Component.class).order("name").field("state").equal(state);
        return new PaginatedView<>(findComponentCards(query, range), page);
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
