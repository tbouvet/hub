define([
    '{angular}/angular',
    '{angular-resource}/angular-resource'

], function (angular) {
    'use strict';

    var module = angular.module('home', ['ngResource']);

    module.controller('HomeController', ['$scope', function ($scope) {
        var hc = this;

    }]);

    return {
        angularModules: ['home']
    };
})
;
