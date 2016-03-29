/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.user;

import org.seedstack.business.finder.Finder;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.list.ComponentCard;

@Finder
public interface UserFinder {

    Result<ComponentCard> findStarred(UserId user, Range range);

    Result<ComponentCard> findUserCards(UserId user, Range range);
}
