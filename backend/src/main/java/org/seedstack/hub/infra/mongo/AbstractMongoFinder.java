/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.mongodb.morphia.query.Query;
import org.seedstack.business.finder.Range;
import org.seedstack.hub.domain.model.component.Component;

import java.util.List;

class AbstractMongoFinder {

    <T> List<T> paginateQuery(Query<T> query, Range range) {
        return query.offset(longToInt(range.getOffset()))
                .limit(longToInt(range.getSize()))
                .asList();
    }

    private int longToInt(long value) {
        return new Long(value).intValue();
    }
}
