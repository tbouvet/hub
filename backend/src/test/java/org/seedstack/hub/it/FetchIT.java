package org.seedstack.hub.it;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.seed.it.AbstractSeedIT;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.net.URL;

public class FetchIT extends AbstractSeedIT {
    @Inject
    @Named("git")
    private FetchService fetchService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void fetching_from_github() throws Exception {
        File target = temporaryFolder.newFolder();
        fetchService.fetchRepository(new URL("https://github.com/seedstack/mongodb-addon"), target);
    }
}
