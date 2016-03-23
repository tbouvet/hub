/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.organisation;

import org.seedstack.business.domain.BaseValueObject;

public class OrganisationId extends BaseValueObject {

    private String id;

    public OrganisationId(String id) {
        if (!isValid(id)) {
            throw new IllegalArgumentException("The organisation id \"" + id + "\" should start by @");
        }
        this.id = id;
    }

    private OrganisationId() {
        // required by morphia
    }

    public String getId() {
        return id;
    }

    public static boolean isValid(String organisation) {
        return organisation.startsWith("@");
    }
}
