import module = require('../module');
import angular = require('{angular}/angular');
import lodash = require('{lodash}/lodash');
import IAugmentedJQuery = angular.IAugmentedJQuery;

class HubInfiniteScroll implements ng.IDirective {
    restrict = 'A';
    scope = {
        hubInfiniteScroll: '&',
        hubSearchCriterias: '='
    };
    link = (scope, element: JQuery) => {
        var parent = angular.element(element[0].parentElement);
        var raw = parent[0];
        parent.bind('scroll', <any> lodash['throttle'](() => {
            if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                scope.hubSearchCriterias.pageIndex++;
                scope.hubInfiniteScroll({criterias: scope.hubSearchCriterias});
            }
        }, 200));
    }
}

angular
    .module(module.angularModules)
    .directive('hubInfiniteScroll', DirectiveFactory.getFactoryFor<HubInfiniteScroll>(HubInfiniteScroll));

