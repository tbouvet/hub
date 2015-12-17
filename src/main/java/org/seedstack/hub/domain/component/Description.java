/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.component;

import java.util.List;

import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.document.DocumentId;

public class Description extends BaseValueObject {

	private static final long serialVersionUID = 4824650265631927581L;

	private DocumentId summary;
	
	private List<DocumentId> images;
	
	private DocumentId readme;
	
	private DocumentId icon;

	public DocumentId getSummary() {
		return summary;
	}

	public void setSummary(DocumentId summary) {
		this.summary = summary;
	}

	public List<DocumentId> getImages() {
		return images;
	}

	public void setImages(List<DocumentId> images) {
		this.images = images;
	}

	public DocumentId getReadme() {
		return readme;
	}

	public void setReadme(DocumentId readme) {
		this.readme = readme;
	}

	public DocumentId getIcon() {
		return icon;
	}

	public void setIcon(DocumentId icon) {
		this.icon = icon;
	}
	
	
}
