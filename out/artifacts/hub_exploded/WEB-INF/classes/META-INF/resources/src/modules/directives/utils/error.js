define(["require", "exports", '../module', '{angular}/angular'], function (require, exports, module, angular) {
    "use strict";
    var ErrSrc = (function () {
        function ErrSrc() {
            this.restrict = 'A';
            this.scope = {
                onBottomReached: '&',
                criterias: '='
            };
            this.link = function (scope, element, attrs) {
                element.bind('error', function () {
                    if (attrs['src'] != attrs['errSrc']) {
                        attrs.$set('src', attrs['errSrc']);
                    }
                });
            };
        }
        ;
        ErrSrc.$inject = [];
        return ErrSrc;
    }());
    angular
        .module(module.angularModules)
        .directive('errSrc', DirectiveFactory.getFactoryFor(ErrSrc));
});

//# sourceMappingURL=error.js.map
