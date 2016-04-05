define(["require", "exports", './module', "{angular}/angular"], function (require, exports, module, angular) {
    "use strict";
    var UserController = (function () {
        function UserController(api, $location, authService, eventService) {
            var _this = this;
            this.api = api;
            this.$location = $location;
            this.authService = authService;
            this.eventService = eventService;
            this.userComponents = [];
            this.favoriteComponents = [];
            this.userPrincipals = this.authService.subjectPrincipals();
            this.getUserComponents().$promise.then(function (components) {
                _this.userComponents = components.$embedded('components');
                _this.userComponentsCount = components.resultSize;
            });
            this.getUserStarredComponents().$promise.then(function (components) {
                _this.favoriteComponents = components.$embedded('components');
                _this.favoriteComponentsCount = components.resultSize;
            });
        }
        ;
        UserController.prototype.getUserComponents = function () {
            return this.api('home').enter('user/components', {}).get();
        };
        UserController.prototype.getUserStarredComponents = function () {
            return this.api('home').enter('stars', {}).get();
        };
        UserController.prototype.view = function (component) {
            this.$location.path('hub/component/' + component.id);
        };
        UserController.$inject = ['HomeService', '$location', 'AuthenticationService', 'EventService'];
        return UserController;
    }());
    angular
        .module(module.angularModules)
        .controller('UserController', UserController);
});

//# sourceMappingURL=user.js.map
