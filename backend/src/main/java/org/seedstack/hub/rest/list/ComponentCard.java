/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.list;

import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.rest.shared.DocumentRepresentation;
import org.seedstack.seed.rest.hal.HalRepresentation;

public class ComponentCard extends HalRepresentation {
    private String id;
    private String name;
    private String summary;
    private String owner;
    private int stars;
    private DocumentRepresentation icon;
    private State state;

    public ComponentCard() {
    }

    public ComponentCard(String name) {
        this.name = name;
    }

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public DocumentRepresentation getIcon() {
        return icon;
    }

    public void setIcon(DocumentRepresentation  icon) {
        this.icon = icon;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
