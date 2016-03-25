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

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embedded
public class Description extends BaseValueObject {
    private static final DocumentId DEFAULT_ICON = new DocumentId(ComponentId.DEFAULT_COMPONENT, "default:icon");
    private static final DocumentId DEFAULT_README = new DocumentId(ComponentId.DEFAULT_COMPONENT, "default:readme");

    @NotBlank
    private String name;
    private String summary;
    private String license;
    private String componentUrl;
    private String issues;
    @NotNull
    private DocumentId icon;
    @NotNull
    private DocumentId readme;
    private List<DocumentId> images = new ArrayList<>();

    public Description(String name, String summary, String license, DocumentId icon, DocumentId readme) {
        this.name = name;
        this.summary = summary;
        this.license = license;
        this.icon = icon != null ? icon : DEFAULT_ICON;
        this.readme = readme != null ? readme : DEFAULT_README;
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
        try {
            return new URL(issues);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }
    }

    public void setIssues(URL issues) {
        this.issues = issues.toString();
    }

    public URL getComponentUrl() {
        try {
            return new URL(componentUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }
    }

    public void setComponentUrl(URL componentUrl) {
        this.componentUrl = componentUrl.toString();
    }
}
