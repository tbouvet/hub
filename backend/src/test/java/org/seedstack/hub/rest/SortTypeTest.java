package org.seedstack.hub.rest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.hub.rest.list.SortType;

public class SortTypeTest {

    @Test
    public void testValid() {
        Assertions.assertThat(SortType.fromOrDefault("DATE")).isEqualTo(SortType.DATE);
        Assertions.assertThat(SortType.fromOrDefault("date")).isEqualTo(SortType.DATE);
    }
    @Test
    public void testDefaultValue() {
        Assertions.assertThat(SortType.fromOrDefault("")).isEqualTo(SortType.NAME);
        Assertions.assertThat(SortType.fromOrDefault(null)).isEqualTo(SortType.NAME);
    }

    @Test
    public void testInvalid() {
        try {
            SortType.fromOrDefault("zzz");
            Assertions.failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The sort value can only be: DATE, NAME or STARS");
        }
    }
}
