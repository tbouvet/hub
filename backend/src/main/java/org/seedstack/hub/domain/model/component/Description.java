/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.hibernate.validator.constraints.NotBlank;
import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.model.document.DocumentId;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embedded
public class Description extends BaseValueObject {

    @NotBlank
    private String name;
    private String summary;
    private String license;
    private String componentUrl;
    private String issues;
    private DocumentId icon;
    private DocumentId readme;
    private List<DocumentId> images = new ArrayList<>();

    public Description(String name, String summary, String license, DocumentId icon, DocumentId readme) {
        this.name = name;
        this.summary = summary;
        this.license = license;
        this.icon = icon;
        this.readme = readme;
    }

    private Description() {
        // required by morphia
    }

    private Description(Description description) {
        this.name = description.name;
        this.summary = description.summary;
        this.license = description.license;
        this.componentUrl = description.componentUrl;
        this.issues = description.issues;
        this.icon = description.icon;
        this.readme = description.readme;
        this.images = new ArrayList<>(description.images);
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public String getLicense() {
        return license;
    }

    public DocumentId getIcon() {
        return icon;
    }

    public DocumentId getReadme() {
        return readme;
    }

    public List<DocumentId> getImages() {
        return Collections.unmodifiableList(images);
    }

    public Description setReadme(DocumentId documentId) {
        Description description = new Description(this);
        description.readme = documentId;
        return description;
    }

    public Description addImage(DocumentId image) {
        Description description = new Description(this);
        description.images.add(image);
        return description;
    }

    public Description removeImage(DocumentId image) {
        Description description = new Description(this);
        description.images.remove(image);
        return description;
    }

    public Description replaceImages(List<DocumentId> images) {
        Description description = new Description(this);
        description.images = new ArrayList<>(images);
        return description;
    }

    public URL getIssues() {
        return stringToUrl(issues);
    }

    public void setIssues(URL issues) {
        this.issues = issues.toString();
    }

    public URL getComponentUrl() {
        return stringToUrl(componentUrl);
    }

    private URL stringToUrl(String url) {
        if (url != null) {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException();
            }
        }
        return null;
    }

    public void setComponentUrl(URL componentUrl) {
        this.componentUrl = componentUrl.toString();
    }
}
