/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.domain.model.component.Source;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

@Provider
public class ImportExceptionMapper implements ExceptionMapper<ImportException> {

    @Override
    public Response toResponse(ImportException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getSource(), exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).type(APPLICATION_JSON_TYPE).entity(errorMessage).build();
    }

    static class ErrorMessage {
        String url;
        String sourceType;
        String message;

        public ErrorMessage(Source source, String message) {
            if (source != null) {
                this.url = source.getUrl();
                this.sourceType = source.getSourceType().name();
            }
            this.message = message;
        }

        public String getUrl() {
            return url;
        }

        public String getSourceType() {
            return sourceType;
        }

        public String getMessage() {
            return message;
        }
    }
}
