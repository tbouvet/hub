package org.seedstack.hub.infra.vcs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.seedstack.hub.domain.services.fetch.FetchException;
import org.seedstack.seed.Logging;
import org.slf4j.Logger;

import javax.inject.Named;
import java.io.File;
import java.net.URL;

@Named("git")
public class GitFetchService extends AbstractFetchService {
    @Logging
    private Logger logger;

    protected void doFetch(URL remote, File target) throws FetchException {
        logger.debug("Cloning Git remote {} into directory {}", remote.toExternalForm(), target.getAbsolutePath());

        try (Git result = Git.cloneRepository().setURI(remote.toExternalForm()).setDirectory(target).call()) {
            String branchName = getBranchName(remote);

            logger.debug("Checking out {} branch in directory {}", branchName, target.getAbsolutePath());

            result.checkout().setName(branchName).call();
        } catch (GitAPIException e) {
            throw new FetchException("Unable to fetch Git remote " + remote.toExternalForm(), e);
        }
    }

    private String getBranchName(URL remote) {
        String path = remote.getPath();
        if (path != null) {
            int index = path.lastIndexOf("#");
            if (index != -1) {
                return path.substring(index);
            }
        }
        return "master";
    }
}
