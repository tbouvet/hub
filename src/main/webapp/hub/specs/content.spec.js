define([
    '{angular}/angular',
    '{angular-mocks}/angular-mocks'
], function (angular) {
    'use strict';

    describe('...', function () {
        var $rootScope;

        beforeEach(function () {

            angular.mock.module('content');

            angular.mock.inject(function (_$rootScope_, $injector) {
                $rootScope = _$rootScope_.$new();
                $rootScope.$digest();
            });
        });

        it('should ...', function () {

        });
    });
});
