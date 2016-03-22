/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.organisation;

import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.finder.Result;
import org.seedstack.hub.application.OrganisationService;
import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.shared.RangeInfo;
import org.seedstack.hub.rest.shared.ResultHal;
import org.seedstack.seed.rest.Rel;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

@Api
@Rel(value = Rels.ORGANISATIONS, home = true)
@Path("/organisations")
@Produces({"application/json", "application/hal+json"})
public class OrganisationResource {

    public static final String ORGANISATION_ID = "organisationId";

    @Inject
    private OrganisationAssembler organisationAssembler;
    @Inject
    private OrganisationFinder organisationFinder;
    @Inject
    private Repository<Organisation, OrganisationId> organisationRepository;
    @Inject
    private SecurityService securityService;
    @Inject
    private OrganisationService organisationService;
    @Inject
    private RelRegistry relRegistry;

    @GET
    public ResultHal<OrganisationCard> getOrganisationCards(@BeanParam RangeInfo rangeInfo) {
        Result<OrganisationCard> organisationCards = organisationFinder.findOrganisation(rangeInfo.range());
        return new ResultHal<>("organisation", organisationCards, relRegistry.uri(Rels.ORGANISATIONS));
    }

    @POST
    @Consumes({"application/json", "application/hal+json"})
    public Response createOrganisation(@Valid OrganisationCard organisationCard) throws URISyntaxException {
        HashSet<UserId> owners = Sets.newHashSet(securityService.getAuthenticatedUser().getEntityId());
        Organisation organisation = new Organisation(new OrganisationId(organisationCard.getId()), organisationCard.getName(), owners);
        organisationRepository.save(organisation);
        return Response.created(new URI(relRegistry.uri(Rels.ORGANISATION).set(ORGANISATION_ID, organisationCard.getId()).expand()))
                .entity(getOrganisationCards(organisationCard.getId())).build();
    }

    @Rel(Rels.ORGANISATION)
    @GET
    @Path("/{organisationId}")
    public OrganisationRepresentation getOrganisationCards(@PathParam(ORGANISATION_ID) String organisationName) {
        Organisation organisation = organisationRepository.load(new OrganisationId(organisationName));
        if (organisation == null) {
            throw new NotFoundException();
        }
        return organisationAssembler.assembleDtoFromAggregate(organisation);
    }

    @Rel(Rels.ORGANISATION_OWNERS)
    @POST
    @Path("/{organisationId}/owners")
    public Response addOwner(@PathParam(ORGANISATION_ID) String organisationName, String ownerName) {
        organisationService.addOwner(new OrganisationId(organisationName), new UserId(ownerName));
        return Response.ok().build();
    }

    @Rel(Rels.ORGANISATION_OWNERS)
    @DELETE
    @Path("/{organisationId}/owners")
    public void removeOwner(@PathParam(ORGANISATION_ID) String organisationName, String ownerName) {
        organisationService.removeOwner(new OrganisationId(organisationName), new UserId(ownerName));
    }

    @Rel(Rels.ORGANISATION_MEMBERS)
    @POST
    @Path("/{organisationId}/members")
    public Response addMember(@PathParam(ORGANISATION_ID) String organisationName, String ownerName) {
        organisationService.addMember(new OrganisationId(organisationName), new UserId(ownerName));
        return Response.ok().build();
    }

    @Rel(Rels.ORGANISATION_MEMBERS)
    @DELETE
    @Path("/{organisationId}/members")
    public void removeMember(@PathParam(ORGANISATION_ID) String organisationName, String ownerName) {
        organisationService.removeMember(new OrganisationId(organisationName), new UserId(ownerName));
    }
}
