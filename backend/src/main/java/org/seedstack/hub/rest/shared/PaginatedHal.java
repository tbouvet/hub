/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.shared;

import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.seed.rest.hal.HalRepresentation;
import org.seedstack.seed.rest.hal.Link;

public class PaginatedHal<T> extends HalRepresentation {

    private long pageCount;
    private long resultSize;

    public PaginatedHal() {
        // keep it for jackson
    }

    public PaginatedHal(String name, PaginatedView<T> paginatedView, Link selfBuilder) {
        embedded(name, paginatedView.getView());

        pageCount = paginatedView.getPagesCount();
        resultSize = paginatedView.getResultSize();


        link("self", selfBuilder
                .set("pageIndex", paginatedView.getPageIndex())
                .set("pageSize", paginatedView.getPageSize())
                .expand());

        if (paginatedView.hasNext()) {
            Page next = paginatedView.next();
            link("next", new Link(selfBuilder)
                    .set("pageIndex", next.getIndex())
                    .set("pageSize", next.getCapacity())
                    .expand());
        }

        if (paginatedView.hasPrev()) {
            Page prev = paginatedView.prev();
            link("prev", new Link(selfBuilder)
                    .set("pageIndex", prev.getIndex())
                    .set("pageSize", prev.getCapacity())
                    .expand());
        }
    }

    public long getPageCount() {
        return pageCount;
    }

    public long getResultSize() {
        return resultSize;
    }
}
