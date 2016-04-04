/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.detail;

import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.rest.shared.DocumentRepresentation;
import org.seedstack.seed.rest.hal.HalRepresentation;

import java.util.List;

public class ComponentDetails extends HalRepresentation {
    private String id;
    private String version;
    private List<ReleaseRepresentation> releases;
    private State state;
    private String owner;
    private String name;
    private String summary;
    private String license;
    private String url;
    private String issues;
    private List<String> maintainers;
    private int stars;
    private DocumentRepresentation icon;
    private DocumentRepresentation readme;
    private List<DocumentRepresentation> images;
    private List<DocumentRepresentation> docs;
    private List<DocumentRepresentation> wikiPages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ReleaseRepresentation> getReleases() {
        return releases;
    }

    public void setReleases(List<ReleaseRepresentation> releases) {
        this.releases = releases;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIssues() {
        return issues;
    }

    public void setIssues(String issues) {
        this.issues = issues;
    }

    public DocumentRepresentation getIcon() {
        return icon;
    }

    public void setIcon(DocumentRepresentation icon) {
        this.icon = icon;
    }

    public DocumentRepresentation getReadme() {
        return readme;
    }

    public void setReadme(DocumentRepresentation readme) {
        this.readme = readme;
    }

    public List<DocumentRepresentation> getImages() {
        return images;
    }

    public void setImages(List<DocumentRepresentation> images) {
        this.images = images;
    }

    public List<DocumentRepresentation> getDocs() {
        return docs;
    }

    public void setDocs(List<DocumentRepresentation> docs) {
        this.docs = docs;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public List<String> getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(List<String> maintainers) {
        this.maintainers = maintainers;
    }

    public List<DocumentRepresentation> getWikiPages() {
        return wikiPages;
    }

    public void setWikiPages(List<DocumentRepresentation> wikiPages) {
        this.wikiPages = wikiPages;
    }
}
