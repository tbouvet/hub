/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.component;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.hub.domain.model.organisation.OrganisationId;
import org.seedstack.hub.domain.model.user.UserId;

public class OwnerTest {

    @Test
    public void testOwnerIsOrganisation() {
        Owner owner = new Owner("@seedstack");

        Assertions.assertThat(owner.isOrganisation()).isTrue();
        Assertions.assertThat(owner.getOrganisationId().isPresent()).isTrue();
        Assertions.assertThat(owner.getOrganisationId().orElseThrow(NullPointerException::new))
                .isEqualTo(new OrganisationId("@seedstack"));
        Assertions.assertThat(owner.isUser()).isFalse();
        Assertions.assertThat(owner.getUserId().isPresent()).isFalse();
    }
    @Test
    public void testOwnerIsUser() {
        Owner owner = new Owner("user3");
        Assertions.assertThat(owner.isUser()).isTrue();
        Assertions.assertThat(owner.getUserId().isPresent()).isTrue();
        Assertions.assertThat(owner.getUserId().orElseThrow(NullPointerException::new))
                .isEqualTo(new UserId("user3"));
        Assertions.assertThat(owner.isOrganisation()).isFalse();
        Assertions.assertThat(owner.getOrganisationId().isPresent()).isFalse();
    }
}
