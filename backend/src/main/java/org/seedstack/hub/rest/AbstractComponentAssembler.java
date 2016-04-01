/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.hub.application.StatePolicy;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.rest.shared.UriBuilder;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.hal.HalRepresentation;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static org.seedstack.hub.rest.Rels.AUTHOR_COMPONENTS;
import static org.seedstack.hub.rest.Rels.ORGANISATION;
import static org.seedstack.hub.rest.detail.ComponentResource.COMPONENT_ID;
import static org.seedstack.hub.rest.organisation.OrganisationResource.ORGANISATION_ID;
import static org.seedstack.hub.rest.user.UserResource.USER_ID;

public abstract class AbstractComponentAssembler<T extends HalRepresentation> extends BaseAssembler<Component, T> {

    @Inject
    protected RelRegistry relRegistry;
    @Inject
    private StatePolicy statePolicy;
    @Inject
    private Injector injector;

    @Override
    protected final void doAssembleDtoFromAggregate(T t, Component component) {
        doAssemble(t, component);
        String id = component.getEntityId().getName();
        t.self(relRegistry
                .uri(Rels.COMPONENT)
                .set(COMPONENT_ID, id).expand());

        String owner = component.getOwner().toString();
        if (component.getOwner().isUser()) {
            t.link(AUTHOR_COMPONENTS, relRegistry
                    .uri(AUTHOR_COMPONENTS).set(USER_ID, owner).expand());
        } else {
            t.link(ORGANISATION, relRegistry
                    .uri(ORGANISATION).set(ORGANISATION_ID, owner).expand());
        }

        if (isPublishableByUser(component) || isArchivableByUser(component)) {
            t.link(Rels.STATE, relRegistry
                    .uri(Rels.STATE).set(COMPONENT_ID, id).expand());
        }
    }

    private boolean isPublishableByUser(Component component) {
        return component.getState() == State.PENDING && statePolicy.canPublish(component);
    }

    private boolean isArchivableByUser(Component component) {
        return component.getState() == State.PUBLISHED && statePolicy.canArchive(component);
    }

    protected abstract void doAssemble(T t, Component component);

    protected String documentIdToString(DocumentId documentId) {
        if (documentId == null) {
            return null;
        }

        return UriBuilder.uri(Rels.COMPONENTS, documentId.getComponentId().toString(), "files", documentId.getPath());
    }

    @Override
    protected final void doMergeAggregateWithDto(Component component, T t) {
        throw new UnsupportedOperationException();
    }

    protected String addContextPath(String url) {
        try {
            ServletContext servletContext = injector.getInstance(ServletContext.class);
            return UriBuilder.uri(servletContext.getContextPath(), url);
        } catch (ConfigurationException | ProvisionException e) {
            return url;
        }
    }
}
