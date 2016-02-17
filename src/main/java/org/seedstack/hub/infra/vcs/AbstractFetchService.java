package org.seedstack.hub.infra.vcs;

import org.seedstack.hub.domain.services.fetch.FetchException;
import org.seedstack.hub.domain.services.fetch.FetchService;
import org.seedstack.seed.Logging;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractFetchService implements FetchService {
    @Logging
    private Logger logger;

    @Override
    public void fetchRepository(URL remote, File target) throws FetchException {
        try {
            prepareLocalDirectory(target);
        } catch (IOException e) {
            throw new FetchException("Unable to prepare target directory " + target.getAbsolutePath(), e);
        }

        doFetch(remote, target);
    }

    private void prepareLocalDirectory(File location) throws IOException {
        if (!location.exists()) {
            logger.debug("Directory " + location.getAbsolutePath() + " doesn't exists, creating it");
            if (!location.mkdirs()) {
                throw new IOException("Cannot create directory " + location.getAbsolutePath());
            }
        }

        if (!location.isDirectory()) {
            throw new IOException("Location " + location.getAbsolutePath() + " does not denote a directory");
        }

        if (!location.canWrite()) {
            throw new IOException("Location " + location.getAbsolutePath() + " is not writable");
        }
    }

    abstract protected void doFetch(URL remote, File local) throws FetchException;
}
