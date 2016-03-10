/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.vcs;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class GitFetchServiceTest {
    private GitFetchService gitFetchService;

    @Before
    public void setUp() throws Exception {
        gitFetchService = new GitFetchService();
    }

    @Test
    public void test_strip_branch() throws Exception {
        assertThat(callStripBranchName("https://github.com/seedstack/mongodb-addon")).isEqualTo("https://github.com/seedstack/mongodb-addon");
        assertThat(callStripBranchName("https://github.com/seedstack/mongodb-addon#master")).isEqualTo("https://github.com/seedstack/mongodb-addon");
        assertThat(callStripBranchName("https://github.com/seedstack/mongodb-addon#")).isEqualTo("https://github.com/seedstack/mongodb-addon");
    }

    @Test
    public void test_get_branch() throws Exception {
        assertThat(callGetBranchName("https://github.com/seedstack/mongodb-addon")).isEqualTo("master");
        assertThat(callGetBranchName("https://github.com/seedstack/mongodb-addon#other-branch")).isEqualTo("other-branch");
        assertThat(callGetBranchName("https://github.com/seedstack/mongodb-addon#")).isEqualTo("master");
    }

    private String callStripBranchName(String url) throws Exception {
        Method stripBranchName = GitFetchService.class.getDeclaredMethod("stripBranchName", URL.class);
        stripBranchName.setAccessible(true);
        return (String) stripBranchName.invoke(gitFetchService, new URL(url));
    }

    private String callGetBranchName(String url) throws Exception {
        Method getBranchName = GitFetchService.class.getDeclaredMethod("getBranchName", URL.class);
        getBranchName.setAccessible(true);
        return (String) getBranchName.invoke(gitFetchService, new URL(url));
    }
}
