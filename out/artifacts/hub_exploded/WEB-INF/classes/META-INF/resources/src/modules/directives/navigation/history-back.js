define(["require", "exports", '../module', '{angular}/angular'], function (require, exports, module, angular) {
    "use strict";
    var HubHistoryBack = (function () {
        function HubHistoryBack($window) {
            var _this = this;
            this.$window = $window;
            this.restrict = 'A';
            this.link = function (scope, element) {
                element.on('click', function () {
                    _this.$window.history.back();
                });
            };
        }
        ;
        HubHistoryBack.$inject = ['$window'];
        return HubHistoryBack;
    }());
    angular
        .module(module.angularModules)
        .directive('hubHistoryBack', DirectiveFactory.getFactoryFor(HubHistoryBack));
});

//# sourceMappingURL=history-back.js.map
