/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.modelmapper.PropertyMap;
import org.seedstack.hub.domain.model.component.Component;

@org.seedstack.business.assembler.ModelMapper
public class ComponentCardAssembler extends AbstractComponentAssembler<ComponentCard> {
    @Override
    protected PropertyMap<Component, ComponentCard> providePropertyMap() {
        return new PropertyMap<Component, ComponentCard>() {
            @Override
            protected void configure() {
                map().setName(source.getDescription().getName());
                map().setSummary(source.getDescription().getSummary());
                map(source.getDescription().getIcon()).setIcon(null);
            }
        };
    }
}
