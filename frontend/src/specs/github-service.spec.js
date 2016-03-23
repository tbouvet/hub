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
    '{hub}/modules/services/github-service'
], function (angular) {
    'use strict';

    describe('the Github service', function () {
        var githubService,
            data;

        beforeEach(function () {
            angular.mock.module('Hub.services');

            angular.mock.inject(function (_$rootScope_, _GithubService_) {
                githubService = _GithubService_;
                _$rootScope_.$digest();
            });

            /*githubService.searchRepositories('foo').then(function (results) {
                data = results;
                done();
            });*/

        });

        it('should query the Github api with the search parameter', function () {
            //expect(data).toBeDefined();
        });
    });
});
