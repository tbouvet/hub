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

interface IHubSidebarScope extends ng.IScope {
    route(path: string): void;
}
class HubSidebar implements ng.IDirective {
    static $inject = ['$mdSidenav', '$location'];
    constructor(private $mdSidenav, private $location: ng.ILocationService) {}

    template = sidebarTemplate;
    link: ng.IDirectiveLinkFn = (scope: IHubSidebarScope) => {
        scope.route = (path: string) => {
            this.$mdSidenav('sidebar').close().then(() => {
                this.$location.path(path);
            });
        }
    }
}

angular
    .module(module.angularModules)
    .directive('hubSidebar', DirectiveFactory.getFactoryFor<HubSidebar>(HubSidebar));
