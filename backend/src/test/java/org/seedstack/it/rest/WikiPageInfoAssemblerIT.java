/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.MockBuilder;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.document.WikiPageInfo;
import org.seedstack.hub.rest.document.WikiPageInfoAssembler;
import org.seedstack.hub.rest.document.WikiPageRevision;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class WikiPageInfoAssemblerIT {

    @Inject
    private WikiPageInfoAssembler assembler;

    @Test
    @SuppressWarnings("unchecked")
    public void testAssemble() throws Exception {
        WikiPageInfo wikiPageInfo = assembler.assembleDtoFromAggregate(MockBuilder.mockWikiDocument(MockBuilder.mock(1), "page1", "a", "b", "c"));
        assertThat(wikiPageInfo).isNotNull();
        assertThat(getRevisions(wikiPageInfo)).hasSize(3);
        assertThat(getRevisions(wikiPageInfo).get(0).getMessage()).isEqualTo("rev0");
        assertThat(getRevisions(wikiPageInfo).get(1).getMessage()).isEqualTo("rev1");
        assertThat(getRevisions(wikiPageInfo).get(2).getMessage()).isEqualTo("rev2");
    }

    @SuppressWarnings("unchecked")
    private List<WikiPageRevision> getRevisions(WikiPageInfo wikiPageInfo) {
        return (List<WikiPageRevision>) wikiPageInfo.getEmbedded().get(Rels.WIKI_REVISIONS);
    }
}
