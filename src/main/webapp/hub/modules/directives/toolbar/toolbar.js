define([
    '{angular}/angular',
    '[text]!{hub}/modules/directives/toolbar/toolbar.html'

], function (angular, toolbarTemplate) {
    'use strict';

    var module = angular.module('hubToolbar', []);

    module.directive('hubToolbar', [function () {
        return {
            template: toolbarTemplate,
            link: function (scope, element, attrs) {

            }
        };
    }]);

    return {
        angularModules: ['hubToolbar']
    };

});