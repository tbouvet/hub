/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.user;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.hub.domain.model.component.ComponentId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity("users")
public class User extends BaseAggregateRoot<UserId> {
    @NotNull
    @Id
    private UserId userId;

    private byte[] icon;

    private Set<String> emails = new HashSet<>();

    private List<ComponentId> starred = new ArrayList<>();

    public User(UserId userId, String email) {
        this.userId = userId;
        addEmail(email);
    }
    public User(UserId userId) {
        this.userId = userId;
    }

    private User() {
        // required by morphia
    }

    @Override
    public UserId getEntityId() {
        return userId;
    }

    public UserId getId() {
        return userId;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public void addEmail(String email) {
        emails.add(email.toLowerCase());
    }
    public void removeEmail(String email) {
        emails.remove(email.toLowerCase());
    }

    public Set<String> getEmails() {
        return emails;
    }

    public List<ComponentId> getStarred() {
        return starred;
    }

    public void star(ComponentId componentId) {
        this.starred.add(0, componentId);
    }

    public boolean hasStarred(ComponentId componentId) {
        return this.starred.contains(componentId);
    }

    public boolean unstar(ComponentId componentId) {
        return this.starred.remove(componentId);
    }
}
