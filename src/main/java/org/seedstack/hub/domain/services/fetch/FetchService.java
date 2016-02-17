package org.seedstack.hub.domain.services.fetch;

import org.seedstack.business.Service;

import java.io.File;
import java.net.URL;

@Service
public interface FetchService {
    void fetchRepository(URL remote, File target) throws FetchException;
}
