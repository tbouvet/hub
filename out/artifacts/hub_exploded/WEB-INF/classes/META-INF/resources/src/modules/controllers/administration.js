define(["require", "exports", './module', '{angular}/angular', '{lodash}/lodash'], function (require, exports, module, angular, _) {
    "use strict";
    var AdminController = (function () {
        function AdminController(api, $location, $window, $mdToast, githubService, $httpParamSerializer) {
            var _this = this;
            this.api = api;
            this.$location = $location;
            this.$window = $window;
            this.$mdToast = $mdToast;
            this.githubService = githubService;
            this.$httpParamSerializer = $httpParamSerializer;
            this.SEARCH_LIMIT = 100;
            this.pendingComponents = [];
            this.githubSearchQuery = '';
            this.searchedGithubComponents = [];
            this.selectedRepositories = [];
            this.importing = false;
            this.getPendingComponents()
                .$promise
                .then(function (components) { _this.pendingComponents = components.$embedded('components'); })
                .catch(AdminController.promiseRejected);
        }
        AdminController.prototype.getPendingComponents = function () {
            return this.api('home').enter('pending').get();
        };
        AdminController.prototype.publishComponent = function (component) {
            var _this = this;
            component.$links('state', {}, { publish: { method: 'PUT' } })
                .publish('PUBLISHED')
                .$promise
                .then(function () {
                _.remove(_this.pendingComponents, function (c) { return c.id === component.id; });
                _this.toast(component.name + ' has been published!');
            })
                .catch(AdminController.promiseRejected);
        };
        AdminController.prototype.toast = function (message) {
            this.$mdToast.show(this.$mdToast.simple()
                .textContent(message)
                .position('top right')
                .hideDelay(3000));
        };
        AdminController.prototype.searchGithubRepositories = function (query) {
            var _this = this;
            this.githubService.searchUserRepositories(query, this.SEARCH_LIMIT)
                .then(function (results) { _this.searchedGithubComponents = results; })
                .catch(AdminController.promiseRejected);
        };
        AdminController.prototype.importComponents = function (selectedComponents) {
            var _this = this;
            this.importing = true;
            selectedComponents = AdminController.formatGithubRepositories(selectedComponents);
            this.api('home').enter('import_list').save({}, selectedComponents).$promise
                .then(function (result) {
                _this.importing = false;
                _this.searchedGithubComponents = [];
                _this.selectedRepositories = [];
                _this.githubSearchQuery = '';
                selectedComponents = [];
                _this.toast('All components have been successfully imported!');
                _this.getPendingComponents()
                    .$promise
                    .then(function (components) { _this.pendingComponents = components.$embedded('components'); })
                    .catch(AdminController.promiseRejected);
            })
                .catch(function (reason) {
                _this.importing = false;
                _this.toast('Oh no ! An error occured ! We could not import your component');
                throw new Error(reason.statusText || reason.data || reason);
            });
        };
        AdminController.prototype.view = function (card) {
            this.$location.path('hub/component/' + card.id);
        };
        AdminController.formatGithubRepositories = function (results) {
            return results.map(function (c) {
                return { url: c.full_name, vcsType: 'GITHUB' };
            });
        };
        AdminController.promiseRejected = function (reason) {
            throw new Error(reason.statusText || reason.data || reason);
        };
        AdminController.$inject = [
            'HomeService',
            '$location',
            '$window',
            '$mdToast',
            'GithubService',
            '$httpParamSerializer'
        ];
        return AdminController;
    }());
    angular
        .module(module.angularModules)
        .controller('AdminController', AdminController);
});

//# sourceMappingURL=administration.js.map
