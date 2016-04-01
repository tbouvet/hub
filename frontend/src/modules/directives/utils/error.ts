import module = require('../module');
import angular = require('{angular}/angular');
import IAugmentedJQuery = angular.IAugmentedJQuery;

class ErrSrc implements ng.IDirective {
    static $inject = [];
    constructor() {};
    restrict = 'A';
    scope = {
        onBottomReached: '&',
        criterias: '='
    };
    link = (scope, element: JQuery, attrs: ng.IAttributes) => {
        element.bind('error', function() {
            if (attrs['src'] != attrs['errSrc']) {
                attrs.$set('src', attrs['errSrc']);
            }
        });
    }
}

angular
    .module(module.angularModules)
    .directive('errSrc', DirectiveFactory.getFactoryFor<ErrSrc>(ErrSrc));