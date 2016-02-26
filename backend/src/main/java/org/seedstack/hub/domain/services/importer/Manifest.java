/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.importer;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

class Manifest {
    @NotBlank
    String id;
    @NotBlank
    String name;
    @NotBlank
    String version;
    @NotBlank
    String owner;
    String publicationDate;
    String summary;
    String icon;
    List<String> images;
    List<String> maintainers;
    List<String> docs;
}
