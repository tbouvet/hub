package org.seedstack.hub.infra.github;

import org.seedstack.hub.application.fetch.ImportException;

public class QuotaExceededException extends ImportException {

    public QuotaExceededException(String message) {
        super(message);
    }
}
