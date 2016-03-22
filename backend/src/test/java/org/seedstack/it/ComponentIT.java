/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.junit.Test;
import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.seed.security.WithUser;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentIT extends AbstractSeedIT {
    @Inject
    private ComponentFactory componentFactory;

    @Test
    @WithUser(id = "adrienlauer", password = "password")
    public void basic_import() throws Exception {
        Component component = componentFactory.createComponent(new File("src/test/resources/components/component1"));
        assertThat(component.getId().getName()).isEqualTo("component1");

        assertThat(component.getReleases()).hasSize(2);
        Release v101 = component.getReleases().get(0);
        assertThat(v101.getVersion()).isEqualTo(new Version(1, 0, 1, null));
        assertThat(v101.getDate()).isEqualTo(LocalDate.of(2016, 2, 9));
        assertThat(v101.getUrl()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon/releases/tag/v1.0.1"));

        Release v100 = component.getReleases().get(1);
        assertThat(v100.getVersion()).isEqualTo(new Version(1, 0, 0, null));
        assertThat(v100.getDate()).isEqualTo(LocalDate.of(2015, 11, 17));
        assertThat(v100.getUrl()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon/releases/tag/v1.0.0"));

        assertThat(component.getOwner()).isEqualTo(new Owner("adrienlauer"));
        Description desc = component.getDescription();
        assertThat(desc.getName()).isEqualTo("component1");
        assertThat(desc.getLicense()).isEqualTo("MPL2");
        assertThat(desc.getComponentUrl()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon"));
        assertThat(desc.getIssues()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon/issues"));
        assertThat(desc.getIcon()).isEqualTo(buildDocumentId("component1", "images/icon.png"));
        assertThat(desc.getImages()).containsExactly(
                buildDocumentId("component1", "images/screenshot-1.png"),
                buildDocumentId("component1", "images/screenshot-2.png"),
                buildDocumentId("component1", "images/screenshot-3.png")
        );
        assertThat(desc.getReadme()).isEqualTo(buildDocumentId("component1", "README.md"));
        assertThat(component.getDocs()).containsExactly(
                buildDocumentId("component1", "docs/intro.md"),
                buildDocumentId("component1", "docs/integration.md"),
                buildDocumentId("component1", "docs/usage.md")
        );
        assertThat(component.getMaintainers()).containsExactly(
                new UserId("pith"),
                new UserId("kavi87")
        );
    }

    private DocumentId buildDocumentId(String componentId, String path) {
        return new DocumentId(new ComponentId(componentId), path);
    }
}
