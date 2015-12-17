/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.component;

import java.util.Date;

import org.seedstack.business.domain.BaseAggregateRoot;

public class Version extends BaseAggregateRoot<VersionId> {

	private VersionId versionId;

	private Date publicationDate;

	private String url;

	@Override
	public VersionId getEntityId() {
		return versionId;
	}

	public VersionId getVersionId() {
		return versionId;
	}

	public void setVersionId(VersionId versionId) {
		this.versionId = versionId;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
