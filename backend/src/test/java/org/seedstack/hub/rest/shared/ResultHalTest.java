/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.shared;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.seedstack.business.finder.Result;
import org.seedstack.seed.rest.hal.Link;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultHalTest {

    private static final String TEMPLATED_URI = "/path{?offset,size}";
    private static final String OFFSET = "offset";
    private static final String SIZE = "size";

    @Test
    public void testHalResult() {
        ArrayList<String> values = Lists.newArrayList("foo", "bar");
        Result<String> result = new Result<>(values, 0, 20);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link(TEMPLATED_URI));

        assertThat(resultHal.getResultSize()).isEqualTo(20);
        assertThat(resultHal.getEmbedded().get("val")).isEqualTo(values);
        assertThat(((Link) resultHal.getLink("self")).getHref())
                .isEqualTo(new Link(TEMPLATED_URI)
                        .set(OFFSET, 0)
                        .set(SIZE, 2)
                        .getHref());
    }

    @Test
    public void testNextLinkPresent() {
        int offset = 1;
        int size = 2;
        int fullSize = 4;
        Result<String> result = new Result<>(Lists.newArrayList("foo", "bar"), offset, fullSize);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link(TEMPLATED_URI));

        assertThat(resultHal.getLink("next")).isNotNull();
        assertThat(((Link) resultHal.getLink("self")).getHref())
                .isEqualTo(new Link(TEMPLATED_URI)
                        .set(OFFSET, offset)
                        .set(SIZE, size)
                        .getHref());
        int nextOffset = 3;
        assertThat(((Link) resultHal.getLink("next")).getHref())
                .isEqualTo(new Link(TEMPLATED_URI)
                        .set(OFFSET, nextOffset)
                        .set(SIZE, size)
                        .getHref());
    }

    @Test
    public void testNextLinkWithIncompletePage() {
        Result<String> result = new Result<>(Lists.newArrayList("foo", "bar"), 2, 4);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link(TEMPLATED_URI));

        assertThat(resultHal.getLink("next")).isNotNull();
    }

    @Test
    public void testNextLinkNotPresent() {
        Result<String> result = new Result<>(Lists.newArrayList("foo"), 3, 4);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link(TEMPLATED_URI));

        assertThat(resultHal.getLink("next")).isNull();
    }

    @Test
    public void testWithEmptyResult() {
        Result<String> result = new Result<>(Lists.newArrayList(), 0, 0);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link(TEMPLATED_URI));

        assertThat(resultHal.getLink("next")).isNull();
    }
}
