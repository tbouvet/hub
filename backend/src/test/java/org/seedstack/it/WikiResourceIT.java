/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it;

import com.jayway.restassured.response.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.MockBuilder;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.seed.it.AbstractSeedWebIT;

import javax.inject.Inject;
import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class WikiResourceIT extends AbstractSeedWebIT {

    @ArquillianResource
    private URL baseURL;

    @Inject
    private Repository<Component, ComponentId> componentRepository;
    @Inject
    private Repository<Document, DocumentId> documentRepository;

    private final Component mockedComponent = MockBuilder.mock(1);

    @Before
    public void setUp() throws Exception {
        componentRepository.persist(mockedComponent);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @RunAsClient
    @Test
    public void create_wiki_page() throws JSONException {
        httpPost("components/Component1/wiki/page1", "# Hello World\n", "creation");
        assertThat(httpGet("components/Component1/wiki/page1", 200).asString()).isEqualTo("<h1><a></a>Hello World</h1>");
        assertEquals("{\"revisions\":[{\"author\":\"adrienlauer\",\"message\":\"creation\"}]}", httpGet("components/Component1/wiki/page1/revisions", 200).asString(), false);
        assertEquals("{\"wikiPages\":[{\"_links\":{\"self\":{\"href\":\"" + baseURL.getPath() + "components/Component1/wiki/page1\"}}}]}", httpGet("components/Component1", 200).asString(), false);
    }

    @RunAsClient
    @Test
    public void update_wiki_page() throws JSONException {
        httpPost("components/Component1/wiki/page2", "# Hello World\n", "creation");
        httpPut("components/Component1/wiki/page2", "# Hello World!\n", "update");
        assertThat(httpGet("components/Component1/wiki/page2", 200).asString()).isEqualTo("<h1><a></a>Hello World!</h1>");
        assertEquals("{\"revisions\":[{\"author\":\"adrienlauer\",\"message\":\"creation\"},{\"author\":\"adrienlauer\",\"message\":\"update\"}]}\n", httpGet("components/Component1/wiki/page2/revisions", 200).asString(), false);
    }

    @RunAsClient
    @Test
    public void delete_wiki_page() throws JSONException {
        httpPost("components/Component1/wiki/page3", "# Hello World\n", "creation");
        assertThat(httpGet("components/Component1/wiki/page3", 200).asString()).isEqualTo("<h1><a></a>Hello World</h1>");
        httpDelete("components/Component1/wiki/page3");
        httpGet("components/Component1/wiki/page3", 404);
    }

    @RunAsClient
    @Test
    public void get_revision_details() throws JSONException {
        httpPost("components/Component1/wiki/page4", "# Hello World\n", "creation");
        assertThat(httpGet("components/Component1/wiki/page4", 200).asString()).isEqualTo("<h1><a></a>Hello World</h1>");
        assertEquals("{\"author\":\"adrienlauer\",\"message\":\"creation\"}", httpGet("components/Component1/wiki/page4/revisions/0", 200).asString(), false);
    }

    @RunAsClient
    @Test
    public void diff_two_revisions() throws JSONException {
        httpPost("components/Component1/wiki/page5", "# Hello World\n", "creation");
        httpPut("components/Component1/wiki/page5", "# Hello World!\n", "update");
        httpPut("components/Component1/wiki/page5", "# Hello World?\n", "update");
        assertThat(httpGet("components/Component1/wiki/page5", 200).asString()).isEqualTo("<h1><a></a>Hello World?</h1>");
        assertThat(httpGet("components/Component1/wiki/page5/revisions/2/diff", 200).asString()).isEqualTo("<span># Hello World</span><del style=\"background:#ffe6e6;\">!</del><ins style=\"background:#e6ffe6;\">?</ins><span>&para;<br></span>");
    }

    private Response httpGet(String path, int expectedCode) {
        return expect()
                .statusCode(expectedCode)
                .given()
                .auth().basic("adrienlauer", "password")
                .header("Content-Type", "application/hal+json")
                .get(baseURL.toString() + path);
    }

    private Response httpPost(String path, String body, String message) {
        return expect()
                .statusCode(201)
                .given()
                .auth().basic("adrienlauer", "password")
                .header("Content-Type", "text/markdown")
                .queryParam("message", message)
                .body(body)
                .post(baseURL.toString() + path);
    }

    private Response httpPut(String path, String body, String message) {
        return expect()
                .statusCode(200)
                .given()
                .auth().basic("adrienlauer", "password")
                .header("Content-Type", "text/markdown")
                .queryParam("message", message)
                .body(body)
                .put(baseURL.toString() + path);
    }

    private Response httpDelete(String path) {
        return expect()
                .statusCode(200)
                .given()
                .auth().basic("adrienlauer", "password")
                .header("Content-Type", "text/markdown")
                .delete(baseURL.toString() + path);
    }

    @After
    public void tearDown() throws Exception {
        componentRepository.clear();
        documentRepository.clear();
    }
}
