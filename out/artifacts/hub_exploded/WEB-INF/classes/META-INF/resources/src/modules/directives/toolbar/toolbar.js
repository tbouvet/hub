/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define(["require", "exports", '../module', 'module', '{angular}/angular', '[text]!{hub}/modules/directives/toolbar/toolbar.html'], function (require, exports, module, moduleConfig, angular, toolbarTemplate) {
    "use strict";
    var config = moduleConfig && moduleConfig['config']() || {};
    var HubToolbar = (function () {
        function HubToolbar($mdSidenav, $location, eventService, authenticationService) {
            var _this = this;
            this.$mdSidenav = $mdSidenav;
            this.$location = $location;
            this.eventService = eventService;
            this.authenticationService = authenticationService;
            this.template = toolbarTemplate;
            this.link = function (scope) {
                scope.config = config;
                _this.eventService.on('w20.security.authenticated', function () {
                    scope.userPrincipals = _this.authenticationService.subjectPrincipals();
                });
                scope.query = _this.$location.search().search || '';
                scope.toggleSidebar = function () {
                    _this.$mdSidenav('sidebar').toggle();
                };
                scope.routeToSearchView = function (query) {
                    return query ? _this.$location.url('hub/components?search=' + query) : _this.$location.url('/hub/components');
                };
            };
        }
        ;
        HubToolbar.$inject = ['$mdSidenav', '$location', 'EventService', 'AuthenticationService'];
        return HubToolbar;
    }());
    angular
        .module(module.angularModules)
        .directive('hubToolbar', DirectiveFactory.getFactoryFor(HubToolbar));
});

//# sourceMappingURL=toolbar.js.map
