/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application;

import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.security.AuthenticationException;

import javax.inject.Inject;
import java.util.stream.Stream;

class StarringServiceImpl implements StarringService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private SecurityService securityService;

    private User getUser() {
        return securityService.getAuthenticatedUser().orElseThrow(AuthenticationException::new);
    }

    @Override
    public Stream<Component> starredComponents() {
        return getUser().getStarred().stream().map(componentRepository::load);
    }

    @Override
    public void star(ComponentId componentId) {
        Component component = componentRepository.load(componentId);
        User user = getUser();
        if (user != null && component != null) {
            component.star();
            user.star(componentId);
            componentRepository.persist(component);
            userRepository.persist(user);
        }
    }

    @Override
    public void unstar(ComponentId componentId) {
        Component component = componentRepository.load(componentId);
        User user = getUser();
        if (user != null && component != null && user.hasStarred(componentId)) {
            component.unstar();
            user.unstar(componentId);
            componentRepository.persist(component);
            userRepository.persist(user);
        }
    }

    @Override
    public boolean hasStarred(ComponentId componentId) {
        Component component = componentRepository.load(componentId);
        return getUser() != null && component != null && getUser().hasStarred(componentId);
    }
}
