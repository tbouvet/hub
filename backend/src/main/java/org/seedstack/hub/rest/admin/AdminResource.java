package org.seedstack.hub.rest.admin;

import org.seedstack.hub.application.ImportService;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.seed.security.RequiresRoles;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@Produces({"application/json", "application/hal+json"})
public class AdminResource {

    @Inject
    private ImportService importService;
    @Inject @Named("GITHUB")
    private ImportService importServiceGithub;

    @POST
    @RequiresRoles("admin")
    @Path("/import")
    @Consumes({"application/json"})
    public Response importList(List<Source> sources) {
        for (Source source : sources) {
            if (source.getVcsType() == VCSType.GITHUB) {
                importServiceGithub.importComponent(source);
            } else {
                importService.importComponent(source);
            }
        }
        return Response.ok().build();
    }
}
