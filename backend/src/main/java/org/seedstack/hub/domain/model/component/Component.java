/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.hibernate.validator.constraints.NotBlank;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.core.utils.SeedTupleUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity(value = "components")
public class Component extends BaseAggregateRoot<ComponentId> {
    @Id
    @NotNull
    private ComponentId componentId;
    private String name;
    @NotNull
    private Owner owner;
    @NotNull
    private Description description;
    @NotNull
    private State state = State.PENDING;
    @NotNull
    private List<Comment> comments = new ArrayList<>();
    @NotNull
    private List<Release> releases = new ArrayList<>();
    @NotNull
    private List<DocumentId> docs = new ArrayList<>();
    @Min(0)
    private int stars = 0;
    private Set<UserId> maintainers = new HashSet<>();


    public Component(ComponentId componentId, String name, Owner owner, Description description) {
        this.componentId = componentId;
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    private Component() {
        // required by morphia
    }

    @Override
    public ComponentId getEntityId() {
        return componentId;
    }

    public ComponentId getId() {
        return componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void publish() throws ComponentException {
        if (state == State.PENDING || state == State.ARCHIVED) {
            state = State.PUBLISHED;
        } else {
            throw new ComponentException("Component cannot be published");
        }
    }

    public void archive() throws ComponentException {
        if (state == State.PUBLISHED) {
            state = State.ARCHIVED;
        } else {
            throw new ComponentException("Component cannot be archived");
        }
    }

    @PrePersist
    void sortComments() {
        Collections.reverse(comments);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Release> getReleases() {
        return Collections.unmodifiableList(releases);
    }

    public void addRelease(Release release) {
        releases.add(release);
        Collections.reverse(releases);
    }

    public void addDoc(DocumentId documentId) {
        docs.add(documentId);
    }

    public void replaceDocs(List<DocumentId> documentIds) {
        docs = new ArrayList<>(documentIds);
    }

    public List<DocumentId> getDocs() {
        return Collections.unmodifiableList(docs);
    }

    public Owner getOwner() {
        return owner;
    }

    public void star() {
        stars++;
    }

    public void unstar() {
        if (stars > 0) {
            stars--;
        }
    }

    public int getStars() {
        return stars;
    }

    public Set<UserId> getMaintainers() {
        return Collections.unmodifiableSet(maintainers);
    }

    public void addMaintainer(UserId maintainer) {
        this.maintainers.add(maintainer);
    }

    public void removeMaintainer(UserId maintainer) {
        this.maintainers.remove(maintainer);
    }

    public void replaceMaintainers(List<UserId> maintainers) {
        this.maintainers.clear();
        this.maintainers.addAll(maintainers);
    }
}
