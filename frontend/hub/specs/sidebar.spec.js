/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define([
    '{angular}/angular',
    '{angular-mocks}/angular-mocks',
    '{angular-material}/angular-material',
    '{hub}/modules/directives/sidebar/sidebar'
], function (angular) {
    'use strict';

    describe('the sidebar directive', function () {
        var $scope,
            $compile,
            $location,
            sidebar;

        beforeEach(function () {
            angular.mock.module('Hub.directives');
           
            angular.mock.inject(function (_$rootScope_, _$compile_, _$location_) {
                $compile = _$compile_;
                $location = _$location_;
                $scope = _$rootScope_.$new();
                $scope.$digest();
            });

            sidebar = $compile('<md-sidenav hub-sidebar md-component-id="sidebar"></md-sidenav>')($scope);
            $scope.$digest();
        });

        it('should route to the home view (/) when clicking "Home"', function () {
            sidebar.scope().route('/');
            $scope.$digest();
            setTimeout(function () {
                expect($location.url()).toBe('');
            });

        });

        it('should route to the components view (/hub/components) when clicking "Components"', function () {
            sidebar.scope().route('/hub/components');
            $scope.$digest();
            setTimeout(function () {
                expect($location.url()).toBe('/hub/components');
            });
        });
    });
});
