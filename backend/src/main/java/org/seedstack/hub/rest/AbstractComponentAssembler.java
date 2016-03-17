/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.seedstack.business.assembler.modelmapper.ModelMapperAssembler;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.rest.shared.UriBuilder;

public abstract class AbstractComponentAssembler<DTO> extends ModelMapperAssembler<Component, DTO> {
    @Override
    protected void configureAssembly(ModelMapper modelMapper) {
        modelMapper.addConverter(new Converter<DocumentId, String>() {
            @Override
            public String convert(MappingContext<DocumentId, String> mappingContext) {
                return AbstractComponentAssembler.this.documentIdToString(mappingContext.getSource());
            }
        });

        modelMapper.addMappings(providePropertyMap());
    }

    @Override
    protected void configureMerge(ModelMapper modelMapper) {
        // not used
    }

    protected abstract PropertyMap<Component, DTO> providePropertyMap();

    private String documentIdToString(DocumentId documentId) {
        return UriBuilder.uri("components", documentId.getComponentId().toString(), "files", documentId.getPath());
    }
}
