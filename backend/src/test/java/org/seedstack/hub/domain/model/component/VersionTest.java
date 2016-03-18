/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest {

    @Test
    public void testVersionId() {
        VersionId v123m2 = new VersionId(1, 2, 3, "M2");
        assertThat(v123m2.toString()).isEqualTo("1.2.3-M2");

        VersionId v123m1 = new VersionId(1, 2, 3, "M1");
        assertThat(v123m1.toString()).isEqualTo("1.2.3-M1");

        VersionId v123 = new VersionId(1, 2, 3, null);
        assertThat(v123.toString()).isEqualTo("1.2.3");

        VersionId v12m1 = new VersionId(1, 2, null, "M1");
        assertThat(v12m1.toString()).isEqualTo("1.2-M1");

        VersionId v1 = new VersionId(1, null, null, null);
        assertThat(v1.toString()).isEqualTo("1");

        assertThat(v123m2).isGreaterThan(v123m1);
        assertThat(v123).isGreaterThan(v123m1);
        assertThat(v123m1).isGreaterThan(v12m1);
        assertThat(v123).isGreaterThan(v12m1);
        assertThat(v123).isGreaterThan(v1);
    }
}
