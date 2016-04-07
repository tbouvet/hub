/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.proxy;

import com.google.common.collect.Lists;
import org.seedstack.seed.LifecycleListener;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

class ProxyLifecycleListener implements LifecycleListener {
    private HubProxySelector hubProxySelector;
    private HubProxyAuthenticator hubProxyAuthenticator;

    @Override
    public void started() {
        String http_proxy = System.getenv("http_proxy");
        String https_proxy = System.getenv("https_proxy");
        String noProxy = System.getenv("no_proxy");

        hubProxySelector = new HubProxySelector(
                buildProxy(http_proxy, 80),
                buildProxy(https_proxy, 443),
                ProxySelector.getDefault(),
                buildExclusions(noProxy)
        );

        hubProxyAuthenticator = new HubProxyAuthenticator(
                buildPasswordAuthentication(http_proxy),
                buildPasswordAuthentication(https_proxy)
        );

        ProxySelector.setDefault(hubProxySelector);
        Authenticator.setDefault(hubProxyAuthenticator);
    }

    @Override
    public void stopping() {
        ProxySelector.setDefault(null);
        Authenticator.setDefault(null);
    }

    private List<Pattern> buildExclusions(String noProxy) {
        if (noProxy == null) {
            return Lists.newArrayList();
        }
        return Arrays.stream(noProxy.split(",")).map(this::makePattern).collect(toList());
    }

    private Proxy buildProxy(String value, int defaultPort) {
        String[] httpProxyInfo = parseProxy(value, String.valueOf(defaultPort));
        if (httpProxyInfo != null) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyInfo[0], Integer.parseInt(httpProxyInfo[1])));
        }
        return null;
    }

    private PasswordAuthentication buildPasswordAuthentication(String value) {
        String[] httpCredentials = parseCredentials(value);
        if (httpCredentials != null) {
            return new PasswordAuthentication(httpCredentials[0], httpCredentials[1].toCharArray());
        }
        return null;
    }

    /**
     * Given a proxy URL returns a two element arrays containing the user name and the password. The second component
     * of the array is null if no password is specified.
     *
     * @param url The proxy host URL.
     * @return An array containing the user name and the password or null when none are present or the url is empty.
     */
    private String[] parseCredentials(String url) {
        String[] result = new String[2];

        if (url == null || url.isEmpty())
            return null;

        int p = url.indexOf("://");
        if (p != -1)
            url = url.substring(p + 3);

        if ((p = url.indexOf('@')) != -1) {
            String credentials = url.substring(0, p);

            if ((p = credentials.indexOf(':')) != -1) {
                result[0] = credentials.substring(0, p);
                result[1] = credentials.substring(p + 1);
            } else {
                result[0] = credentials;
            }
        } else {
            return null;
        }

        return result;
    }

    /**
     * Given a proxy URL returns a two element arrays containing the host name and the port
     *
     * @param url     The proxy host URL.
     * @param defPort The default proxy port
     * @return An array containing the host name and the proxy port or null when url is empty
     */
    private String[] parseProxy(String url, String defPort) {
        String[] result = new String[2];

        if (url == null || url.isEmpty())
            return null;

        int p = url.indexOf("://");
        if (p != -1)
            url = url.substring(p + 3);

        if ((p = url.indexOf('@')) != -1)
            url = url.substring(p + 1);

        if ((p = url.indexOf(':')) != -1) {
            result[0] = url.substring(0, p);
            result[1] = url.substring(p + 1);
        } else {
            result[0] = url;
            result[1] = defPort;
        }

        // remove trailing slash from the host name
        p = result[0].indexOf("/");
        if (p != -1) {
            result[0] = result[0].substring(0, p);
        }

        // remove trailing slash from the port number
        p = result[1].indexOf("/");
        if (p != -1) {
            result[1] = result[1].substring(0, p);
        }

        return result;
    }

    private Pattern makePattern(String noProxy) {
        return Pattern.compile(noProxy.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"));
    }
}
