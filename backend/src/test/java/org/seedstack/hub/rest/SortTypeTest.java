/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.hub.rest.component.list.SortType;

public class SortTypeTest {

    @Test
    public void testValid() {
        Assertions.assertThat(SortType.fromOrDefault("DATE")).isEqualTo(SortType.DATE);
        Assertions.assertThat(SortType.fromOrDefault("date")).isEqualTo(SortType.DATE);
    }
    @Test
    public void testDefaultValue() {
        Assertions.assertThat(SortType.fromOrDefault("")).isEqualTo(SortType.NAME);
        Assertions.assertThat(SortType.fromOrDefault(null)).isEqualTo(SortType.NAME);
    }

    @Test
    public void testInvalid() {
        try {
            SortType.fromOrDefault("zzz");
            Assertions.failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The sort value can only be: DATE, NAME or STARS");
        }
    }
}
