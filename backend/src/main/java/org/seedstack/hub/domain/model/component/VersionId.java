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

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionId extends BaseValueObject implements Comparable<VersionId> {
    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(\\.(\\d+))?(\\.(\\d+))?(-(\\w+))?");

    @NotNull
    private Integer majorNumber;
    private Integer minorNumber;
    private Integer microNumber;
    private String qualifier;

    public VersionId(Integer majorNumber, Integer minorNumber, Integer microNumber, String qualifier) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.microNumber = microNumber;
        this.qualifier = qualifier;
    }

    public VersionId(String value) {
        Matcher matcher = VERSION_PATTERN.matcher(value);
        if (matcher.matches()) {
            this.majorNumber = Integer.parseInt(matcher.group(0));
            this.minorNumber = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
            this.microNumber = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
            this.qualifier = matcher.group(6);
        } else {
            throw new IllegalArgumentException("Invalid version number " + value);
        }
    }

    private VersionId() {
        // required by morphia
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


    @Override
    public int compareTo(VersionId that) {
        int result;

        result = this.majorNumber.compareTo(that.majorNumber);
        if (result != 0) {
            return result;
        }

        result = this.minorNumber.compareTo(that.minorNumber);
        if (result != 0) {
            return result;
        }

        result = this.microNumber.compareTo(that.microNumber);
        if (result != 0) {
            return result;
        }

        result = this.qualifier.compareTo(that.qualifier);
        if (result != 0) {
            return result;
        }

        return 0;
    }
}
