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
    '{hub}/modules/controllers/module'
], function (angular) {
    'use strict';

    describe('...', function () {
        var $rootScope;

        beforeEach(function () {

            angular.mock.module('Hub.controllers');

            angular.mock.inject(function (_$rootScope_, $injector) {
                $rootScope = _$rootScope_.$new();
                $rootScope.$digest();
            });
        });

        it('should ...', function () {
            expect(true).toBe(true);
        });
    });
});
