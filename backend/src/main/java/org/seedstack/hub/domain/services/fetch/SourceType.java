/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.fetch;

public enum SourceType {
    GIT,
    SVN,
    GITHUB;

    public String qualifier() {
        return this.name().toLowerCase();
    }

    public static SourceType from(String value) {
        if (value == null || value.equals("")) {
            throw new IllegalArgumentException("Missing VCS type.");
        }
        try {
            return SourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported VCS type " + value);
        }
    }
}
