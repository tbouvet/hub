/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.shared;

import org.seedstack.business.finder.Result;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.rest.hal.Link;

public class ResultHal<T> extends HalRepresentation {

    private long resultSize;

    public ResultHal() {
        // keep it for jackson
    }

    public ResultHal(String name, Result<T> result, Link selfBuilder) {
        embedded(name, result.getResult());

        resultSize = result.getFullSize();

        self(selfBuilder
                .set(RangeInfo.OFFSET, result.getOffset())
                .set(RangeInfo.SIZE, result.getResult().size()));

        if (result.getOffset() + 1 < result.getFullSize()) {
            link("next", selfBuilder
                    .set(RangeInfo.OFFSET, result.getOffset() + result.getSize())
                    .set(RangeInfo.SIZE, result.getResult().size()));
        }
    }

    public long getResultSize() {
        return resultSize;
    }
}
