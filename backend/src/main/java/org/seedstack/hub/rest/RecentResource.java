package org.seedstack.hub.rest;

import org.seedstack.business.view.PaginatedView;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * Created by kavi87 on 07/03/2016.
 */

@Path("/recent")
public class RecentResource {

    public static final String RECENT = "recent";
    public static final short QUANTITY = 6;

    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private RelRegistry relRegistry;

    @Rel(value = RECENT, home = true)
    @GET
    @Produces({"application/json", "application/hal+json" })
    public HalRepresentation getRecentCards() {
        HalRepresentation halRepresentation = new HalRepresentation();
        halRepresentation.embedded(RECENT, componentFinder.findRecentCards(QUANTITY));
        halRepresentation.link("self", relRegistry.uri(RECENT).expand());

        return halRepresentation;
    }
}
