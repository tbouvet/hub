/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;

public class ComponentId extends BaseValueObject {
    public static final ComponentId DEFAULT_COMPONENT = new ComponentId("default");

    private String name;

    ComponentId() {
        // required by morphia
    }

    public ComponentId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("ComponentId{name='%s'}", name);
    }
}
