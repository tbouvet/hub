/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.application.fetch;

import org.seedstack.hub.domain.model.component.Source;

import java.util.ArrayList;
import java.util.Collection;

public class ImportException extends RuntimeException {

    private Source source;

    private Collection<String> violations = new ArrayList<>();

    public ImportException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(Throwable e) {
        super(e);
    }

    public ImportException(Throwable e, Source source) {
        super(e);
        this.source = source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public void addViolation(String violation){
        violations.add(violation);
    }

    public void setViolations(Collection<String> violations) {
        this.violations = violations;
    }

    public Collection<String> getViolations() {
        return violations;
    }
}
