/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.seedstack.business.finder.Finder;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.ComponentId;

@Finder
public interface ComponentFinder {

    PaginatedView<ComponentCard> findCards(Page page, String searchName, String sort);

    PaginatedView<Comment> findComments(ComponentId componentId, Page page);

    Result<ComponentCard> findRecentCards(int howMany);

    Result<ComponentCard> findPopularCards(int howMany);
}
