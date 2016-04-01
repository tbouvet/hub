/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.list;

import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.rest.AbstractComponentAssembler;
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.rest.detail.ComponentDetails;
import org.seedstack.hub.rest.shared.UriBuilder;

public class ComponentCardAssembler extends AbstractComponentAssembler<ComponentCard> {

    @Override
    protected void doAssemble(ComponentCard componentCard, Component component) {
        componentCard.setId(component.getId().getName());
        componentCard.setName(component.getName());
        componentCard.setSummary(component.getDescription().getSummary());
        componentCard.setIcon(documentIdToString(component.getDescription().getIcon()));
        componentCard.setOwner(component.getOwner().toString());
        componentCard.setStars(component.getStars());
        componentCard.setState(component.getState());
        updateUrls(componentCard);
    }

    private void updateUrls(ComponentCard componentCard) {
        componentCard.setIcon(addContextPath(componentCard.getIcon()));
    }
}
