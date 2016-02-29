/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.seedstack.business.view.Page;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PageInfo {

    public static final String PAGE_INDEX = "pageIndex";
    public static final String PAGE_SIZE = "pageSize";

    @Min(0)
    @DefaultValue("0")
    @QueryParam(PAGE_INDEX)
    private int pageIndex;

    @Min(1)
    @DefaultValue("10")
    @QueryParam(PAGE_SIZE)
    private int pageSize;

    public PageInfo() {
    }

    public PageInfo(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Page page() {
        return new Page(pageIndex, pageSize);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}