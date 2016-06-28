import module = require('../module');
import angular = require('{angular}/angular');
import IAugmentedJQuery = angular.IAugmentedJQuery;

class HubHistoryBack implements ng.IDirective {
    static $inject = ['$window'];
    constructor(private $window) {};
    restrict = 'A';
    link = (scope, element: JQuery) => {
        element.on('click', () => {
            this.$window.history.back();
        });
    }
}

angular
    .module(module.angularModules)
    .directive('hubHistoryBack', DirectiveFactory.getFactoryFor<HubHistoryBack>(HubHistoryBack));

