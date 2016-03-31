/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.hub.application.fetch.Manifest;
import org.seedstack.hub.application.fetch.ReleaseDTO;
import org.seedstack.hub.domain.model.component.*;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.services.fetch.FetchResult;
import org.seedstack.hub.domain.services.fetch.SourceType;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class FetchServiceGithubTest {

    private static String responseValue = "{\"id\":42917133,\"name\":\"mongodb-addon\",\"full_name\":\"seedstack/mongodb-addon\",\"owner\":{\"login\":\"seedstack\",\"id\":9429488,\"avatar_url\":\"https://avatars.githubusercontent.com/u/9429488?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/seedstack\",\"html_url\":\"https://github.com/seedstack\",\"followers_url\":\"https://api.github.com/users/seedstack/followers\",\"following_url\":\"https://api.github.com/users/seedstack/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/seedstack/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/seedstack/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/seedstack/subscriptions\",\"organizations_url\":\"https://api.github.com/users/seedstack/orgs\",\"repos_url\":\"https://api.github.com/users/seedstack/repos\",\"events_url\":\"https://api.github.com/users/seedstack/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/seedstack/received_events\",\"type\":\"Organization\",\"site_admin\":false},\"private\":false,\"html_url\":\"https://github.com/seedstack/mongodb-addon\",\"description\":\"MongoDB official persistence integration for SeedStack.\",\"fork\":false,\"url\":\"https://api.github.com/repos/seedstack/mongodb-addon\",\"forks_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/forks\",\"keys_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/keys{/key_id}\",\"collaborators_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/collaborators{/collaborator}\",\"teams_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/teams\",\"hooks_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/hooks\",\"issue_events_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/issues/events{/number}\",\"events_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/events\",\"assignees_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/assignees{/user}\",\"branches_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/branches{/branch}\",\"tags_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/tags\",\"blobs_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/git/blobs{/sha}\",\"git_tags_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/git/tags{/sha}\",\"git_refs_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/git/refs{/sha}\",\"trees_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/git/trees{/sha}\",\"statuses_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/statuses/{sha}\",\"languages_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/languages\",\"stargazers_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/stargazers\",\"contributors_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/contributors\",\"subscribers_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/subscribers\",\"subscription_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/subscription\",\"commits_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/commits{/sha}\",\"git_commits_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/git/commits{/sha}\",\"comments_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/comments{/number}\",\"issue_comment_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/issues/comments{/number}\",\"contents_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/contents/{+path}\",\"compare_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/compare/{base}...{head}\",\"merges_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/merges\",\"archive_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/{archive_format}{/ref}\",\"downloads_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/downloads\",\"issues_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/issues{/number}\",\"pulls_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/pulls{/number}\",\"milestones_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/milestones{/number}\",\"notifications_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/notifications{?since,all,participating}\",\"labels_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/labels{/name}\",\"releases_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/releases{/id}\",\"deployments_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/deployments\",\"created_at\":\"2015-09-22T07:01:38Z\",\"updated_at\":\"2016-02-09T08:05:07Z\",\"pushed_at\":\"2016-03-25T16:38:59Z\",\"git_url\":\"git://github.com/seedstack/mongodb-addon.git\",\"ssh_url\":\"git@github.com:seedstack/mongodb-addon.git\",\"clone_url\":\"https://github.com/seedstack/mongodb-addon.git\",\"svn_url\":\"https://github.com/seedstack/mongodb-addon\",\"homepage\":null,\"size\":114,\"stargazers_count\":0,\"watchers_count\":0,\"language\":\"Java\",\"has_issues\":true,\"has_downloads\":true,\"has_wiki\":true,\"has_pages\":false,\"forks_count\":3,\"mirror_url\":null,\"open_issues_count\":0,\"forks\":3,\"open_issues\":0,\"watchers\":0,\"default_branch\":\"master\",\"organization\":{\"login\":\"seedstack\",\"id\":9429488,\"avatar_url\":\"https://avatars.githubusercontent.com/u/9429488?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/seedstack\",\"html_url\":\"https://github.com/seedstack\",\"followers_url\":\"https://api.github.com/users/seedstack/followers\",\"following_url\":\"https://api.github.com/users/seedstack/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/seedstack/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/seedstack/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/seedstack/subscriptions\",\"organizations_url\":\"https://api.github.com/users/seedstack/orgs\",\"repos_url\":\"https://api.github.com/users/seedstack/repos\",\"events_url\":\"https://api.github.com/users/seedstack/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/seedstack/received_events\",\"type\":\"Organization\",\"site_admin\":false},\"network_count\":3,\"subscribers_count\":6}";
    private static String releaseValue = "[{\"url\":\"https://api.github.com/repos/seedstack/mongodb-addon/releases/2577670\",\"assets_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/releases/2577670/assets\",\"upload_url\":\"https://uploads.github.com/repos/seedstack/mongodb-addon/releases/2577670/assets{?name,label}\",\"html_url\":\"https://github.com/seedstack/mongodb-addon/releases/tag/v1.0.1\",\"id\":2577670,\"tag_name\":\"v1.0.1\",\"target_commitish\":\"master\",\"name\":\"v1.0.1\",\"draft\":false,\"author\":{\"login\":\"adrienlauer\",\"id\":2498919,\"avatar_url\":\"https://avatars.githubusercontent.com/u/2498919?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/adrienlauer\",\"html_url\":\"https://github.com/adrienlauer\",\"followers_url\":\"https://api.github.com/users/adrienlauer/followers\",\"following_url\":\"https://api.github.com/users/adrienlauer/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/adrienlauer/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/adrienlauer/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/adrienlauer/subscriptions\",\"organizations_url\":\"https://api.github.com/users/adrienlauer/orgs\",\"repos_url\":\"https://api.github.com/users/adrienlauer/repos\",\"events_url\":\"https://api.github.com/users/adrienlauer/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/adrienlauer/received_events\",\"type\":\"User\",\"site_admin\":false},\"prerelease\":false,\"created_at\":\"2016-02-09T08:33:04Z\",\"published_at\":\"2016-02-09T08:50:30Z\",\"assets\":[],\"tarball_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/tarball/v1.0.1\",\"zipball_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/zipball/v1.0.1\",\"body\":\"* **[fix]** Flawed release process made this add-on unusable by clients.\"},{\"url\":\"https://api.github.com/repos/seedstack/mongodb-addon/releases/2137778\",\"assets_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/releases/2137778/assets\",\"upload_url\":\"https://uploads.github.com/repos/seedstack/mongodb-addon/releases/2137778/assets{?name,label}\",\"html_url\":\"https://github.com/seedstack/mongodb-addon/releases/tag/v1.0.0\",\"id\":2137778,\"tag_name\":\"v1.0.0\",\"target_commitish\":\"2544ff7238ab89ad6c38428ebf720452b45623d4\",\"name\":\"v1.0.0\",\"draft\":false,\"author\":{\"login\":\"adrienlauer\",\"id\":2498919,\"avatar_url\":\"https://avatars.githubusercontent.com/u/2498919?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/adrienlauer\",\"html_url\":\"https://github.com/adrienlauer\",\"followers_url\":\"https://api.github.com/users/adrienlauer/followers\",\"following_url\":\"https://api.github.com/users/adrienlauer/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/adrienlauer/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/adrienlauer/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/adrienlauer/subscriptions\",\"organizations_url\":\"https://api.github.com/users/adrienlauer/orgs\",\"repos_url\":\"https://api.github.com/users/adrienlauer/repos\",\"events_url\":\"https://api.github.com/users/adrienlauer/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/adrienlauer/received_events\",\"type\":\"User\",\"site_admin\":false},\"prerelease\":false,\"created_at\":\"2015-11-17T08:34:08Z\",\"published_at\":\"2015-11-17T09:18:43Z\",\"assets\":[],\"tarball_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/tarball/v1.0.0\",\"zipball_url\":\"https://api.github.com/repos/seedstack/mongodb-addon/zipball/v1.0.0\",\"body\":\"* **[new]** Initial Open-Source release.\"}]";
    private static String readmeValue = "# SeedStack MongoDB add-on\n\n[![Build status](https://travis-ci.org/seedstack/mongodb-addon.svg?branch=master)](https://travis-ci.org/seedstack/mongodb-addon) [![Coverage Status](https://coveralls.io/repos/seedstack/mongodb-addon/badge.svg?branch=master)](https://coveralls.io/r/seedstack/mongodb-addon?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.seedstack.addons.mongodb/mongodb/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.seedstack.addons.mongodb/mongodb)\n\nOfficial MongoDB integration for SeedStack.\n\n# Copyright and license\n\nThis source code is copyrighted by [The SeedStack Authors](https://github.com/seedstack/seedstack/blob/master/AUTHORS) and\nreleased under the terms of the [Mozilla Public License 2.0](https://www.mozilla.org/MPL/2.0/). \n";

    @Tested
    private GithubFetchService underTest;
    @Injectable
    private ComponentFactory componentFactory;
    @Injectable
    private DocumentFactory documentFactory;
    @Injectable
    private GithubClient githubClient;
    @Injectable
    private Repository<Document, DocumentId> documentRepository;

    @Test
    public void testFetch() throws Exception {
        givenHttpGetSuccess();
        expectManifest();

        FetchResult result = underTest.fetch(new Source(SourceType.GITHUB, "seedstack/mongodb-addon"));

        assertThat(result).isNotNull();
        assertThat(result.getComponent().getId()).isEqualTo(new ComponentId("mongodb-addon"));
        assertThat(result.getDocuments().count()).isEqualTo(2);
    }

    private void givenHttpGetSuccess() throws IOException {
        new Expectations() {{
            githubClient.getRepo("seedstack", "mongodb-addon");
            result = stringToJson(responseValue);
            githubClient.getRelease("seedstack", "mongodb-addon");
            result = stringToJson(releaseValue);
            githubClient.getReadme("seedstack", "mongodb-addon");
            result = readmeValue;
        }};
    }

    @Test
    public void testUrl() throws Exception {
        URI.create("seedstack/seed");
    }

    private void expectManifest() {
        Manifest manifest = new Manifest();
        manifest.setId("mongodb-addon");
        manifest.setName("mongodb-addon");
        manifest.setOwner("@seedstack");
        manifest.setSummary("MongoDB official persistence integration for SeedStack.");
        manifest.setUrl("https://github.com/seedstack/mongodb-addon");
        manifest.setIssues("https://github.com/seedstack/mongodb-addon/issues");
        manifest.setIcon("https://avatars.githubusercontent.com/u/9429488?v=3");

        ReleaseDTO v101 = new ReleaseDTO();
        v101.setVersion("1.0.1");
        v101.setUrl("https://github.com/seedstack/mongodb-addon/releases/tag/v1.0.1");
        v101.setDate("2016-02-09");

        ReleaseDTO v100 = new ReleaseDTO();
        v100.setVersion("1.0.0");
        v100.setUrl("https://github.com/seedstack/mongodb-addon/releases/tag/v1.0.0");
        v100.setDate("2015-11-17");

        manifest.setReleases(Lists.newArrayList(v101, v100));

        Component component = new Component(new ComponentId("mongodb-addon"), "mongodb-addon", new Owner("@seedstack"),
                new Description("mongodb-addon", "MongoDB official persistence integration for SeedStack.", null, null, null));
        new Expectations() {{
            componentFactory.createComponent(manifest);
            result = component;
        }};
    }

    public JsonNode stringToJson(String value) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(value);
    }
}
