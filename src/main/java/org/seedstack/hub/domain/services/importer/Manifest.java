package org.seedstack.hub.domain.services.importer;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

class Manifest {
    @NotBlank
    String name;
    String summary;
    List<String> maintainers;
    String icon;
    String images;
    String docs;
}
