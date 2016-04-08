/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.application.fetch.NotFoundException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

class GithubClientJersey implements GithubClient {
    private static final String GITHUB_API = "https://api.github.com";
    private static final String REPO_PATH = "/repos/{org}/{name}";
    private static final String RELEASES_PATH = REPO_PATH + "/releases";
    private static final String README_PATH = REPO_PATH + "/readme";

    @Override
    public JsonNode getRepo(String organisation, String repository) {
        return getJsonOrFail(REPO_PATH, organisation, repository);
    }

    @Override
    public JsonNode getRelease(String organisation, String repository) {
        return getJsonOrFail(RELEASES_PATH, organisation, repository);
    }

    @Override
    public String getReadme(String organisation, String repository) {
        return getOrFail(README_PATH, organisation, repository, new MediaType("application", "vnd.github.VERSION.raw"));
    }

    @Override
    public byte[] getImage(URI uri) {
        Response response = ClientBuilder.newClient().target(uri).request().get();
        if (response.getStatus() == 200) {
            return response.readEntity(byte[].class);
        } else {
            throw new ImportException("Status: " + response.getStatus());
        }
    }

    private JsonNode getJsonOrFail(String path, String organisation, String repository) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(getOrFail(path, organisation, repository, MediaType.APPLICATION_JSON_TYPE));
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    private String getOrFail(String path, String organisation, String repository, MediaType mediaType) {
        Response response = get(path, organisation, repository, mediaType);
        if (response.getStatus() == 200) {
            return response.readEntity(String.class);
        } else if (response.getStatus() == 404) {
            throw new NotFoundException(String.format("Not found: %s", path));
        } else {
            throw new ImportException("Bad HTTP status: " + response.getStatus());
        }
    }

    private Response get(String path, String organisation, String repository, MediaType mediaType) {
        Client client = ClientBuilder.newClient();
        WebTarget github = client.target(GITHUB_API);
        WebTarget componentTarget = github.path(path)
                .resolveTemplate("org", organisation)
                .resolveTemplate("name", repository);
        return componentTarget.request(mediaType).get();
    }
}
