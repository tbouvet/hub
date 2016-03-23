/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.document;

import org.seedstack.business.finder.Finder;
import org.seedstack.hub.domain.model.component.ComponentId;

import java.util.List;

@Finder
public interface WikiFinder {

    List<WikiPage> findComponentPages(ComponentId componentId);

}
