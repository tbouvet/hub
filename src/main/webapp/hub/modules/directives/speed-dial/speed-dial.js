/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    'require',
    '{angular}/angular',
    '[text]!{hub}/modules/directives/speed-dial/speed-dial.html'

], function (require, angular, speedDialTemplate) {
    'use strict';

    var module = angular.module('hubSpeedDial', []);

    module.directive('hubSpeedDial', ['$mdMedia', '$mdDialog', function ($mdMedia, $mdDialog) {
        return {
            template: speedDialTemplate,
            link: function (scope) {
                scope.showAddComponent = function (event) {
                    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'));
                    $mdDialog.show({
                        controller: 'AddComponentController',
                        templateUrl: require.toUrl('{hub}/modules/directives/speed-dial/actions/templates/component.tmpl.html'),
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose: false,
                        fullscreen: useFullScreen
                    })
                }
            }
        };
    }]);

    return {
        angularModules: ['hubSpeedDial']
    };

});