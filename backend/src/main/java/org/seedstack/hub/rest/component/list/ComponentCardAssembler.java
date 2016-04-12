/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.component.list;

import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.rest.component.AbstractComponentAssembler;
import org.seedstack.hub.shared.Dates;
import org.seedstack.hub.rest.shared.DocumentRepresentation;

import java.util.List;

public class ComponentCardAssembler extends AbstractComponentAssembler<ComponentCard> {

    @Override
    protected void doAssemble(ComponentCard componentCard, Component component) {
        componentCard.setId(component.getId().getName());
        componentCard.setName(component.getName());
        componentCard.setSummary(component.getDescription().getSummary());
        componentCard.setIcon(new DocumentRepresentation(component.getDescription().getIcon(), documentService, relRegistry));
        componentCard.setOwner(component.getOwner().toString());
        componentCard.setStars(component.getStars());
        componentCard.setState(component.getState());
        List<Release> releases = component.getReleases();
        addLastRelease(componentCard, releases);
    }

    private void addLastRelease(ComponentCard componentCard, List<Release> releases) {
        if (!releases.isEmpty()) {
            Release release = releases.get(0);
            componentCard.setReleaseVersion(release.getVersion().toString());
            if (release.getDate() != null) {
                componentCard.setReleaseDate(Dates.asDate(release.getDate()));
            }
            if (release.getUrl() != null) {
                componentCard.setReleaseUrl(release.getUrl().toString());
            }
        }
    }

}
