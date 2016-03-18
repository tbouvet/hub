import module = require('../module');
import angular = require('{angular}/angular');
import lodash = require('{lodash}/lodash');
import IAugmentedJQuery = angular.IAugmentedJQuery;

class HubInfiniteScroll implements ng.IDirective {
    static $inject = ['$timeout'];
    constructor(private $timeout) {};
    restrict = 'A';
    scope = {
        onBottomReached: '&',
        criterias: '='
    };
    link = (scope, element: JQuery) => {
        this.$timeout(() => {
            var parent = angular.element('#view');
            var raw = parent[0];
            parent.bind('scroll', <any> lodash['throttle'](() => {
                if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                    scope.criterias.pageIndex++;
                    scope.onBottomReached({criterias: scope.criterias});
                }
            }, 200));
        })
    }
}

angular
    .module(module.angularModules)
    .directive('hubInfiniteScroll', DirectiveFactory.getFactoryFor<HubInfiniteScroll>(HubInfiniteScroll));

