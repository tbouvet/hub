/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    '{angular}/angular',
    '{css-framework}/modules/css-framework'

], function (angular) {
    'use strict';

    var module = angular.module('config', ['w20CSSFramework']);

/*    module.config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.headers.post  = {'Content-Type': 'application/x-www-form-urlencoded'};
    }]);*/

    module.config(['$mdThemingProvider', '$mdIconProvider', function ($mdThemingProvider, $mdIconProvider) {

        $mdIconProvider
            .defaultIconSet('./assets/svg/avatars.svg', 128)
            .icon('menu', './assets/svg/menu.svg', 24)
            .icon('share', './assets/svg/share.svg', 24)
            .icon('google_plus', './assets/svg/google_plus.svg', 512)
            .icon('hangouts', './assets/svg/hangouts.svg', 512)
            .icon('twitter', './assets/svg/twitter.svg', 512)
            .icon('phone', './assets/svg/phone.svg', 512);

        var lightGreenLightText = $mdThemingProvider.extendPalette('light-green', {
            'contrastDefaultColor': 'light'
        });

        $mdThemingProvider.definePalette('light-green-white-text', lightGreenLightText);

        $mdThemingProvider.theme('default')
            .primaryPalette('light-green-white-text')
            .accentPalette('brown');

    }]);

    return {
        angularModules: ['config']
    };
});


