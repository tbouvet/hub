package org.seedstack.hub.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentFactory;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.Source;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextFormat;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.hub.infra.file.Manifest;
import org.seedstack.hub.infra.file.ReleaseDTO;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Named("GITHUB")
public class ImportServiceGithub implements SeedStackImportService, ImportService {

    private static final String GITHUB_API = "https://api.github.com";
    private static final String REPO_PATH = "/repos/{org}/{name}";
    private static final String RELEASES_PATH = REPO_PATH + "/releases";
    private static final String README_PATH = REPO_PATH + "/readme";
    private final WebTarget github;
    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private DocumentFactory documentFactory;
    @Inject
    @Named("git")
    private FetchService fetchService;
    private final Client client;

    public ImportServiceGithub() {
        client = ClientBuilder.newClient();
        github = client.target(GITHUB_API);
    }

    @Override
    public Component importComponent(Source source) {
        String[] parts = source.getUrl().split("/");
        return importFromGithub(parts[0], parts[1]);
    }

    @Override
    public Component importFromGithub(String organisationName, String componentName) {
        Component component = importComponentFromGithub(organisationName, componentName);
        importReadme(organisationName, componentName, component);
        componentRepository.persist(component);
        return component;
    }

    private Component importComponentFromGithub(String organisationName, String componentName) {
        JsonNode jsonComponent = httpGetComponent(REPO_PATH, organisationName, componentName);
        if (jsonComponent == null) {
            throw new ImportException("Component " + componentName + " not found.");
        }
        JsonNode jsonReleases = httpGetComponent(RELEASES_PATH, organisationName, componentName);
        Manifest manifest = jsonToManifest(jsonComponent, jsonReleases);
        getIcon(manifest.getId(), jsonComponent);
        return componentFactory.createComponent(manifest);

    }

    private void getIcon(String componentId, JsonNode jsonComponent) {
        String ownerIconUrl = jsonComponent.get("owner").get("avatar_url").asText();
        Response response = client.target(ownerIconUrl).request().get();
        if (response.getStatus() == 200) {
            byte[] bytes = response.readEntity(byte[].class);
            String iconName = getLastPartOfUrl(ownerIconUrl);
            documentFactory.createBinaryDocument(new DocumentId(new ComponentId(componentId), iconName), iconName, bytes);
        }
    }

    private String getLastPartOfUrl(String ownerIconUrl) {
        return ownerIconUrl.substring(ownerIconUrl.lastIndexOf("/"));
    }

    private JsonNode httpGetComponent(String path, String organisationName, String componentName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(httpGetComponent(path, organisationName, componentName, MediaType.APPLICATION_JSON_TYPE));
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    private String httpGetComponent(String path, String organisationName, String componentName, MediaType mediaType) {
        WebTarget componentTarget = github.path(path)
                .resolveTemplate("name", componentName)
                .resolveTemplate("org", organisationName);

        Response response = componentTarget.request(mediaType).get();
        if (response.getStatus() == 200) {
            return response.readEntity(String.class);
        } else {
            throw new ImportException("Status: " + response.getStatus());
        }
    }

    private Manifest jsonToManifest(JsonNode jsonComponent, JsonNode jsonReleases) {
        Manifest manifest = new Manifest();
        manifest.setOwner("@seedstack");
        manifest.setId(jsonComponent.get("name").asText());
        manifest.setName(jsonComponent.get("name").asText());
        manifest.setSummary(jsonComponent.get("description").asText());
        manifest.setUrl(jsonComponent.get("html_url").asText());
        try {
            manifest.setIssues(new URL(jsonComponent.get("html_url").asText() + "/issues").toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }

        addReleases(jsonReleases, manifest);

        return manifest;
    }

    private void addReleases(JsonNode jsonReleases, Manifest manifest) {
        jsonReleases.forEach(jsonNode -> {
            if (!jsonNode.get("draft").asBoolean()) {
                ReleaseDTO releaseDTO = new ReleaseDTO();
                releaseDTO.setVersion(normalizeVersionName(jsonNode.get("name").asText()));
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

    private String normalizeVersionName(String name) {
        if (name.startsWith("v")) {
            return name.substring(1);
        }
        return name;
    }

    private void importReadme(String organisationName, String componentName, Component component) {
        String readme = httpGetComponent(README_PATH, organisationName, componentName, new MediaType("application", "vnd.github.VERSION.raw"));
        DocumentId readmeId = new DocumentId(component.getId(), "README.md");
        documentFactory.createTextDocument(readmeId, TextFormat.MARKDOWN, readme);
        component.setDescription(component.getDescription().setReadme(readmeId));
    }
}
