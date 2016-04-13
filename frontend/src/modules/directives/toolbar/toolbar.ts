/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import module =  require('../module');
import moduleConfig =  require('module');
import angular = require('{angular}/angular');
import toolbarTemplate = require('[text]!{hub}/modules/directives/toolbar/toolbar.html');

var config = moduleConfig && moduleConfig['config']() || {};

interface ISidebarScope extends ng.IScope {
    userPrincipals:any;
    config: {},
    toggleSidebar(): void,
    routeToSearchView (query: string): void,
    query: string
}

class HubToolbar implements ng.IDirective {
    static $inject = ['$mdSidenav', '$location', 'EventService', 'AuthenticationService'];
    constructor(private $mdSidenav, private $location, private eventService, private authenticationService) {
    };

    template:string = toolbarTemplate;

    link:ng.IDirectiveLinkFn = (scope:ISidebarScope) => {
        scope.config = config;

        this.eventService.on('w20.security.authenticated', () => {
            scope.userPrincipals = this.authenticationService.subjectPrincipals();
        });


        scope.query = this.$location.search().search || '';

        scope.toggleSidebar = () => {
            this.eventService.emit('hub.sidenav.toggle');
        };

        scope.routeToSearchView = (query: string) => {
            return query ? this.$location.url('hub/components?search=' + query) : this.$location.url('/hub/components');
        }
    };
}

angular
    .module(module.angularModules)
    .directive('hubToolbar', DirectiveFactory.getFactoryFor<HubToolbar>(HubToolbar));
