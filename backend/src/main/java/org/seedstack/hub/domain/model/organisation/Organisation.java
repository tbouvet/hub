/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.organisation;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.UserId;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity("organisations")
public class Organisation extends BaseAggregateRoot<OrganisationId> {
    @Id
    @NotNull
    private OrganisationId id;
    @NotBlank
    private String name;
    @NotEmpty
    private Set<UserId> owners = new HashSet<>();
    private Set<UserId> members = new HashSet<>();
    private Set<ComponentId> components = new HashSet<>();

    public Organisation(OrganisationId id, String name, Set<UserId> owners) {
        this.id = id;
        this.name = name;
        this.owners = owners;
    }

    private Organisation() {
        // required by morphia
    }

    @Override
    public OrganisationId getEntityId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public Set<UserId> getOwners() {
        return owners;
    }

    public void addOwner(UserId user) {
        this.owners.add(user);
    }

    public void removeOwner(UserId user) {
        this.owners.remove(user);
    }

    public boolean isOwner(UserId user) {
        return this.owners.contains(user);
    }

    public Set<UserId> getMembers() {
        return members;
    }

    public void addMember(UserId user) {
        this.members.add(user);
    }

    public void removeMember(UserId user) {
        this.members.remove(user);
    }

    public boolean isMember(UserId user) {
        return this.members.contains(user);
    }

    public Set<ComponentId> getComponents() {
        return components;
    }

    public void addComponent(ComponentId components) {
        this.components.add(components);
    }

    public void removeComponent(ComponentId components) {
        this.components.remove(components);
    }
}
