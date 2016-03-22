/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.list;

import org.seedstack.business.finder.Finder;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.domain.model.component.Comment;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.user.UserId;

@Finder
public interface ComponentFinder {

    PaginatedView<ComponentCard> findCards(Page page, String searchName, String sort);

    PaginatedView<ComponentCard> findUserCards(UserId user, Page page);

    PaginatedView<ComponentCard> findCardsByState(Page page, State state);

    PaginatedView<Comment> findComments(ComponentId componentId, Page page);

    Result<ComponentCard> findRecentCards(Range range);

    Result<ComponentCard> findPopularCards(Range range);
}
