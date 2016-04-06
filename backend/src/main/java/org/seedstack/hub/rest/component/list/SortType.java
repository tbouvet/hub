/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.component.list;

public enum SortType {
    DATE,
    NAME,
    STARS;

    public static SortType fromOrDefault(String sort) {
        if (sort != null && !sort.equals("")) {
            try {
                return SortType.valueOf(sort.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The sort value can only be: DATE, NAME or STARS");
            }
        } else {
            return SortType.NAME;
        }
    }
}
