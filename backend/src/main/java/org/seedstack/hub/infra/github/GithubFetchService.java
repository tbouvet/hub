/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.seedstack.hub.application.fetch.Manifest;
import org.seedstack.hub.application.fetch.ReleaseDTO;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.model.document.*;
import org.seedstack.hub.domain.services.fetch.FetchException;
import org.seedstack.hub.domain.services.fetch.FetchResult;
import org.seedstack.hub.domain.services.fetch.FetchService;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Named("github")
class GithubFetchService implements FetchService {

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private DocumentFactory documentFactory;
    @Inject
    private GithubClient githubClient;

    @Override
    public FetchResult fetch(Source source) throws FetchException {
        String[] parts = source.getUrl().split("/");
        return importFromGithub(parts[0], parts[1]);
    }

    private FetchResult importFromGithub(String organisationName, String componentName) {
        Manifest manifest = importComponentFromGithub(organisationName, componentName);
        addReleasesToManifest(manifest);
        Component component = componentFactory.createComponent(manifest);

        Document icon = fetchAndSaveIcon(manifest);
        component.setDescription(component.getDescription().changeIcon(icon.getId()));

        Document readme = fetchAndSaveReadme(manifest);
        component.setDescription(component.getDescription().setReadme(readme.getId()));

        return new FetchResult(component, Stream.of(icon, readme));
    }

    private Manifest importComponentFromGithub(String organisationName, String componentName) {
        JsonNode jsonComponent =  githubClient.getRepo(organisationName, componentName);

        Manifest manifest = new Manifest();
        manifest.setOwner(getOwnerName(jsonComponent));
        manifest.setId(jsonComponent.get("name").asText());
        manifest.setName(jsonComponent.get("name").asText());
        manifest.setSummary(jsonComponent.get("description").asText());
        manifest.setUrl(jsonComponent.get("html_url").asText());
        manifest.setIcon(jsonComponent.get("owner").get("avatar_url").asText());
        try {
            manifest.setIssues(new URL(jsonComponent.get("html_url").asText() + "/issues").toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }

        return manifest;
    }

    private String getOwnerName(JsonNode jsonComponent) {
        String ownerName = "";
        if (jsonComponent.get("owner").get("type").asText().equals("Organization")) {
            ownerName += "@";
        }
        ownerName += jsonComponent.get("owner").get("login").asText();
        return ownerName;
    }

    private Document fetchAndSaveReadme(Manifest manifest) {
        String readmeContent = githubClient.getReadme(normalizeOrgName(manifest.getOwner()), manifest.getId());
        DocumentId readmeId = new DocumentId(new ComponentId(manifest.getId()), DocumentScope.FILE, "README.md");
        return documentFactory.createTextDocument(readmeId, TextFormat.MARKDOWN, readmeContent);
    }

    private Document fetchAndSaveIcon(Manifest manifest) {
        byte[] bytes = githubClient.getImage(URI.create(manifest.getIcon()));
        String iconName = getLastPartOfUrl(manifest.getIcon());
        DocumentId iconId = new DocumentId(new ComponentId(manifest.getId()), DocumentScope.FILE, iconName);
        return documentFactory.createBinaryDocument(iconId, iconName, bytes);
    }

    private String getLastPartOfUrl(String ownerIconUrl) {
        String path = URI.create(ownerIconUrl).getPath();
        return path.substring(path.lastIndexOf("/"));
    }

    private void addReleasesToManifest(Manifest manifest) {
        JsonNode jsonReleases = githubClient.getRelease(normalizeOrgName(manifest.getOwner()), manifest.getId());

        jsonReleases.forEach(jsonNode -> {
            if (!jsonNode.get("draft").asBoolean()) {
                ReleaseDTO releaseDTO = new ReleaseDTO();
                releaseDTO.setVersion(normalizeVersion(jsonNode.get("name").asText()));
                releaseDTO.setUrl(jsonNode.get("html_url").asText());
                releaseDTO.setDate(convertDate(jsonNode));
                manifest.addRelease(releaseDTO);
            }
        });
    }

    private String convertDate(JsonNode jsonNode) {
        String published_at = jsonNode.get("published_at").asText();
        Instant instant = Instant.parse(published_at);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private String normalizeVersion(String name) {
        return stripFirstCharIfPresent(name, "v");
    }

    private String normalizeOrgName(String name) {
        return stripFirstCharIfPresent(name, "@");
    }

    private String stripFirstCharIfPresent(String name, String toStrip) {
        if (name.startsWith(toStrip)) {
            return name.substring(1);
        }
        return name;
    }

    @Override
    public void clean() {
        // nothing to do
    }
}
