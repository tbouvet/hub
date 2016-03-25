/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.shared;

import org.seedstack.business.finder.Range;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class RangeInfo {

    private static final String DEFAULT_OFFSET = "0";
    private static final String DEFAULT_SIZE = "12";
    static final String OFFSET = "offset";
    static final String SIZE = "size";

    @Min(0)
    @QueryParam(OFFSET)
    @DefaultValue(DEFAULT_OFFSET)
    private long offset;

    @Min(1)
    @QueryParam(SIZE)
    @DefaultValue(DEFAULT_SIZE)
    private long size;

    public RangeInfo() {
    }

    public RangeInfo(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }

    public Range range() {
        return new Range(offset, size);
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }
}
