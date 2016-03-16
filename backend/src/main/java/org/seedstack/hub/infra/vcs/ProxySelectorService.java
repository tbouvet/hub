/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import com.google.common.collect.Lists;
import org.apache.commons.configuration.Configuration;
import org.seedstack.hub.application.ConfigurationException;
import org.seedstack.seed.Application;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

class ProxySelectorService extends ProxySelector implements LifecycleListener {

    public static final String PROXY = "proxy";
    public static final String PORT = "port";
    public static final String HOST = "host";
    public static final String TYPE = "type";
    public static final String EXCLUSIONS = "exclusions";
    private ProxySelector defaultProxySelector;
    private Optional<Proxy> proxy = Optional.empty();
    private List<Pattern> exclusions = new ArrayList<>();

    @Inject
    private Application application;

    @Override
    public List<Proxy> select(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }
        String protocol = uri.getScheme();
        if (("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) && isNotExcluded(uri)) {
            return Lists.newArrayList(proxy.orElse(Proxy.NO_PROXY));
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
        Configuration proxyConfig = application.getConfiguration().subset(PROXY);

        if (!proxyConfig.isEmpty()) {
            if (!proxyConfig.containsKey(TYPE)) {
                throw new ConfigurationException("Missing \"type\"  in the proxy configuration.");
            }
            String type = proxyConfig.getString(TYPE);

            if (!proxyConfig.containsKey(HOST)) {
                throw new ConfigurationException("Missing \"url\" in the proxy configuration.");
            }
            String url = proxyConfig.getString(HOST);

            if (!proxyConfig.containsKey(PORT)) {
                throw new ConfigurationException("Missing \"port\"  in the proxy configuration.");
            }
            int port = proxyConfig.getInt(PORT);

            String[] exclusionsConfig = proxyConfig.getStringArray(EXCLUSIONS);
            if (exclusionsConfig != null) {
                exclusions = Arrays.stream(exclusionsConfig).map(this::makePattern).collect(toList());
            }
            proxy = Optional.of(new Proxy(Proxy.Type.valueOf(type), new InetSocketAddress(url, port)));
        } else {
            proxy = Optional.empty();
            exclusions = new ArrayList<>();
        }
    }

    private Pattern makePattern(String noProxy) {
        return Pattern.compile(noProxy.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"));
    }

    @Override
    public void stopping() {
        ProxySelector.setDefault(null);
    }
}
