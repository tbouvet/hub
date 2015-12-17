/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    '{angular}/angular',
    '{angular-resource}/angular-resource'

], function (angular) {
    'use strict';

    var module = angular.module('home', ['ngResource']);

    module.controller('HomeController', ['$scope', '$mdDialog', function ($scope, $mdDialog) {
        var hc = this;

        var originatorEv;
        this.openMenu = function($mdOpenMenu, ev) {
            originatorEv = ev;
            $mdOpenMenu(ev);
        };
        this.notificationsEnabled = true;
        this.toggleNotifications = function() {
            this.notificationsEnabled = !this.notificationsEnabled;
        };
        this.redial = function() {
            $mdDialog.show(
                $mdDialog.alert()
                    .targetEvent(originatorEv)
                    .clickOutsideToClose(true)
                    .parent('body')
                    .title('Suddenly, a redial')
                    .textContent('You just called a friend; who told you the most amazing story. Have a cookie!')
                    .ok('That was easy')
            );
            originatorEv = null;
        };
    }]);

    return {
        angularModules: ['home']
    };
})
;
