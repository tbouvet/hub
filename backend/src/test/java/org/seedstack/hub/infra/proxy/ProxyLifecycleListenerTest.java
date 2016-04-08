/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.proxy;

import mockit.Deencapsulation;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.List;

@RunWith(JMockit.class)
public class ProxyLifecycleListenerTest {

    @Tested
    private ProxyLifecycleListener underTest;

    @Test
    public void testWithNoProxy() throws Exception {
        givenProxy(null, null, null, false);
        underTest.started();
        List<Proxy> select = getHubProxySelector().select(new URI("http://app.domain.com:42"));
        underTest.stopping();
        assertNoProxy(select);
    }

    @Test
    public void testWithProxy() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, false);
        underTest.started();
        List<Proxy> select = getHubProxySelector().select(new URI("http://app.otherdomain.com"));
        underTest.stopping();
        assertProxy(select, Proxy.Type.HTTP, "proxy.mycompany.com", 8080);
    }

    @Test
    public void testWithUpperCaseProxy() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, true);
        underTest.started();
        List<Proxy> select = getHubProxySelector().select(new URI("http://app.otherdomain.com"));
        underTest.stopping();
        assertProxy(select, Proxy.Type.HTTP, "proxy.mycompany.com", 8080);
    }

    @Test
    public void testProxyWithExclusion() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, false, "*.mycompany.com");
        underTest.started();
        List<Proxy> select = getHubProxySelector().select(new URI("http://app.mycompany.com"));
        underTest.stopping();
        assertNoProxy(select);
    }

    @Test
    public void testProxyWithLocalhost() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, false);
        underTest.started();
        List<Proxy> select = getHubProxySelector().select(new URI("http://localhost"));
        underTest.stopping();
        assertNoProxy(select);
    }

    @Test
    public void testProxyWithMultipleExclusion() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, false, "*.mycompany.com", "*.otherdomain.com");
        underTest.started();
        List<Proxy> select1 = getHubProxySelector().select(new URI("http://app.mycompany.com"));
        List<Proxy> select2 = getHubProxySelector().select(new URI("http://app.otherdomain.com"));
        List<Proxy> select3 = getHubProxySelector().select(new URI("http://app.yetanotherdomain.com"));
        underTest.stopping();
        assertNoProxy(select1);
        assertNoProxy(select2);
        assertProxy(select3, Proxy.Type.HTTP, "proxy.mycompany.com", 8080);
    }

    @Test
    public void testProxyWithExclusionNoMatch() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, false, "*.mycompany.com");
        underTest.started();
        List<Proxy> select = getHubProxySelector().select(new URI("http://app.otherdomain.com"));
        underTest.stopping();
        assertProxy(select, Proxy.Type.HTTP, "proxy.mycompany.com", 8080);
    }

    private void assertNoProxy(List<Proxy> select) {
        Assertions.assertThat(select).containsExactly(Proxy.NO_PROXY);
    }

    private void assertProxy(List<Proxy> select, Proxy.Type type, String host, int port) {
        Assertions.assertThat(select).hasSize(1);
        Assertions.assertThat(select.get(0).type()).isEqualTo(type);
        Assertions.assertThat(((InetSocketAddress) select.get(0).address()).getHostName()).isEqualTo(host);
        Assertions.assertThat(((InetSocketAddress) select.get(0).address()).getPort()).isEqualTo(port);
    }

    private void givenProxy(String type, String host, Integer port, boolean upperCase, String... exclusions) {
        if (type != null) {
            new NonStrictExpectations(System.class) {{
                System.getenv(upperCase ? (type.toUpperCase() + "_PROXY") : (type.toLowerCase() + "_proxy"));
                result = String.format("http://%s:%d", host, port);
                System.getenv(!upperCase ? (type.toUpperCase() + "_PROXY") : (type.toLowerCase() + "_proxy"));
                result = null;

                System.getenv(upperCase ? "NO_PROXY" : "no_proxy");
                result = String.join(",", (CharSequence[]) exclusions);
                System.getenv(!upperCase ? "NO_PROXY" : "no_proxy");
                result = null;
            }};
        } else {
            new NonStrictExpectations(System.class) {{
                System.getenv("http_proxy");
                result = null;
                System.getenv("HTTP_PROXY");
                result = null;
                System.getenv("https_proxy");
                result = null;
                System.getenv("HTTPS_PROXY");
                result = null;
                System.getenv("no_proxy");
                result = null;
                System.getenv("NO_PROXY");
                result = null;
            }};
        }
    }

    private HubProxySelector getHubProxySelector() {
        return Deencapsulation.getField(underTest, "hubProxySelector");
    }
}
