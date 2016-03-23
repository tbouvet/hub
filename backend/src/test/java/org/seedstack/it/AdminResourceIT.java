package org.seedstack.it;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;
import org.seedstack.hub.domain.services.fetch.VCSType;
import org.seedstack.seed.it.AbstractSeedWebIT;

import javax.inject.Inject;
import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;

@Ignore
public class AdminResourceIT extends AbstractSeedWebIT {

    @ArquillianResource
    private URL baseURL;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @Inject
    private Repository<Component, ComponentId> repository;

    @RunAsClient
    @Test
    public void get_with_pagination() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("vcsType", VCSType.GITHUB);
        source.put("url", "seedstack/jdbc-addon");
        JSONArray sources = new JSONArray();
        sources.put(source);

        expect()
                .statusCode(200)
                .given()
                .auth().basic("adrienlauer", "password")
                .header("Content-Type", "application/json")
                .body(sources.toString())
                .post(baseURL.toString() + "admin/import");

        Assertions.assertThat(repository.load(new ComponentId("jdbc-addon"))).isNotNull();
    }
}
