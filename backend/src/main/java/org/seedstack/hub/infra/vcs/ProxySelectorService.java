/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import com.google.common.collect.Lists;
import org.seedstack.seed.Application;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

class ProxySelectorService extends ProxySelector implements LifecycleListener {

    private static final String PROXY = "proxy";
    private ProxySelector defaultProxySelector;
    private Proxy proxy;

    @Inject
    private Application application;
    private List<Pattern> exclusions = new ArrayList<>();

    @Override
    public List<Proxy> select(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }
        String protocol = uri.getScheme();
        if (("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) && isNotExcluded(uri)) {
            return Lists.newArrayList(proxy);
        }
        if (defaultProxySelector != null) {
            return defaultProxySelector.select(uri);
        } else {
            ArrayList<Proxy> l = new ArrayList<>();
            l.add(Proxy.NO_PROXY);
            return l;
        }
    }

    private boolean isNotExcluded(URI uri) {
        String host = uri.getHost();
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            return false;
        }

        for (Pattern exclusion : exclusions) {
            if (exclusion.matcher(host).matches()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // nothing to do
    }

    @Override
    public void started() {
        defaultProxySelector = ProxySelector.getDefault();
        initializeProxiesFromConfiguration();
        ProxySelector.setDefault(this);
    }

    private void initializeProxiesFromConfiguration() {
        Optional<ProxyConfig> optionalProxyConfig = ProxyConfig.createFromConfiguration(application.getConfiguration());
        if (optionalProxyConfig.isPresent()) {
            ProxyConfig proxyConfig = optionalProxyConfig.get();
            proxy = proxyConfig.createProxy();
            exclusions = proxyConfig.getExclusions();
            initializeAuthentication(proxyConfig);
        } else {
            proxy = Proxy.NO_PROXY;
        }
    }

    private void initializeAuthentication(ProxyConfig proxyConfig) {
        proxyConfig.getAuthentication().ifPresent(auth -> Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return auth;
            }
        }));
    }

    @Override
    public void stopping() {
        ProxySelector.setDefault(null);
    }
}
