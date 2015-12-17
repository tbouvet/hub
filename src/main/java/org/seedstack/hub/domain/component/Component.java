/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.component;

import java.util.List;

import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.hub.domain.user.UserId;

public class Component extends BaseAggregateRoot<ComponentId> {

	private ComponentId componentId;

	private State state;

	private Description description;

	private List<Comment> comments;

	private List<Version> versions;

	private UserId userId;

	@Override
	public ComponentId getEntityId() {
		return componentId;
	}

	public ComponentId getComponentId() {
		return componentId;
	}

	public void setComponentId(ComponentId componentId) {
		this.componentId = componentId;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

}
