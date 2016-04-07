/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.junit.Test;
import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.services.fetch.SourceType;
import org.seedstack.hub.rest.ImportExceptionMapper.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.fest.assertions.Assertions.assertThat;

public class ImportExceptionMapperTest {

    private ExceptionMapper<ImportException> underTest = new ImportExceptionMapper();

    @Test
    public void testMapException() {
        ImportException exception = new ImportException("Component 1 not found");
        exception.setSource(new Source(SourceType.GIT, "seedstack/component1"));

        Response response = underTest.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);

        expectMessage(response, "GIT", "seedstack/component1", "Component 1 not found");
    }

    private void expectMessage(Response response, String type, String url, String message) {
        ErrorMessage entity = (ErrorMessage) response.getEntity();
        assertThat(entity.getSourceType()).isEqualTo(type);
        assertThat(entity.getUrl()).isEqualTo(url);
        assertThat(entity.getMessage()).isEqualTo(message);
    }

    @Test
    public void testMapExceptionWithoutSourceAndMessage() {
        Response response = underTest.toResponse(new ImportException(new IllegalArgumentException()));

        expectMessage(response, null, null, "java.lang.IllegalArgumentException");
    }

    @Test
    public void testMapExceptionWithoutMessage() {
        Source source = new Source(SourceType.GIT, "seedstack/component1");
        ImportException exception = new ImportException(new IllegalArgumentException(), source);

        Response response = underTest.toResponse(exception);

        expectMessage(response, "GIT", "seedstack/component1", "java.lang.IllegalArgumentException");
    }
}
