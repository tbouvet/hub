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
import HubUserService = require("../../services/user-service");

interface IHubSidebarScope extends ng.IScope {
    route(path: string): void;
    authorizationService: { hasRole: (realm, role, attributes) => {} };
    userPrincipals: { subjectId: string };
    keepVisible: boolean;
}
class HubSidebar implements ng.IDirective {
    static $inject = ['$mdSidenav', '$mdMedia', '$location', 'EventService', 'AuthenticationService', 'AuthorizationService'];
    constructor(private $mdSidenav, private $mdMedia, private $location: ng.ILocationService, private eventService, private authenticationService, private authorizationService) {}
    template = sidebarTemplate;
    link: ng.IDirectiveLinkFn = (scope: IHubSidebarScope) => {

        scope.authorizationService = this.authorizationService;

        scope.keepVisible = true;

        scope.route = (path: string) => {
            if (this.$mdMedia('xs') || this.$mdMedia('sm')) {
                this.$mdSidenav('sidebar').close().then(() => {
                    this.$location.path(path);
                });
            } else {
                this.$location.path(path);
            }

        };

        this.eventService.on('w20.security.authenticated', () => {
            scope.userPrincipals = this.authenticationService.subjectPrincipals();
        });

        this.eventService.on('hub.sidenav.toggle', () => {
            scope.keepVisible = !scope.keepVisible;
            this.$mdSidenav('sidebar').toggle();
        });
    }
}

angular
    .module(module.angularModules)
    .directive('hubSidebar', DirectiveFactory.getFactoryFor<HubSidebar>(HubSidebar));
