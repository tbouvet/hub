/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.document;

import java.util.List;

public class WikiPage {
    private String title;
    private List<WikiPageRevision> revisions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WikiPageRevision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<WikiPageRevision> revisions) {
        this.revisions = revisions;
    }
}
