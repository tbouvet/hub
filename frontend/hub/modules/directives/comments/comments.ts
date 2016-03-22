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

interface PaginationCriteria {
    pageIndex: number,
    pageSize: number
}

interface ICommentScope extends ng.IScope {
    submitComment(comment: string): void;
    newCommentText: string;
    paginationCriterias: PaginationCriteria;
    loadNewComments(criterias):void;
    comments: Comment[];
    component: any;
}
class HubComment implements ng.IDirective {
    static $inject = ['HomeService', '$resource'];
    constructor(private api, private $resource) {};
    scope = {
      component: '='
    };
    template = commentTemplate;
    link: ng.IDirectiveLinkFn = (scope: ICommentScope) => {
        scope.comments = [];
        scope.newCommentText = '';

        scope.paginationCriterias = {
            pageIndex: 0,
            pageSize: 10
        };

        scope.submitComment = (text: string) => {
          scope.component.$links('comment').save(text).$promise.then((comment: IComment) => {
              scope.newCommentText = '';
              scope.comments.push(comment);
          }, (reject) => { throw new Error(reject); });
        };

        scope.loadNewComments = () => {
            scope.component.$links('comment', scope.paginationCriterias).get((results: any) => {
                if (results.$embedded('comment').view.length) {
                    scope.comments = <Comment[]> scope.comments.concat(results.$embedded('comment').view);
                    scope.paginationCriterias.pageIndex++;
                }
            }, (reject) => { throw new Error(reject); });
        };
    }
}

angular
    .module(module.angularModules)
    .directive('hubComment', DirectiveFactory.getFactoryFor<HubComment>(HubComment));
