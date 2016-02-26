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
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(value = "components")
public class Component extends BaseAggregateRoot<ComponentId> {
    @Id
    @NotNull
    private ComponentId componentId;
    @NotNull
    private UserId owner;
    @NotNull
    private Description description;
    @NotNull
    private State state = State.PENDING;
    @NotNull
    private List<Comment> comments = new ArrayList<>();
    @NotNull
    private List<Version> versions = new ArrayList<>();
    @NotNull
    private Set<DocumentId> docs = new HashSet<>();

    public Component(ComponentId componentId, UserId owner, Description description) {
        this.componentId = componentId;
        this.owner = owner;
        this.description = description;
    }

    private Component() {
        // required by morphia
    }

    @Override
    public ComponentId getEntityId() {
        return componentId;
    }

    public ComponentId getComponentId() {
        return componentId;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void publish() throws ChangeStateException {
        if (state == State.PENDING || state == State.ARCHIVED) {
            state = State.PUBLISHED;
        } else {
            throw new ChangeStateException("Component cannot be published");
        }
    }

    public void archive() throws ChangeStateException {
        if (state == State.PUBLISHED) {
            state = State.ARCHIVED;
        } else {
            throw new ChangeStateException("Component cannot be archived");
        }
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Version> getVersions() {
        return Collections.unmodifiableList(versions);
    }

    public void addVersion(Version version) {
        versions.add(version);
        Collections.sort(versions);
    }

    public UserId getOwner() {
        return owner;
    }
}
