package org.seedstack.hub.domain.services.fetch;

public class FetchException extends Exception {
    public FetchException(String s, Exception e) {
        super(s, e);
    }
}
