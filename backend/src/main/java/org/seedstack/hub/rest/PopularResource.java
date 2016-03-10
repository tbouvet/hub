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

@Path("/popular")
public class PopularResource {

    public static final String POPULAR = "popular";
    public static final short QUANTITY = 6;

    @Inject
    private ComponentFinder componentFinder;
    @Inject
    private RelRegistry relRegistry;

    @Rel(value = POPULAR, home = true)
    @GET
    @Produces({"application/json", "application/hal+json"})
    public HalRepresentation getPopularCards() {
        HalRepresentation halRepresentation = new HalRepresentation();
        halRepresentation.embedded(POPULAR, componentFinder.findPopularCards(QUANTITY));
        halRepresentation.link("self", relRegistry.uri(POPULAR).expand());

        return halRepresentation;
    }
}
