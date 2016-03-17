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
import org.mongodb.morphia.Datastore;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.model.component.State;
import org.seedstack.hub.rest.MockBuilder;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.it.AbstractSeedWebIT;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.expect;
import static java.util.stream.Collectors.toList;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class ComponentsResourceIT extends AbstractSeedWebIT {

    final String requestWithPagination = "{\"_links\":{" +
            "\"next\":{\"href\":\"/components?search=ponent1&pageIndex=2&pageSize=5\"}," +
            "\"prev\":{\"href\":\"/components?search=ponent1&pageIndex=0&pageSize=5\"}," +
            "\"self\":{\"href\":\"/components?search=ponent1&pageIndex=1&pageSize=5\"}" +
            "},\"_embedded\":{" +
            "\"components\":[" +
            "{\"id\":\"Component14\"},{\"id\":\"Component15\"},{\"id\":\"Component16\"},{\"id\":\"Component17\"},{\"id\":\"Component18\"}" +
            "]}}";

    final String requestByState = "{\"_links\":{" +
            "\"next\":{\"href\":\"/components?search=ponent1&pageIndex=2&pageSize=5\"}," +
            "\"prev\":{\"href\":\"/components?search=ponent1&pageIndex=0&pageSize=5\"}," +
            "\"self\":{\"href\":\"/components?search=ponent1&pageIndex=1&pageSize=5\"}" +
            "},\"_embedded\":{" +
            "\"components\":[" +
            "{\"id\":\"Component14\"},{\"id\":\"Component15\"},{\"id\":\"Component16\"},{\"id\":\"Component17\"},{\"id\":\"Component18\"}" +
            "]}}";

    @ArquillianResource
    private URL baseURL;

    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Inject
    private Repository<Component, ComponentId> componentRepository;

    private final List<Component> mockedComponents = IntStream.range(0, 23)
            .mapToObj(MockBuilder::mock)
            .collect(toList());

    @Before
    public void setUp() throws Exception {
        mockedComponents.forEach(datastore::save);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @RunAsClient
    @Test
    public void get_with_pagination() throws JSONException {
        Response response = httpGet("components?search=ponent1&pageIndex=1&pageSize=5");
        assertEquals(requestWithPagination, response.asString(), false);
    }

    @RunAsClient
    @Test
    public void get_by_state() throws JSONException {
        componentRepository.save(MockBuilder.mock(99, State.PENDING));
        Response response = httpGet("pending");
        assertEquals("{}", response.asString(), false);
        componentRepository.delete(new ComponentId("Component99"));
    }

    @RunAsClient
    @Test
    public void get_popular_with_pagination() throws JSONException {
        Response response = httpGet("popular");
        assertEquals("{}", response.asString(), false);
    }

    @RunAsClient
    @Test
    public void get_recent_with_pagination() throws JSONException {
        Response response = httpGet("recent");
        assertEquals("{}", response.asString(), false);
    }

    private Response httpGet(String path) {
        return expect().statusCode(200).given()
                .auth().basic("adrienlauer", "password")
                .header("Content-Type", "application/hal+json")
                .get(baseURL.toString() + path);
    }

    @After
    public void tearDown() throws Exception {
        mockedComponents.forEach(datastore::delete);
    }
}
