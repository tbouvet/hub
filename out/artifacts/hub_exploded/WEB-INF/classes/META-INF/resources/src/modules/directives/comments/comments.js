/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define(["require", "exports", '../module', '{angular}/angular', '[text]!{hub}/modules/directives/comments/comment.tmpl.html'], function (require, exports, module, angular, commentTemplate) {
    "use strict";
    var HubComment = (function () {
        function HubComment(api, $resource) {
            this.api = api;
            this.$resource = $resource;
            this.scope = {
                component: '='
            };
            this.template = commentTemplate;
            this.link = function (scope) {
                var criterias = { size: 10 };
                scope.comments = [];
                scope.newCommentText = '';
                var clearForm = function () {
                    scope.commentForm.$setPristine();
                    scope.commentForm.$setUntouched();
                    scope.newCommentText = '';
                };
                scope.submitComment = function (text) {
                    if (scope.component) {
                        scope.component.$links('comment', { componentId: scope.component.id }).save(text).$promise.then(function (comment) {
                            clearForm();
                            scope.comments.push(comment);
                        }, function (reject) {
                            throw new Error(reject);
                        });
                    }
                };
                var nextLink;
                scope.loadNewComments = function () {
                    if (scope.component) {
                        var fetchFunction = nextLink ? nextLink : scope.component.$links('comment', { componentId: scope.component.id });
                        if (fetchFunction.get) {
                            fetchFunction.get(criterias).$promise
                                .then(function (results) {
                                nextLink = results.$links('next') || angular.noop;
                                if (results.$embedded('comment').length) {
                                    scope.comments = scope.comments.concat(results.$embedded('comment'));
                                }
                            })
                                .catch(function (reject) {
                                throw new Error(reject);
                            });
                        }
                    }
                };
            };
        }
        ;
        HubComment.$inject = ['HomeService', '$resource'];
        return HubComment;
    }());
    angular
        .module(module.angularModules)
        .directive('hubComment', DirectiveFactory.getFactoryFor(HubComment));
});

//# sourceMappingURL=comments.js.map
