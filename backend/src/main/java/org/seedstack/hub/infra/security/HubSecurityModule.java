/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.security;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.seedstack.seed.Install;

@Install
public class HubSecurityModule extends AbstractModule {
    public void configure() {
        //Missing default url for Shiro form auth feature
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login.html");
        //Missing binding for Shiro logout feature
        bind(LogoutFilter.class);
    }
}
