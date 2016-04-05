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
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.hub.rest.user.UserFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

class UserMongoFinder extends AbstractMongoFinder implements UserFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Result<ComponentCard> findUserCards(UserId user, Range range) {
        Query<Component> query = datastore.find(Component.class).order("name");

        List<Criteria> criteria = addCriteriaForOwnerOrMaintainer(user.getId(), query);
        addCriteriaForOrganisation(user.getId(), query, criteria);
        query.or(criteria.toArray(new Criteria[criteria.size()]));

        return findComponentCards(query, range);
    }

    @Override
    public Result<ComponentCard> findStarred(UserId userId, Range range) {
        User user  = datastore.get(User.class, userId);
        List<String> componentNames = user.getStarred().stream().map(ComponentId::getName).collect(toList());
        Query<Component> query = datastore.find(Component.class, "_id.name in", componentNames).order("name");
        return findComponentCards(query, range);
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

    private Result<ComponentCard> findPublishedComponentCards(Query<Component> query, Range range) {
        query.field("state").equal(State.PUBLISHED);
        return findComponentCards(query, range);
    }

    private Result<ComponentCard> findComponentCards(Query<Component> query, Range range) {
        List<Component> list = paginateQuery(query, range);
        List<ComponentCard> cards = fluentAssembler.assemble(list).to(ComponentCard.class);
        return new Result<>(cards, range.getOffset(), query.countAll());
    }
}
