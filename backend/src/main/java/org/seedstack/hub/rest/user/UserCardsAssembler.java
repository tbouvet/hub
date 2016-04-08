/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.user;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.rest.Rels;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;

public class UserCardsAssembler extends BaseAssembler<User, UserCard> {

    private static final String USER_NAME = "userId";

    @Inject
    private RelRegistry relRegistry;

    @Override
    protected void doAssembleDtoFromAggregate(UserCard userCard, User user) {
        String id = user.getEntityId().getId();
        userCard.setName(id);
        userCard.setEmails(user.getEmails());

        userCard.self(relRegistry.uri(Rels.USER).set(USER_NAME, id));
        userCard.link(Rels.USERS_ICON, relRegistry.uri(Rels.USERS_ICON).set(USER_NAME, id));
        userCard.link(Rels.USERS_COMPONENTS, relRegistry.uri(Rels.USERS_COMPONENTS).set(USER_NAME, id));
        userCard.link(Rels.USERS_STARS, relRegistry.uri(Rels.USERS_STARS).set(USER_NAME, id));
    }

    @Override
    protected void doMergeAggregateWithDto(User user, UserCard userCard) {
        userCard.getEmails().forEach(user::addEmail);
    }
}
