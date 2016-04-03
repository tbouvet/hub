/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.seedstack.hub.domain.model.user.UserId;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A special kind of text document which keep tracks of all its changes through revisions.
 */
public class WikiDocument extends TextDocument {
    private static final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();

    private final List<Revision> revisions = new ArrayList<>();

    /**
     * Creates a blank Wiki document.
     *
     * @param documentId the document identifier.
     */
    public WikiDocument(DocumentId documentId) {
        super(documentId, TextFormat.MARKDOWN, Charset.forName("UTF-8"));
    }

    private WikiDocument() {
        // required by morphia
    }

    /**
     * Add a revision to the Wiki document.
     *
     * @param text    the new text.
     * @param author  the author of the revision.
     * @param message the message describing the revision.
     */
    public void addRevision(String text, UserId author, String message) {
        String latestRevisionText = "";
        int newRevisionIndex = 0;

        if (!revisions.isEmpty()) {
            newRevisionIndex = revisions.get(revisions.size() - 1).getIndex() + 1;
            latestRevisionText = getText();
        }

        revisions.add(new Revision(
                newRevisionIndex,
                diffMatchPatch.patchToText(
                        diffMatchPatch.patchMake(
                                diffMatchPatch.diffMain(
                                        latestRevisionText,
                                        text
                                )
                        )
                ),
                author,
                message
        ));

        super.setText(text);
    }

    /**
     * Revert the Wiki document to an older revision.
     *
     * @param revisionIndex the revision to revert to.
     * @param author        the author of the revert.
     * @param message       the message describing the reason of the revert.
     */
    public void revertToRevision(int revisionIndex, UserId author, String message) {
        addRevision(applyRevisionsUpTo(revisionIndex), author, message);
    }

    /**
     * Returns a pretty HTML diff between the two specified revisions
     *
     * @param revisionIndex1 the first revision to diff, starting at 0 (-1 to specify the blank document).
     * @param revisionIndex2 the second revision to diff, starting at 0 (must be equal or greater than revisionIndex1).
     * @param simplify       simplify the diff for better readability
     * @return the pretty HTML diff.
     */
    public String diff(int revisionIndex1, int revisionIndex2, boolean simplify) {
        if (revisionIndex1 < -1 || revisionIndex1 >= revisions.size()) {
            throw new DocumentException("Invalid revision index " + revisionIndex1);
        }
        if (revisionIndex2 < -1 || revisionIndex2 >= revisions.size() || revisionIndex2 < revisionIndex1) {
            throw new DocumentException("Invalid revision index " + revisionIndex2);
        }
        String revisionText1 = "";
        if (revisionIndex1 >= 0) {
            revisionText1 = applyRevisionsUpTo(revisionIndex1);
        }

        LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diffMain(revisionText1, applyRevisionsUpTo(revisionIndex2));
        if (simplify) {
            diffMatchPatch.diffCleanupSemantic(diffs);
        }
        return diffMatchPatch.diffPrettyHtml(diffs);
    }

    /**
     * Return all the revisions of the Wiki document in chronological order.
     *
     * @return the list of revisions.
     */
    public List<Revision> getRevisions() {
        return Collections.unmodifiableList(revisions);
    }

    /**
     * Return a specific revision of the Wiki document.
     *
     * @param index the index number of the revision to get, starting with 0 as the oldest revision.
     * @return the requested revision or null if it doesn't exists.
     */
    public Revision getRevision(int index) {
        if (index < revisions.size()) {
            return revisions.get(index);
        } else {
            return null;
        }
    }

    /**
     * This method cannot be used to set the text of a Wiki document. Use {@link #addRevision(String, UserId, String)}
     * instead.
     *
     * @param text the text of the document.
     */
    @Override
    public void setText(String text) {
        throw new IllegalStateException("Cannot directly alter the text in a Wiki document, use addRevision() instead");
    }

    private String applyRevisionsUpTo(int revisionIndex) {
        String textResult = "";
        for (Revision revision : revisions) {
            if (revision.getIndex() > revisionIndex) {
                break;
            }
            textResult = applyRevision(textResult, revision);
        }
        return textResult;
    }

    private String applyRevision(String initialText, Revision revision) {
        Object[] patchResult = diffMatchPatch.patchApply(new LinkedList<>(diffMatchPatch.patchFromText(revision.getPatch())), initialText);
        if (!all((boolean[]) patchResult[1])) {
            throw new DocumentException("Unable to apply patch of revision " + revision.getIndex());
        } else {
            initialText = (String) patchResult[0];
        }
        return initialText;
    }

    private boolean all(boolean[] values) {
        for (boolean value : values) {
            if (!value) {
                return false;
            }
        }
        return true;
    }
}
