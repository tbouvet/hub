/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.mongodb.morphia.Datastore;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;

public class DataCleanerLifecycleListener implements LifecycleListener {

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Override
    public void started() {
    }

    @Override
    public void stopping() {
        datastore.getDB().getCollection("components").drop();
        datastore.getDB().getCollection("users").drop();
        datastore.getDB().getCollection("organisations").drop();
        datastore.getDB().getCollection("documents").drop();
    }
}
