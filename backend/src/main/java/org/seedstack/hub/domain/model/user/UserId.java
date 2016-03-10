/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.user;

import org.hibernate.validator.constraints.NotBlank;
import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;

import javax.validation.constraints.NotNull;

@Embedded
public class UserId extends BaseValueObject {
    @NotBlank
    private String id;

    public UserId(String id) {
        this.id = id;
    }

    private UserId() {
        // required by morphia
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
