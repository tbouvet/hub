/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import org.apache.commons.configuration.Configuration;
import org.seedstack.hub.application.ConfigurationException;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

class ProxyConfig {
    private static final String PROXY = "proxy";
    private static final String PORT = "port";
    private static final String HOST = "host";
    private static final String TYPE = "type";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String EXCLUSIONS = "exclusions";

    private String type;
    private String host;
    private int port;
    private String user;
    private char[] password;
    private List<Pattern> exclusions = new ArrayList<>();

    Proxy createProxy() {
        return new Proxy(Proxy.Type.valueOf(type), new InetSocketAddress(host, port));
    }

    Optional<PasswordAuthentication> getAuthentication() {
        if (user != null && password != null) {
            return Optional.of(new PasswordAuthentication(user, password));
        } else {
            return Optional.empty();
        }
    }

    static Optional<ProxyConfig> createFromConfiguration(Configuration config) {
        Configuration configuration = config.subset(PROXY);
        if (!configuration.isEmpty()) {
            ProxyConfig proxyConfig = new ProxyConfig();
            if (!configuration.containsKey(TYPE)) {
                throw new ConfigurationException("Missing \"type\"  in the proxy configuration.");
            }
            proxyConfig.type = configuration.getString(TYPE);

            if (!configuration.containsKey(HOST)) {
                throw new ConfigurationException("Missing \"url\" in the proxy configuration.");
            }
            proxyConfig.host = configuration.getString(HOST);

            if (!configuration.containsKey(PORT)) {
                throw new ConfigurationException("Missing \"port\"  in the proxy configuration.");
            }
            proxyConfig.port = configuration.getInt(PORT);

            if (configuration.containsKey(USER)) {
                proxyConfig.user = configuration.getString(USER);
            }
            if (configuration.containsKey(PASSWORD)) {
                proxyConfig.password = configuration.getString(PASSWORD).toCharArray();
            }

            String[] exclusionsConfig = configuration.getStringArray(EXCLUSIONS);
            if (exclusionsConfig != null) {
                proxyConfig.exclusions = Arrays.stream(exclusionsConfig).map(ProxyConfig::makePattern).collect(toList());
            }
            return Optional.of(proxyConfig);
        }
        return Optional.empty();
    }

    private static Pattern makePattern(String noProxy) {
        return Pattern.compile(noProxy.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"));
    }

    List<Pattern> getExclusions() {
        return exclusions;
    }
}
