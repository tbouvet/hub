/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    '{angular}/angular',
    '[text]!{hub}/modules/directives/sidebar/sidebar.html'

], function (angular, sidebarTemplate) {
    'use strict';

    var module = angular.module('hubSidebar', []);

    module.directive('hubSidebar', [function () {
        return {
            template: sidebarTemplate,
            link: function (scope, element, attrs) {
            }
        };
    }]);

    return {
        angularModules: ['hubSidebar']
    };

});