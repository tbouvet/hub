/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.rest.detail.ComponentDetailsAssembler;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;

@RunWith(SeedITRunner.class)
public class ComponentDetailsAssemblerIT {

    @Inject
    private ComponentDetailsAssembler assembler;

    @Test @Ignore
    public void testAssemble() throws Exception {
//        ComponentDetails detail = assembler.assembleDtoFromAggregate(MockBuilder.mock(2));
//        assertThat(detail).isNotNull();
//        assertThat(detail.getName()).isEqualTo("Component1");
    }
}
