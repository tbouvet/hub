/// <amd-dependency path="{css-framework}/modules/css-framework"/>
import angular = require("{angular}/angular");

var module = angular.module('Hub.configuration', ['w20CSSFramework']);

module.config(['$mdThemingProvider', $mdThemingProvider => {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('blue-grey');
}]);

export = { angularModules: 'Hub.configuration' }



