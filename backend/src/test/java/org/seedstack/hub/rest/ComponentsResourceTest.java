/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.application.fetch.ImportService;
import org.seedstack.hub.rest.list.ComponentCard;
import org.seedstack.hub.rest.list.ComponentFinder;
import org.seedstack.hub.rest.list.ComponentsResource;
import org.seedstack.hub.rest.shared.RangeInfo;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class ComponentsResourceTest {

    @Tested
    private ComponentsResource undertest;

    @Injectable
    private ComponentFinder componentFinder;
    @Injectable
    private ServletContext servletContext;
    @Injectable
    private RelRegistry relRegistry;
    @Injectable
    private ImportService importService;
    @Injectable
    private FluentAssembler fluentAssembler;

    private String searchName = "foo";
    private String sort = "publishedData";
    private int offset = 0;
    private int size = 10;
    private RangeInfo rangeInfo = new RangeInfo(offset, size);
    private List<ComponentCard> componentCards = IntStream.range(0, 10)
            .mapToObj(i -> new ComponentCard("Component" + i))
            .collect(toList());

    @Before
    public void setUp() throws Exception {
        new Expectations() {{
            componentFinder.findCards(withAny(rangeInfo.range()), searchName, sort);
            result = new Result<>(componentCards, size, offset);
        }};
    }

    @Test
    public void testGetHasEmbeddedComponents() {
        HalRepresentation halRepresentation = undertest.list(searchName, new RangeInfo(offset, size), sort);

        assertThat(halRepresentation).isNotNull();
        assertThat(halRepresentation.getEmbedded().get(Rels.COMPONENTS)).isEqualTo(componentCards);
    }
}
