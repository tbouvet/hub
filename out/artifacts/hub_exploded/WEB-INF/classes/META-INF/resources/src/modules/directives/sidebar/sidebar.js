/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define(["require", "exports", '../module', '{angular}/angular', '[text]!{hub}/modules/directives/sidebar/sidebar.html'], function (require, exports, module, angular, sidebarTemplate) {
    "use strict";
    var HubSidebar = (function () {
        function HubSidebar($mdSidenav, $mdMedia, $location, eventService, authenticationService, authorizationService) {
            var _this = this;
            this.$mdSidenav = $mdSidenav;
            this.$mdMedia = $mdMedia;
            this.$location = $location;
            this.eventService = eventService;
            this.authenticationService = authenticationService;
            this.authorizationService = authorizationService;
            this.template = sidebarTemplate;
            this.link = function (scope) {
                _this.eventService.on('w20.security.authenticated', function () {
                    scope.userPrincipals = _this.authenticationService.subjectPrincipals();
                });
                scope.authorizationService = _this.authorizationService;
                scope.route = function (path) {
                    if (_this.$mdMedia('xs') || _this.$mdMedia('sm')) {
                        _this.$mdSidenav('sidebar').close().then(function () {
                            _this.$location.path(path);
                        });
                    }
                    else {
                        _this.$location.path(path);
                    }
                };
            };
        }
        HubSidebar.$inject = ['$mdSidenav', '$mdMedia', '$location', 'EventService', 'AuthenticationService', 'AuthorizationService'];
        return HubSidebar;
    }());
    angular
        .module(module.angularModules)
        .directive('hubSidebar', DirectiveFactory.getFactoryFor(HubSidebar));
});

//# sourceMappingURL=sidebar.js.map
