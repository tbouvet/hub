/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.hub.domain.model.user.UserId;

import java.util.List;

@Entity(value = "components")
public class Component extends BaseAggregateRoot<ComponentId> {
    @Id
    private final ComponentId componentId;
    private State state = State.PENDING;
    private Description description;
    private List<Comment> comments;
    private List<Version> versions;
    private UserId userId;

    public Component(ComponentId componentId) {
        this.componentId = componentId;
    }

    @Override
    public ComponentId getEntityId() {
        return componentId;
    }

    public ComponentId getComponentId() {
        return componentId;
    }

    public State getState() {
        return state;
    }

    public Description getDescription() {
        return description;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public UserId getUserId() {
        return userId;
    }

}
