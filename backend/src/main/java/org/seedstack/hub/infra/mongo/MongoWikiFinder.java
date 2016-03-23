/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.mongodb.morphia.Datastore;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.rest.document.WikiFinder;
import org.seedstack.hub.rest.document.WikiPage;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;
import java.util.List;

class MongoWikiFinder extends AbstractMongoFinder implements WikiFinder {
    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Override
    public List<WikiPage> findComponentPages(ComponentId componentId) {
//        Query<WikiDocument> query = datastore.find(WikiDocument.class).criteria(componentId);
//        return findOrganisationCards(query, range);
        return null;
    }
}
