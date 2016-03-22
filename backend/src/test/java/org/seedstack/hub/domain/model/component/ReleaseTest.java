/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReleaseTest {

    @Test
    public void testCompareVersion() {
        Version v123m2 = new Version(1, 2, 3, "M2");
        assertThat(v123m2.toString()).isEqualTo("1.2.3-M2");

        Version v123m1 = new Version(1, 2, 3, "M1");
        assertThat(v123m1.toString()).isEqualTo("1.2.3-M1");

        Version v123 = new Version(1, 2, 3, null);
        assertThat(v123.toString()).isEqualTo("1.2.3");

        Version v12m1 = new Version(1, 2, null, "M1");
        assertThat(v12m1.toString()).isEqualTo("1.2-M1");

        Version v1 = new Version(1, null, null, null);
        assertThat(v1.toString()).isEqualTo("1");

        assertThat(v123m2).isGreaterThan(v123m1);
        assertThat(v123).isGreaterThan(v123m1);
        assertThat(v123m1).isGreaterThan(v12m1);
        assertThat(v123).isGreaterThan(v12m1);
        assertThat(v123).isGreaterThan(v1);
    }

    @Test
    public void testCompareRelease() {
        Release release = new Release(new Version(1,0,0, null));
        Release release2 = new Release(new Version(1,0,1, null));
        Assertions.assertThat(release2).isGreaterThan(release);
    }
}
