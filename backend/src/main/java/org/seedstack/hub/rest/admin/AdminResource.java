/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.admin;

import org.seedstack.hub.application.fetch.ImportService;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.seed.security.RequiresRoles;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@Produces({"application/json", "application/hal+json"})
public class AdminResource {

    @Inject
    private ImportService importService;

    @POST
    @RequiresRoles("admin")
    @Path("/import")
    @Consumes({"application/json"})
    public Response importList(List<Source> sources) {
        sources.forEach(importService::importComponent);
        return Response.ok().build();
    }
}
