/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import module =  require('../module');
import angular = require('{angular}/angular');
import toolbarTemplate = require('[text]!{hub}/modules/directives/toolbar/toolbar.html');

interface ISidebarScope  extends ng.IScope {
    toggleSidebar(): void
}

class HubToolbar implements ng.IDirective {
    template: string = toolbarTemplate;

    link: ng.IDirectiveLinkFn = (scope: ISidebarScope) => {
        scope.toggleSidebar = () => {
            this.$mdSidenav('sidebar').toggle();
        };
    };

    static $inject = ["$mdSidenav"];
    constructor(private $mdSidenav) {};
}

angular
    .module(module.angularModules)
    .directive('hubToolbar', DirectiveFactory.getFactoryFor<HubToolbar>(HubToolbar));
