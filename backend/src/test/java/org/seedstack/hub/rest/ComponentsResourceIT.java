/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest;

import com.jayway.restassured.response.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.it.AbstractSeedWebIT;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.expect;
import static java.util.stream.Collectors.toList;

@Ignore
public class ComponentsResourceIT extends AbstractSeedWebIT {

    private final List<Component> mockedComponents = IntStream.range(0, 23)
            .mapToObj(MockedComponentBuilder::mock)
            .collect(toList());

    final String requestWithPagination = "{\"_links\":{" +
            "\"next\":{\"href\":\"/components?search=ponent1&pageIndex=2&pageSize=5\"}," +
            "\"prev\":{\"href\":\"/components?search=ponent1&pageIndex=0&pageSize=5\"}," +
            "\"self\":{\"href\":\"/components?search=ponent1&pageIndex=1&pageSize=5\"}" +
            "},\"_embedded\":{" +
            "\"components\":[" +
            "{\"name\":\"Component14\"},{\"name\":\"Component15\"},{\"name\":\"Component16\"},{\"name\":\"Component17\"},{\"name\":\"Component18\"}" +
            "]}}";

    @ArquillianResource
    private URL baseURL;
    @Inject
    @MorphiaDatastore(clientName = "main", dbName = "hub")
    private Datastore datastore;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @RunAsClient
    @Test
    public void get_with_pagination() throws JSONException {
        mockedComponents.forEach(datastore::save);
        /////

        Response response = expect().statusCode(200).given().header("Content-Type", "application/hal+json")
                .get(baseURL.toString() + "components?pageIndex=1&pageSize=5&search=ponent1");

        JSONAssert.assertEquals(requestWithPagination, response.asString(), false);

        /////
        mockedComponents.forEach(datastore::delete);
    }
}
