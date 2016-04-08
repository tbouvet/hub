/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

class HubProxyAuthenticator extends Authenticator {
    private final PasswordAuthentication httpsPasswordAuthentication;
    private final PasswordAuthentication httpPasswordAuthentication;

    public HubProxyAuthenticator(PasswordAuthentication httpPasswordAuthentication, PasswordAuthentication httpsPasswordAuthentication) {
        this.httpPasswordAuthentication = httpPasswordAuthentication;
        this.httpsPasswordAuthentication = httpsPasswordAuthentication;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if ("http".equalsIgnoreCase(getRequestingProtocol())) {
            return httpPasswordAuthentication;
        } else if ("https".equalsIgnoreCase(getRequestingProtocol())) {
            return httpsPasswordAuthentication;
        } else {
            return null;
        }
    }
}
