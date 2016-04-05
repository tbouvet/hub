/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define(["require", "exports", '../../module', '{angular}/angular'], function (require, exports, module, angular) {
    "use strict";
    var RepositoryType;
    (function (RepositoryType) {
        RepositoryType[RepositoryType["GIT"] = 'GIT'] = "GIT";
        RepositoryType[RepositoryType["SVN"] = 'SVN'] = "SVN";
    })(RepositoryType || (RepositoryType = {}));
    var VcsUrlValidator = (function () {
        function VcsUrlValidator() {
            this.require = 'ngModel';
            this.scope = { repository: '=' };
            this.link = function (scope, element, attributes, ngModel) {
                ngModel.$validators['isRepoUrl'] = function (modelValue, viewValue) {
                    switch (scope['repository'].vcs) {
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
    var AddComponentController = (function () {
        function AddComponentController(api, $mdDialog, $httpParamSerializer) {
            this.api = api;
            this.$mdDialog = $mdDialog;
            this.$httpParamSerializer = $httpParamSerializer;
            this.repositoryTypes = [RepositoryType.GIT, RepositoryType.SVN];
            this.repository = {};
            this.failure = false;
            this.repository.vcs = this.repositoryTypes[0];
            this.repository.url = '';
        }
        ;
        AddComponentController.prototype.cancel = function () {
            this.$mdDialog.cancel();
        };
        ;
        AddComponentController.prototype.confirm = function (repository) {
            var _this = this;
            var action = {
                save: {
                    method: 'POST',
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    }
                }
            };
            var formParam = this.$httpParamSerializer({ 'url': repository.url, 'vcs': repository.vcs });
            this.promise = this.api('home').enter('components', {}, action).save({}, formParam);
            this.promise.$promise
                .then(function (newComponent) {
                _this.$mdDialog.hide(newComponent);
            })
                .catch(function (reason) {
                _this.failure = true;
                throw new Error(reason.statusText);
            });
        };
        ;
        AddComponentController.$inject = ['HomeService', '$mdDialog', '$httpParamSerializer'];
        return AddComponentController;
    }());
    angular
        .module(module.angularModules)
        .directive('vcsUrlValidator', DirectiveFactory.getFactoryFor(VcsUrlValidator))
        .controller('AddComponentController', AddComponentController);
});

//# sourceMappingURL=add-component.js.map
