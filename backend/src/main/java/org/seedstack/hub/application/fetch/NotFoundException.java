/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.fetch;

import org.seedstack.hub.domain.model.component.Source;

public class NotFoundException extends ImportException {
    public NotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable e) {
        super(e);
    }

    public NotFoundException(Throwable e, Source source) {
        super(e, source);
    }
}
