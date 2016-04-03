/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.MockBuilder;
import org.seedstack.hub.rest.document.WikiPageInfo;
import org.seedstack.hub.rest.document.WikiPageInfoAssembler;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class WikiPageInfoAssemblerIT {

    @Inject
    private WikiPageInfoAssembler assembler;

    @Test
    public void testAssemble() throws Exception {
        WikiPageInfo wikiPageInfo = assembler.assembleDtoFromAggregate(MockBuilder.mockWikiDocument(MockBuilder.mock(1), "page1", "a", "b", "c"));
        assertThat(wikiPageInfo).isNotNull();
        assertThat(wikiPageInfo.getRevisions()).hasSize(3);
        assertThat(wikiPageInfo.getRevisions().get(0).getMessage()).isEqualTo("rev0");
        assertThat(wikiPageInfo.getRevisions().get(1).getMessage()).isEqualTo("rev1");
        assertThat(wikiPageInfo.getRevisions().get(2).getMessage()).isEqualTo("rev2");
    }
}
