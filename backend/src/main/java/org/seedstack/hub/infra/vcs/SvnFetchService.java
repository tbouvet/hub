/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import org.seedstack.hub.domain.services.fetch.FetchException;
import org.seedstack.seed.Logging;
import org.slf4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import javax.inject.Named;
import java.io.File;
import java.net.URL;

@Named("svn")
class SvnFetchService extends LocalFetchService {
    @Logging
    private Logger logger;

    @Override
    protected void doFetch(URL remote, File target) {
        logger.debug("Checking out SVN URL {} into directory {}", remote.toExternalForm(), target.getAbsolutePath());

        SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        try {
            SvnCheckout checkout = svnOperationFactory.createCheckout();
            checkout.setSingleTarget(SvnTarget.fromFile(target));
            checkout.setSource(SvnTarget.fromURL(SVNURL.parseURIEncoded(remote.toExternalForm())));
            checkout.setDepth(SVNDepth.INFINITY);
            checkout.run();
        } catch (Exception e) {
            throw new FetchException("Unable to fetch SVN URL " + remote.toExternalForm(), e);
        } finally {
            svnOperationFactory.dispose();
        }
    }
}
