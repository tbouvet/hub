/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.admin;

import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.application.fetch.ImportService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.rest.component.list.ComponentCardAssembler;
import org.seedstack.seed.Logging;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.security.RequiresRoles;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static org.seedstack.hub.rest.Rels.IMPORT_LIST;

@Path("/admin")
@Produces({"application/json", "application/hal+json"})
public class AdminResource {

    @Logging
    private Logger logger;
    @Inject
    private ImportService importService;
    @Inject
    private ComponentCardAssembler assembler;

    @Rel(value = IMPORT_LIST, home = true)
    @POST
    @RequiresRoles("admin")
    @Path("/import")
    @Consumes({"application/json"})
    public Response importList(List<Source> sources) {
        long startTime = System.currentTimeMillis();
        final ImportReport importReport = new ImportReport();
        Future<?> future = Executors.newWorkStealingPool().submit(() ->
                importReport.setComponentCards(
                        sources.parallelStream()
                                .map(source -> {
                                    Component component = null;
                                    try {
                                        component = importService.importComponent(source);
                                    } catch (ImportException e) {
                                        importReport.addFailedSource(e);
                                    }
                                    return component;
                                }).filter(Objects::nonNull)
                                .map(assembler::assembleDtoFromAggregate)
                                .collect(Collectors.toList())
                )
        );
        ResponseBuilder response = Response.noContent();
        try {
            if (future.get() == null) {
                long stopTime = System.currentTimeMillis();
                logger.info(Duration.ofMillis(stopTime - startTime).toString());
                if (importReport.getFailedSources().isEmpty()) {
                    response = Response.ok();
                } else {
                    response = Response.status(ACCEPTED);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException(new ImportException(e));
        }
        return response.entity(importReport).build();
    }
}
