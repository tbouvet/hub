/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;

@RunWith(SeedITRunner.class)
public class ComponentRepositoryIT {

    @Inject
    private Repository<Component, ComponentId> componentRepository;

    @Test
    public void testRepo() throws Exception {
        ComponentId componentId = new ComponentId("seedstack-hub");
        Component component = new Component(componentId, "SeedStack Hub", new Owner("admin"), buildDescription(componentId));
        Release release = new Release(new Version(1, 0, 0, "M2"));
        release.setUrl(new URL("http://component.com/release/1.0.0"));
        release.asDate(LocalDate.now());
        component.addComment(new Comment("pith", "some text", new Date()));
        component.addMaintainer(new UserId("pith"));
        component.addRelease(release);
        componentRepository.persist(component);
        Assertions.assertThat(componentRepository.load(componentId)).isNotNull();
        componentRepository.delete(component);
        Assertions.assertThat(componentRepository.load(componentId)).isNull();
    }

    private Description buildDescription(ComponentId componentId) throws Exception {
        Description description = new Description("c1", "summary", "MPL2", new DocumentId(componentId, "/icon.png"), new DocumentId(componentId, "readme.md"));
        description.setComponentUrl(new URL("http://some-url.com"));
        description.setIssues(new URL("http://issue-url.com"));
        description.setReadme(new DocumentId(componentId, "/readme.png"));
        return description;
    }
}
