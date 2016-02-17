/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    '{angular}/angular',
    '[text]!{hub}/modules/directives/bottom-sheet/bottom-sheet.html'

], function (angular, bottomSheetTemplate) {
    'use strict';

    var module = angular.module('hubBottomSheet', []);

    /*
    Usage:
     <md-button hub-bottom-sheet ng-click="showBottomSheet()">
        Open a Bottom Sheet!
     </md-button>
     */

    module.directive('hubBottomSheet', ['$mdBottomSheet', function ($mdBottomSheet) {
        return {
            link: function (scope, element, attrs) {
                scope.showBottomSheet = function() {
                    $mdBottomSheet.show({
                        template: bottomSheetTemplate,
                        clickOutsideToClose: true
                    });
                };
            }
        };
    }]);

    return {
        angularModules: ['hubBottomSheet']
    };

});