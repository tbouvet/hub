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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A special kind of text document which keep tracks of all its changes through revisions.
 */
public class WikiDocument extends TextDocument {
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

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
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
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        String textResult = "";

        for (Revision revision : revisions) {
            if (revision.getIndex() > revisionIndex) {
                break;
            }

            Object[] patchResult = diffMatchPatch.patchApply(new LinkedList<>(diffMatchPatch.patchFromText(revision.getPatch())), textResult);
            if (!all((boolean[]) patchResult[1])) {
                throw new DocumentException("Unable to revert to revision " + revisionIndex + ": unable to apply patch of revision " + revision.getIndex());
            } else {
                textResult = (String) patchResult[0];
            }
        }

        addRevision(textResult, author, message);
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
     * Delete all revisions older than the specified threshold.
     *
     * @param threshold the date before which revisions are deleted.
     */
    public void pruneOldRevisions(LocalDate threshold) {
        for (Iterator<Revision> iterator = revisions.iterator(); iterator.hasNext(); ) {
            Revision revision = iterator.next();
            if (revision.getDate().isBefore(threshold)) {
                iterator.remove();
            }
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

    private boolean all(boolean[] values) {
        for (boolean value : values) {
            if (!value) {
                return false;
            }
        }

        return true;
    }
}
