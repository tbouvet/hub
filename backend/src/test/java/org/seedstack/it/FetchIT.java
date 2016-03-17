/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.seed.it.AbstractSeedIT;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.net.URL;

@Ignore
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
