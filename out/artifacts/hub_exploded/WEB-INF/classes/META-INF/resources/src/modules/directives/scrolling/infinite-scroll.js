define(["require", "exports", '../module', '{angular}/angular'], function (require, exports, module, angular) {
    "use strict";
    var HubInfiniteScroll = (function () {
        function HubInfiniteScroll($timeout) {
            var _this = this;
            this.$timeout = $timeout;
            this.restrict = 'A';
            this.scope = {
                onBottomReached: '&',
                criterias: '='
            };
            this.link = function (scope, element) {
                _this.$timeout(function () {
                    var parent = angular.element('#view');
                    var raw = parent[0];
                    parent.bind('scroll', function () {
                        if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                            scope.onBottomReached({ criterias: scope.criterias });
                        }
                    });
                });
            };
        }
        ;
        HubInfiniteScroll.$inject = ['$timeout'];
        return HubInfiniteScroll;
    }());
    angular
        .module(module.angularModules)
        .directive('hubInfiniteScroll', DirectiveFactory.getFactoryFor(HubInfiniteScroll));
});

//# sourceMappingURL=infinite-scroll.js.map
