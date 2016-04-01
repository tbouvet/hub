/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.fetch;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceTypeTest {

    @Test
    public void testFrom() throws Exception {
        assertThat(SourceType.from("GIT")).isEqualTo(SourceType.GIT);
        assertThat(SourceType.from("git")).isEqualTo(SourceType.GIT);
    }

    @Test
    public void testInvalidThrowException() throws Exception {
        try {
            SourceType.from("cvs");
            Assertions.failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("Unsupported VCS type cvs");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotNull() throws Exception {
        SourceType.from(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotEmpty() throws Exception {
        SourceType.from("");
    }

}