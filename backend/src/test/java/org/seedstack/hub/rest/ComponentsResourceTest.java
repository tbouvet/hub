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
import org.seedstack.business.domain.Repository;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.hub.application.ImportService;
import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
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
    private ComponentResource undertest;

    @Injectable
    private ComponentFinder componentFinder;
    @Injectable
    private Repository<Component, ComponentId> componentRepository;
    @Injectable
    private RelRegistry relRegistry;
    @Injectable
    private ImportService importService;
    @Injectable
    private FluentAssembler fluentAssembler;
    @Injectable
    private SecurityService securityService;
    @Injectable
    private ServletContext servletContext;

    private String searchName = "foo";
    private String sort = "publishedData";
    private int pageIndex = 0;
    private int pageSize = 10;
    private PageInfo pageInfo = new PageInfo(pageIndex, pageSize);
    private List<ComponentCard> componentCards = IntStream.range(0, 10)
            .mapToObj(i -> new ComponentCard("Component" + i))
            .collect(toList());

    @Before
    public void setUp() throws Exception {
        new Expectations() {{
            componentFinder.findCards(withAny(pageInfo.page()), searchName, sort);
            result = new PaginatedView<>(componentCards, pageSize, pageIndex);
        }};
    }

    @Test
    public void testGetHasEmbeddedComponents() {
        HalRepresentation halRepresentation = undertest.list(searchName, new PageInfo(pageIndex, pageSize), sort);

        assertThat(halRepresentation).isNotNull();
        assertThat(halRepresentation.getEmbedded().get(ComponentResource.COMPONENTS)).isEqualTo(componentCards);
    }
}
