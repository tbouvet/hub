/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    '{angular}/angular',
    '[text]!{hub}/modules/directives/toolbar/toolbar.html'

], function (angular, toolbarTemplate) {
    'use strict';

    var module = angular.module('hubToolbar', []);

    module.directive('hubToolbar', [function () {
        return {
            template: toolbarTemplate,
            link: function (scope, element, attrs) {

            }
        };
    }]);

    return {
        angularModules: ['hubToolbar']
    };

});