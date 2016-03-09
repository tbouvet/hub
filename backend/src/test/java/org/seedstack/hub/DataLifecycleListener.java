/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub;

import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;

public class DataLifecycleListener implements LifecycleListener {
    @Inject
    UserRepository userRepository;

    @Override
    public void start() {
        userRepository.persist(new User(new UserId("adrienlauer"), "adrien.lauer@mpsa.com"));
        userRepository.persist(new User(new UserId("pith"), "pierre.thirouin@ext.mpsa.com"));
        userRepository.persist(new User(new UserId("kavi87"), "kavi.ramyead@ext.mpsa.com"));
    }

    @Override
    public void stop() {
        userRepository.delete(new UserId("adrienlauer"));
        userRepository.delete(new UserId("pith"));
        userRepository.delete(new UserId("kavi87"));
    }
}
