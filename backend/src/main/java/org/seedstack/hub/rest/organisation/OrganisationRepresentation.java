/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.organisation;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.seedstack.hub.rest.component.list.ComponentCard;
import org.seedstack.seed.rest.hal.HalRepresentation;

import java.util.List;

public class OrganisationRepresentation extends HalRepresentation {

    @NotBlank
    @Length(max = 64)
    private String id;
    @NotBlank
    @Length(max = 64)
    private String name;
    @NotEmpty
    private List<String> owners;
    private List<String> members;
    private List<ComponentCard> components;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<ComponentCard> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentCard> components) {
        this.components = components;
    }
}
