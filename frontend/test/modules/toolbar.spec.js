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
    '{hub}/modules/directives/toolbar/toolbar'
], function (angular) {
    'use strict';

    describe('the toolbar directive', function () {
        var $scope,
            $compile,
            $location,
            toolbar,
            $mdSidenavMock;

        beforeEach(function () {
            angular.mock.module('Hub.directives');

            $mdSidenavMock = {};
            $mdSidenavMock.toggle = jasmine.createSpy();

            angular.mock.module(function($provide) {
                $provide.factory('$mdSidenav', function() {
                    return function(id) {
                        return $mdSidenavMock;
                    };
                })
            });

            angular.mock.inject(function (_$rootScope_, _$compile_, _$location_) {
                $compile = _$compile_;
                $location = _$location_;
                $scope = _$rootScope_.$new();
                $scope.$digest();
            });

            toolbar = $compile('<md-toolbar hub-toolbar></md-toolbar>')($scope);
            $scope.$digest();
        });

        xit('should toggle the sidebar', function () {
            toolbar.scope().toggleSidebar();
            expect($mdSidenavMock.toggle).toHaveBeenCalled();
        });

        xit('should bind the search parameter to its query property', function () {
            $location.url('/hub/components?search=bar');
            expect($location.url()).toBe('/hub/components?search=bar');
            toolbar = $compile('<md-toolbar hub-toolbar></md-toolbar>')($scope);
            $scope.$digest();
            expect(toolbar.scope().query).toBe('bar');
        });

        xit('should route to the component view with the search param', function () {
            toolbar.scope().routeToSearchView('foo');
            expect($location.url()).toBe('/hub/components?search=foo');
        })
    });
});
