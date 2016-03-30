/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.application.fetch.ImportService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@RunWith(SeedITRunner.class)
public class SeedStackServiceIT {

    @Inject @Named("GITHUB")
    private ImportService importService;

    @Test
    public void testGithubAPICall() throws Exception {
        Component component = importService.importComponent(new Source(VCSType.GITHUB, "seedstack/mongodb-addon"));
        assertThat(component).isNotNull();
        assertThat(component.getId()).isEqualTo(new ComponentId("mongodb-addon"));
        assertThat(component.getName()).isEqualTo("mongodb-addon");
        assertThat(component.getOwner().getOrganisationId().get().getId()).isEqualTo("@seedstack");
        assertThat(component.getDescription().getComponentUrl()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon"));
        assertThat(component.getDescription().getIssues()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon/issues"));
        assertThat(component.getReleases()).isNotEmpty();
    }

}
