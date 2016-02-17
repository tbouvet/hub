/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;

public class VersionId extends BaseValueObject {
    private final Integer majorNumber;
    private final Integer minorNumber;
    private final Integer microNumber;
    private final String qualifier;

    public VersionId(Integer majorNumber, Integer minorNumber, Integer microNumber, String qualifier) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.microNumber = microNumber;
        this.qualifier = qualifier;
    }

    public Integer getMajorNumber() {
        return majorNumber;
    }


    public Integer getMinorNumber() {
        return minorNumber;
    }


    public Integer getMicroNumber() {
        return microNumber;
    }


    public String getQualifier() {
        return qualifier;
    }

}
