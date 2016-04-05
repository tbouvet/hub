define(["require", "exports", './module', "{angular}/angular"], function (require, exports, module, angular) {
    "use strict";
    var HomeController = (function () {
        function HomeController(api, location) {
            var _this = this;
            this.api = api;
            this.location = location;
            this.popularComponents = [];
            this.recentComponents = [];
            this.getPopularCards()
                .$promise
                .then(function (components) {
                _this.popularComponents = components.$embedded('components');
            }).catch(HomeController.promiseRejected);
            this.getRecentCards()
                .$promise
                .then(function (components) {
                _this.recentComponents = components.$embedded('components');
            }).catch(HomeController.promiseRejected);
        }
        HomeController.prototype.getPopularCards = function () {
            return this.api('home').enter('popular').get();
        };
        HomeController.prototype.getRecentCards = function () {
            return this.api('home').enter('recent').get();
        };
        HomeController.promiseRejected = function (reason) {
            throw new Error(reason);
        };
        HomeController.prototype.view = function (card) {
            this.location.path('hub/component/' + card.id);
        };
        HomeController.$inject = ['HomeService', '$location'];
        return HomeController;
    }());
    angular
        .module(module.angularModules)
        .controller('HomeController', HomeController);
});

//# sourceMappingURL=home.js.map
