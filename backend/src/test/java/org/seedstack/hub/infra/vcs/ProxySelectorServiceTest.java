/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.apache.commons.configuration.Configuration;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.hub.application.ConfigurationException;
import org.seedstack.seed.Application;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.List;

@RunWith(JMockit.class)
public class ProxySelectorServiceTest {

    @Tested
    private ProxySelectorService underTest;
    @Injectable
    private Application application;
    @Mocked
    private Configuration configuration;

    @After
    public void tearDown() throws Exception {
        if (underTest != null) {
            underTest.stopping();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadUri() throws Exception {
        underTest.select(null);
    }

    @Test
    public void testWithNoProxy() throws Exception {
        givenProxy(null, null, null, null);
        underTest.started();
        List<Proxy> select = underTest.select(new URI("http://localhost:42"));
        underTest.stopping();
        assertNoProxy(select);
    }

    @Test
    public void testWithNoProxyDefault() throws Exception {
        List<Proxy> select = underTest.select(new URI("http://localhost:42"));
        assertNoProxy(select);
    }

    @Test
    public void testConfigurationError() throws Exception {
        givenProxy("HTTP", null, 8080, null);
        try {
            underTest.started();
            Assertions.failBecauseExceptionWasNotThrown(ConfigurationException.class);
        } catch (ConfigurationException e) {
            Assertions.assertThat(e).hasMessage("Missing \"url\" in the proxy configuration.");
        }
    }

    @Test
    public void testWithProxy() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, null);
        underTest.started();
        List<Proxy> select = underTest.select(new URI("http://localhost"));
        underTest.stopping();
        assertProxy(select, Proxy.Type.HTTP, "proxy.mycompany.com", 8080);
    }

    @Test
    public void testProxyWithExclusion() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, "mycompany.com");
        underTest.started();
        List<Proxy> select = underTest.select(new URI("http://app.mycompany.com"));
        underTest.stopping();
        assertNoProxy(select);
    }

    @Test
    public void testProxyWithExclusionNoMatch() throws Exception {
        givenProxy("HTTP", "proxy.mycompany.com", 8080, "mycompany.com");
        underTest.started();
        List<Proxy> select = underTest.select(new URI("http://localhost"));
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

    private void givenProxy(String type, String host, Integer port, String exclude) {
        new NonStrictExpectations() {{
            application.getConfiguration(); result = configuration;

            configuration.isEmpty(); result = type == null && host == null && port == null;

            configuration.containsKey("type"); result = type != null;
            configuration.getString("type"); result = type;

            configuration.containsKey("host"); result = host != null;
            configuration.getString("host"); result = host;

            configuration.containsKey("port"); result = port != null;
            configuration.getInt("port"); result = port;

            configuration.getString("exclude", null); result = exclude != null ? ".mycompany.com" : null;
        }};
    }
}
