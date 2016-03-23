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
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.rest.organisation.OrganisationCard;
import org.seedstack.hub.rest.organisation.OrganisationFinder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.List;

public class OrganisationMongoFinder extends AbstractMongoFinder implements OrganisationFinder {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Result<OrganisationCard> findOrganisation(Range range) {
        Query<Organisation> queryComponent = datastore.find(Organisation.class).order("name");
        return findOrganisationCards(queryComponent, range);
    }

    private Result<OrganisationCard> findOrganisationCards(Query<Organisation> query, Range range) {
        List<Organisation> list = paginateQuery(query, range);
        List<OrganisationCard> cards = fluentAssembler.assemble(list).to(OrganisationCard.class);
        return new Result<>(cards, range.getOffset(), query.countAll());
    }
}
