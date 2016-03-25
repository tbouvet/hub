package org.seedstack.hub.rest.user;

import org.seedstack.business.finder.Finder;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.list.ComponentCard;

@Finder
public interface UserFinder {

    Result<ComponentCard> findStarred(Range range);

    Result<ComponentCard> findUserCards(UserId user, Range range);
}
