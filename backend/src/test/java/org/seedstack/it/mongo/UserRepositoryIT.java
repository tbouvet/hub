/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class UserRepositoryIT {

    @Inject
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        assertThat(userRepository.findByName("admin")).isEqualTo(userRepository.findByEmail("admin@email.com"));
        assertThat(userRepository.findByName("user1")).isNotEqualTo(userRepository.findByEmail("admin@email.com"));
        assertThat(userRepository.findByName("zzz").isPresent()).isFalse();
    }
    @Test
    public void testFindByEmailWithMultipleEmails() {
        assertThat(userRepository.findByName("simple")).isEqualTo(userRepository.findByEmail("simple@email.com"));
        assertThat(userRepository.findByName("simple")).isEqualTo(userRepository.findByEmail("simple@email.org"));
    }
}
