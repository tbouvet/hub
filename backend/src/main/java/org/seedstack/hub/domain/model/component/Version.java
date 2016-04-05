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

import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embedded
public class Version extends BaseValueObject implements Comparable<Version> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(\\.(\\d+))?(\\.(\\d+))?(-(\\w+))?");

    @NotNull
    private Integer majorNumber;
    private Integer minorNumber;
    private Integer microNumber;
    private String qualifier;

    public Version(Integer majorNumber, Integer minorNumber, Integer microNumber, String qualifier) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.microNumber = microNumber;
        this.qualifier = qualifier;
    }

    public Version(String value) {
        Matcher matcher = VERSION_PATTERN.matcher(value);
        if (matcher.matches()) {
            this.majorNumber = Integer.parseInt(matcher.group(1));
            this.minorNumber = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
            this.microNumber = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 0;
            this.qualifier = matcher.group(7);
        } else {
            throw new IllegalArgumentException("Invalid version number " + value);
        }
    }

    private Version() {
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
    public int compareTo(Version that) {
        int result;

        result = compareTo(this.majorNumber, that.majorNumber);
        if (result != 0) {
            return result;
        }
        result = compareTo(minorNumber, that.minorNumber);
        if (result != 0) {
            return result;
        }
        result = compareTo(microNumber, that.microNumber);
        if (result != 0) {
            return result;
        }
        result = compareTo(qualifier, that.qualifier);
        result = versionWithoutQualifierIsGreater(that, result);
        if (result != 0) {
            return result;
        }
        return 0;
    }

    private int versionWithoutQualifierIsGreater(Version that, int result) {
        if (qualifier == null || that.qualifier == null) {
            result = result * -1;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> int compareTo(Comparable<T> i, Comparable<T> j) {
        if (i == null && j == null) {
            return 0;
        } else if (i == null) {
            return -1;
        } else if (j == null) {
            return 1;
        }
        return i.compareTo((T) j);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(majorNumber);
        if (minorNumber != null) {
            stringBuilder.append(".").append(minorNumber);
        }
        if (microNumber != null) {
            stringBuilder.append(".").append(microNumber);
        }
        if (qualifier != null) {
            stringBuilder.append("-").append(qualifier);
        }
        return stringBuilder.toString();
    }
}
