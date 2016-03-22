/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.file;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

class Manifest {
    @NotBlank
    private String id;
    private String name;
    @NotBlank
    private String version;
    private List<ReleaseDTO> releases;
    @NotBlank
    private String owner;
    private String url;
    private String issues;
    private String summary;
    private String license;
    private String icon;
    private List<String> images;
    private List<String> maintainers;
    private List<String> docs;

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    List<ReleaseDTO> getReleases() {
        return releases;
    }

    void setReleases(List<ReleaseDTO> releases) {
        this.releases = releases;
    }

    String getOwner() {
        return owner;
    }

    void setOwner(String owner) {
        this.owner = owner;
    }

    String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    String getIssues() {
        return issues;
    }

    void setIssues(String issues) {
        this.issues = issues;
    }

    String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    String getLicense() {
        return license;
    }

    void setLicense(String license) {
        this.license = license;
    }

    String getIcon() {
        return icon;
    }

    void setIcon(String icon) {
        this.icon = icon;
    }

    List<String> getImages() {
        return images;
    }

    void setImages(List<String> images) {
        this.images = images;
    }

    List<String> getMaintainers() {
        return maintainers;
    }

    void setMaintainers(List<String> maintainers) {
        this.maintainers = maintainers;
    }

    List<String> getDocs() {
        return docs;
    }

    void setDocs(List<String> docs) {
        this.docs = docs;
    }
}
