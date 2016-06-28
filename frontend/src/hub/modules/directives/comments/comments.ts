/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import module = require('../module');
import angular = require('{angular}/angular');
import commentTemplate = require('[text]!{hub}/modules/directives/comments/comment.tmpl.html');
import Collator = Intl.Collator;

interface IComment {
    author: string,
    publicationDate: string,
    text: string
}

interface ICommentScope extends ng.IScope {
    submitComment(comment:string): void;
    newCommentText: string;
    loadNewComments(criterias):void;
    criterias: { size: number, offset?: number }
    comments: IComment[];
    component: any;
    commentForm: ng.IFormController;
    clearForm: Function
}

class HubComment implements ng.IDirective {
    static $inject = ['HomeService', '$resource'];

    constructor(private api, private $resource) {
    };

    scope = {
        component: '='
    };
    template = commentTemplate;
    link:ng.IDirectiveLinkFn = (scope:ICommentScope) => {
        scope.criterias = {size: 10};
        scope.comments = [];
        scope.newCommentText = '';

        var clearForm = () => {
            scope.commentForm.$setPristine();
            scope.commentForm.$setUntouched();
            scope.newCommentText = '';
        };

        scope.submitComment = (text:string) => {
            if (scope.component) {
                scope.component.$links('comment', {componentId: scope.component.id}).save(text).$promise
                    .then((comment:IComment) => {
                        clearForm();
                        if (scope.comments.length) {
                            scope.comments.unshift(comment);
                        } else {
                            scope.loadNewComments(scope.criterias);
                        }
                    })
                    .catch((reject:any) => {
                        throw new Error(reject);
                    });
            }
        };

        var nextLink:Function;

        scope.loadNewComments = (criterias) => {
            if (scope.component) {
                var fetchFunction = nextLink ? nextLink : scope.component.$links('comment', {componentId: scope.component.id});
                if (fetchFunction.get) {
                    fetchFunction.get(criterias).$promise
                        .then((results:any) => {
                            nextLink = results.$links('next') || angular.noop;
                            if (results.$embedded('comment').length) {
                                scope.comments = scope.comments.concat(results.$embedded('comment'));
                            }
                        })
                        .catch((reject:any) => {
                            throw new Error(reject);
                        });
                }

            }
        };
    }
}

angular
    .module(module.angularModules)
    .directive('hubComment', DirectiveFactory.getFactoryFor<HubComment>(HubComment));
