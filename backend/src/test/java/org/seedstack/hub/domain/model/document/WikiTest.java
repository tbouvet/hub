/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.junit.Test;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.user.UserId;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WikiTest {

    public static final String FINAL_TEXT = "Hello World!\nChanged this line.";
    public static final String INTERMEDIATE_TEXT = "Hello World!\nAdding a test line";
    public static final String STARTING_TEXT = "Hello World!";

    @Test
    public void empty_wiki_document() {
        WikiDocument wikiDocument = createWikiDocument();
        assertThat(wikiDocument.getRevisions()).isEmpty();
    }

    @Test
    public void wiki_add_revision() throws Exception {
        WikiDocument wikiDocument = createWikiDocument();
        addRevisions(wikiDocument);

        assertThat(wikiDocument.getRevisions()).hasSize(3);
        assertThat(wikiDocument.getText()).isEqualTo(FINAL_TEXT);
        assertThat(applyRevisions(wikiDocument.getRevisions(), "")).isEqualTo(FINAL_TEXT);
    }

    @Test
    public void wiki_revert_revision() throws Exception {
        WikiDocument wikiDocument = createWikiDocument();
        addRevisions(wikiDocument);
        wikiDocument.revertToRevision(1, new UserId("adrien"), "reverted to 1");
        assertThat(wikiDocument.getText()).isEqualTo(INTERMEDIATE_TEXT);
    }

    private WikiDocument createWikiDocument() {
        return new WikiDocument(new DocumentId(new ComponentId("c1"), DocumentScope.WIKI, "/page1"));
    }

    private void addRevisions(WikiDocument wikiDocument) {
        wikiDocument.addRevision(STARTING_TEXT, new UserId("adrien"), "initial version");
        wikiDocument.addRevision(INTERMEDIATE_TEXT, new UserId("adrien"), "added some text");
        wikiDocument.addRevision(FINAL_TEXT, new UserId("adrien"), "changed text");
    }

    private String applyRevisions(List<Revision> revisions, String initialText) {
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        String result = initialText;
        for (Revision revision : revisions) {
            result = (String) (diffMatchPatch.patchApply(new LinkedList<>(diffMatchPatch.patchFromText(revision.getPatch())), result)[0]);
        }
        return result;
    }
}
