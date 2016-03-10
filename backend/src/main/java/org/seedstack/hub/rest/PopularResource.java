/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
