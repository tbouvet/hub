/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.importer;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

class Manifest {
    @NotBlank
    private String id;
    @NotBlank
    private String version;
    @NotBlank
    private String owner;
    private String publicationDate;
    private String url;
    private String summary;
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

    String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    String getOwner() {
        return owner;
    }

    void setOwner(String owner) {
        this.owner = owner;
    }

    String getPublicationDate() {
        return publicationDate;
    }

    void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
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
