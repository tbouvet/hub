package org.seedstack.hub.rest.shared;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.seedstack.business.finder.Result;
import org.seedstack.seed.rest.hal.Link;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultHalTest {

    @Test
    public void testHalResult() {
        ArrayList<String> values = Lists.newArrayList("foo", "bar");
        Result<String> result = new Result<>(values, 0, 20);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link("/path"));

        assertThat(resultHal.getResultSize()).isEqualTo(20);
        assertThat(resultHal.getEmbedded().get("val")).isEqualTo(values);
        assertThat(((Link) resultHal.getLink("self")).getHref())
                .isEqualTo(new Link("/path")
                        .set("offset", 0)
                        .set("size", 2)
                        .getHref());
    }

    @Test
    public void testNextLinkPresent() {
        int offset = 1;
        int fullSize = 4;
        Result<String> result = new Result<>(Lists.newArrayList("foo", "bar"), offset, fullSize);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link("/path"));

        assertThat(resultHal.getLink("next")).isNotNull();
        int nextOffset = 3;
        int nextSize = 2;
        assertThat(((Link) resultHal.getLink("next")).getHref())
                .isEqualTo(new Link("/path")
                        .set("offset", nextOffset)
                        .set("size", nextSize)
                        .getHref());
    }

    @Test
    public void testNextLinkWithIncompletePage() {
        Result<String> result = new Result<>(Lists.newArrayList("foo", "bar"), 2, 4);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link("/path"));

        assertThat(resultHal.getLink("next")).isNotNull();
    }

    @Test
    public void testNextLinkNotPresent() {
        Result<String> result = new Result<>(Lists.newArrayList("foo"), 3, 4);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link("/path"));

        assertThat(resultHal.getLink("next")).isNull();
    }

    @Test
    public void testWithEmptyResult() {
        Result<String> result = new Result<>(Lists.newArrayList(), 0, 0);

        ResultHal<String> resultHal = new ResultHal<>("val", result, new Link("/path"));

        assertThat(resultHal.getLink("next")).isNull();
        assertThat(((Link) resultHal.getLink("next")).getHref())
                .isEqualTo(new Link("/path")
                        .set("offset", 0)
                        // TODO find a way to get the actual size asked by the client
                        // here the size is equal to zero because the list of string is empty
                        .set("size", 0)
                        .getHref());
    }
}
