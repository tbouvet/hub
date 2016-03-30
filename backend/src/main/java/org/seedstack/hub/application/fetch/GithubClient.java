/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import org.seedstack.business.Service;

import java.net.URI;

@Service
interface GithubClient {

    JsonNode getRepo(String organisation, String repository);

    JsonNode getRelease(String organisation, String repository);

    String getReadme(String organisation, String repository);

    byte[] getImage(URI uri);
}
