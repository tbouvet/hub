/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.mongodb.morphia.BaseMorphiaRepository;

import java.util.Optional;

class MongoUserRepository extends BaseMorphiaRepository<User, UserId> implements UserRepository {
    @Override
    public Optional<User> findByName(String name) {
        return Optional.ofNullable(load(new UserId(name)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(getDatastore().createQuery(User.class).field("email").equal(email).get());
    }
}
