package org.seedstack.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.application.SeedStackImportService;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.seed.it.SeedITRunner;

import javax.inject.Inject;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class SeedStackServiceIT {

    @Inject
    private SeedStackImportService importService;

    @Test
    public void testGithubAPICall() throws Exception {
        Component component = importService.importFromGithub("seedstack", "mongodb-addon");
        assertThat(component).isNotNull();
        assertThat(component.getId()).isEqualTo(new ComponentId("mongodb-addon"));
        assertThat(component.getName()).isEqualTo("mongodb-addon");
        assertThat(component.getOwner().getOrganisationId().get().getId()).isEqualTo("@seedstack");
        assertThat(component.getDescription().getComponentUrl()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon"));
        assertThat(component.getDescription().getIssues()).isEqualTo(new URL("https://github.com/seedstack/mongodb-addon/issues"));
        assertThat(component.getReleases()).isNotEmpty();

    }

}
