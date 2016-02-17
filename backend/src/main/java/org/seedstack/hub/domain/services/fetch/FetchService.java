/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.fetch;

import org.seedstack.business.Service;

import java.io.File;
import java.net.URL;

@Service
public interface FetchService {
    void fetchRepository(URL remote, File target) throws FetchException;
}
