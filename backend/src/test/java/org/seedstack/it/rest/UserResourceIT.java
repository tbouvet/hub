/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.it.rest;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedWebIT;

import javax.ws.rs.core.MediaType;
import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class UserResourceIT extends AbstractSeedWebIT {

    @ArquillianResource
    private URL baseURL;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @RunAsClient
    @Test
    public void get_user_card() throws JSONException {
        Response response = httpGet("users/user2");

        String requestWithPagination = "{\"name\":\"user2\",\"emails\":[\"user2@email.com\"],\"" +
                "_links\":{" +
                "\"users/icon\":{\"href\":\"" + baseURL.getPath() + "api/users/user2/icon\"}," +
                "\"self\":{\"href\":\"" + baseURL.getPath() + "api/users/user2\"}," +
                "\"users/components\":{\"href\":\"" + baseURL.getPath() + "api/users/user2/components\"}," +
                "\"users/stars\":{\"href\":\"" + baseURL.getPath() + "api/users/user2/stars\"}}" +
                "}";

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertEquals(requestWithPagination, response.asString(), false);
    }

    @Ignore
    @RunAsClient
    @Test
    public void manage_icon() throws JSONException {
        byte[] bytes = "icon bytes".getBytes();
        assertThat(httpMethod(MediaType.MULTIPART_FORM_DATA).multiPart("file", bytes, MediaType.APPLICATION_OCTET_STREAM)
                .post(baseURL.toString() + "users/user2/icon").getStatusCode()).isEqualTo(200);

        Response response = httpGet("users/user2/icon");
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.as(byte[].class)).isEqualTo(bytes);

        assertThat(httpMethod().delete(baseURL.toString() + "api/users/user2/icon").getStatusCode()).isEqualTo(204);
    }

    private Response httpGet(String path) {
        return httpMethod()
                .get(baseURL.toString() + "api/" + path);
    }

    private RequestSpecification httpMethod() {
        return httpMethod(MediaType.APPLICATION_JSON);
    }

    private RequestSpecification httpMethod(String mediaType) {
        return expect()
                .given()
                .auth().basic("user2", "password")
                .header("Content-Type", mediaType);
    }
}
