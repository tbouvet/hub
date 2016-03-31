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
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.hub.domain.services.fetch.SourceType;
import org.seedstack.seed.it.AbstractSeedIT;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;

@Ignore
public class FetchIT extends AbstractSeedIT {
    @Inject
    private Logger log;

    @Inject
    @Named("git")
    private FetchService fetchService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void fetching_from_github() throws Exception {
        log.warn("Calling GITHUB API !!");
        fetchService.fetch(new Source(SourceType.GIT, new URL("https://github.com/seedstack/mongodb-addon")));
    }
}
