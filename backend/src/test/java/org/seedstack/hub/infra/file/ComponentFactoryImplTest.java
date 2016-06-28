/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.file;

import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.fetch.Manifest;
import org.seedstack.hub.application.fetch.ReleaseDTO;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Owner;
import org.seedstack.hub.domain.model.component.Release;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.domain.model.component.Version;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentScope;
import org.seedstack.hub.domain.model.organisation.Organisation;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class ComponentFactoryImplTest {

    @Tested
    private ComponentFactoryImpl underTest;
    @Injectable
    private UserRepository userRepository;
    @Injectable
    private Repository<Organisation, OrganisationId> organisationRepository;
    private Manifest manifest;

    @Before
    public void setUp() throws Exception {
        manifest = new Manifest();
        manifest.setId("component1");
        manifest.setOwner("user3@gmail.com");

        new NonStrictExpectations() {{
            userRepository.findByEmail("user3@gmail.com");
            result = Optional.of(new User(new UserId("user3"), "user3@gmail.com"));
        }};
    }

    @Test
    public void create_minimal_component_from_manifest() {
        Component component = underTest.createComponent(manifest);

        assertThat(component.getId()).isEqualTo(new ComponentId("component1"));
        assertThat(component.getOwner()).isEqualTo(new Owner("user3"));
        assertThat(component.getDescription()).isNotNull();
        assertThat(component.getDescription().getComponentUrl()).isNull();
        assertThat(component.getDocs()).isNotNull();
        assertThat(component.getComments()).isNotNull();
        assertThat(component.getMaintainers()).isNotNull();
        assertThat(component.getState()).isEqualTo(State.PENDING);
        assertThat(component.getStars()).isEqualTo(0);
    }

    @Test
    public void create_minimal_component_from_manifest_with_organisation() {
        manifest.setOwner("@org");
        Component component = underTest.createComponent(manifest);

        assertThat(component.getOwner()).isEqualTo(new Owner("@org"));
    }

    @Test
    public void create_minimal_component_from_manifest_with_maintainers() {
        manifest.setMaintainers(Lists.newArrayList("user1@gmail.com", "user2@gmail.com"));
        new Expectations() {{
            userRepository.findByEmail("user1@gmail.com");
            result = Optional.of(new User(new UserId("user1"), "user1@gmail.com"));
            userRepository.findByEmail("user2@gmail.com");
            result = Optional.of(new User(new UserId("user2"), "user2@gmail.com"));
        }};
        Component component = underTest.createComponent(manifest);

        assertThat(component.getMaintainers()).containsOnly(new UserId("user1"), new UserId("user2"));
    }

    @Test
    public void create_component_from_manifest_with_description() throws MalformedURLException {
        manifest.setName("Hub");

        manifest.setSummary("Some component description");
        manifest.setLicense("MPL 2");
        manifest.setReadme("README.md");
        manifest.setIcon("icon.png");
        manifest.setIssues("https://github.com/seedstack/hub/issues");
        manifest.setUrl("https://github.com/seedstack/hub");
        manifest.setImages(Lists.newArrayList("image.gif"));

        manifest.setDocs(Lists.newArrayList("doc/index.md"));

        Component component = underTest.createComponent(manifest);

        assertThat(component.getName()).isEqualTo("Hub"); // todo warn: name also on description

        assertThat(component.getDescription().getSummary()).isEqualTo("Some component description");
        assertThat(component.getDescription().getLicense()).isEqualTo("MPL 2");
        assertThat(component.getDescription().getReadme()).isEqualTo(new DocumentId(new ComponentId("component1"), DocumentScope.FILES, "README.md"));
        assertThat(component.getDescription().getIcon()).isEqualTo(new DocumentId(new ComponentId("component1"), DocumentScope.FILES, "icon.png"));
        assertThat(component.getDescription().getIssues()).isEqualTo(new URL("https://github.com/seedstack/hub/issues"));
        assertThat(component.getDescription().getComponentUrl()).isEqualTo(new URL("https://github.com/seedstack/hub"));
        assertThat(component.getDescription().getImages().get(0)).isEqualTo(new DocumentId(new ComponentId("component1"), DocumentScope.FILES, "image.gif"));

        assertThat(component.getDocs().get(0)).isEqualTo(new DocumentId(new ComponentId("component1"), DocumentScope.FILES, "doc/index.md"));

    }

    @Test
    public void create_component_from_manifest_with_release() throws MalformedURLException {
        ReleaseDTO releaseDTO = new ReleaseDTO();
        releaseDTO.setVersion("1.0.0-M1");
        releaseDTO.setDate("2016-03-29");
        releaseDTO.setUrl("https://github.com/seedstack/hub/releases/tag/v2.2.1");
        manifest.setReleases(Lists.newArrayList(releaseDTO));

        Component component = underTest.createComponent(manifest);

        Release release = component.getReleases().get(0);
        assertThat(release.getVersion()).isEqualTo(new Version(1, 0, 0, "M1"));
        assertThat(release.getDate()).isEqualTo(LocalDateTime.of(2016, 3, 29, 0, 0));
        assertThat(release.getUrl()).isEqualTo(new URL("https://github.com/seedstack/hub/releases/tag/v2.2.1"));
    }
}
