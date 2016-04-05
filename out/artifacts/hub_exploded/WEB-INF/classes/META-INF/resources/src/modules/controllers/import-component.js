define(["require", "exports", './module', '{angular}/angular'], function (require, exports, module, angular) {
    "use strict";
    var RepositoryType;
    (function (RepositoryType) {
        RepositoryType[RepositoryType["GIT"] = 'GIT'] = "GIT";
        RepositoryType[RepositoryType["SVN"] = 'SVN'] = "SVN";
    })(RepositoryType || (RepositoryType = {}));
    var ImportComponentController = (function () {
        function ImportComponentController() {
        }
        ImportComponentController.promiseRejected = function (reason) {
            throw new Error(reason.statusText || reason.data || reason);
        };
        return ImportComponentController;
    }());
    var QuickImportController = (function () {
        function QuickImportController(api, $httpParamSerializer, $location) {
            this.api = api;
            this.$httpParamSerializer = $httpParamSerializer;
            this.$location = $location;
            this.repositoryTypes = [RepositoryType.GIT, RepositoryType.SVN];
            this.repository = {};
            this.importing = false;
            this.failure = false;
            this.success = false;
            this.repository.sourceType = this.repositoryTypes[0];
            this.repository.sourceUrl = '';
        }
        ;
        QuickImportController.prototype.confirm = function (repository) {
            var _this = this;
            this.importing = true;
            var action = {
                save: {
                    method: 'POST',
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    }
                }
            };
            var formParam = this.$httpParamSerializer({
                'sourceUrl': repository.sourceUrl,
                'sourceType': repository.sourceType
            });
            this.api('home').enter('components', {}, action).save({}, formParam)
                .$promise
                .then(function (newComponent) {
                if (newComponent) {
                    _this.success = true;
                    _this.newComponent = newComponent;
                }
            })
                .catch(function (reason) {
                _this.failure = true;
                ImportComponentController.promiseRejected(reason);
            });
        };
        ;
        QuickImportController.prototype.terminate = function () {
            this.importing = false;
            this.success = false;
            this.failure = false;
        };
        QuickImportController.prototype.view = function (card) {
            this.$location.path('hub/component/' + card.id);
        };
        QuickImportController.$inject = ['HomeService', '$httpParamSerializer', '$location'];
        return QuickImportController;
    }());
    var GithubImportController = (function () {
        function GithubImportController(api, $location, $window, $mdToast, githubService, $httpParamSerializer) {
            this.api = api;
            this.$location = $location;
            this.$window = $window;
            this.$mdToast = $mdToast;
            this.githubService = githubService;
            this.$httpParamSerializer = $httpParamSerializer;
            this.SEARCH_LIMIT = 100;
            this.githubSearchQuery = '';
            this.searchedGithubComponents = [];
            this.selectedRepositories = [];
            this.importing = false;
        }
        GithubImportController.prototype.toast = function (message) {
            this.$mdToast.show(this.$mdToast.simple()
                .textContent(message)
                .position('top right')
                .hideDelay(3000));
        };
        GithubImportController.prototype.searchGithubRepositories = function (query) {
            var _this = this;
            this.githubService.searchUserRepositories(query, this.SEARCH_LIMIT)
                .then(function (results) {
                _this.searchedGithubComponents = results.map(function (result) {
                    return { avatar: result.owner['avatar_url'], full_name: result.full_name };
                });
            })
                .catch(ImportComponentController.promiseRejected);
        };
        GithubImportController.prototype.importComponents = function (selectedComponents) {
            var _this = this;
            if (!selectedComponents.length) {
                this.toast('You did not select any repositories!');
                return;
            }
            this.importing = true;
            selectedComponents = GithubImportController.formatGithubRepositories(selectedComponents);
            this.api('home').enter('import_list').save({}, selectedComponents).$promise
                .then(function (result) {
                _this.importing = false;
                _this.searchedGithubComponents = [];
                _this.selectedRepositories = [];
                _this.githubSearchQuery = '';
                _this.toast('All components have been successfully imported!');
            })
                .catch(function (reason) {
                _this.importing = false;
                _this.toast('Oh no ! An error occurred ! We could not import your component');
                ImportComponentController.promiseRejected(reason);
            });
        };
        GithubImportController.prototype.view = function (card) {
            this.$location.path('hub/component/' + card.id);
        };
        GithubImportController.formatGithubRepositories = function (results) {
            return results.map(function (c) {
                return { url: c.full_name, sourceType: 'GITHUB' };
            });
        };
        GithubImportController.$inject = [
            'HomeService',
            '$location',
            '$window',
            '$mdToast',
            'GithubService',
            '$httpParamSerializer'
        ];
        return GithubImportController;
    }());
    var VcsUrlValidator = (function () {
        function VcsUrlValidator() {
            this.require = 'ngModel';
            this.scope = { repository: '=' };
            this.link = function (scope, element, attributes, ngModel) {
                ngModel.$validators['isRepoUrl'] = function (modelValue, viewValue) {
                    switch (scope['repository'].sourceType) {
                        case RepositoryType.GIT:
                            return /(?:git|ssh|https?|git@[\w\.]+):(?:\/\/)?[\w\.@:\/~_-]+\.git(?:\/?|\#[\d\w\.\-_]+?)$/.test(viewValue);
                        default:
                            return true;
                    }
                };
                scope.$watch('repository', function () {
                    ngModel.$validate();
                }, true);
            };
        }
        return VcsUrlValidator;
    }());
    angular
        .module(module.angularModules)
        .controller('ImportComponentController', ImportComponentController)
        .controller('QuickImportController', QuickImportController)
        .controller('GithubImportController', GithubImportController);
});

//# sourceMappingURL=import-component.js.map
