/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import module = require('../module');
import angular = require('{angular}/angular');
import sidebarTemplate = require('[text]!{hub}/modules/directives/sidebar/sidebar.html')

class HubSidebar implements ng.IDirective {
    template = sidebarTemplate;
}

angular
    .module(module.angularModules)
    .directive('hubSidebar', DirectiveFactory.getFactoryFor<HubSidebar>(HubSidebar));
