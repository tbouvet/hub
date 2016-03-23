/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.mongodb.morphia.annotations.Embedded;
import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.model.user.UserId;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Embedded
public class Revision extends BaseValueObject {
    private int index;
    private UserId author;
    private String patch;
    private String message;
    private Date date = new Date();

    Revision(int index, String patch, UserId author, String message) {
        this.index = index;
        this.patch = patch;
        this.author = author;
        this.message = message;
    }

    private Revision() {
        // required by morphia
    }

    public int getIndex() {
        return index;
    }

    public String getPatch() {
        return patch;
    }

    public UserId getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getDate() {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public String toString() {
        return String.format("Revision{%d} by %s at %s:\n%s", index, author, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(date), patch);
    }
}
