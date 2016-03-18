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

interface Comment {
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
    paginationCriterias: PaginationCriteria;
    loadNewComments():void;
    comments: Comment[];
    component: any;
}
class HubComment implements ng.IDirective {
    static $inject = ['HomeService'];
    constructor(private api) {};
    scope = {
      component: '='
    };
    template = commentTemplate;
    link: ng.IDirectiveLinkFn = (scope: ICommentScope) => {
        scope.comments = [];

        scope.paginationCriterias = {
            pageIndex: 0,
            pageSize: 10
        };

        scope.submitComment = (text: string) => {
          scope.component.$links('comment').save(text).$promise.then(() => {
              scope.comments
          }, (reject) => { throw new Error(reject); });
        };

        scope.loadNewComments = () => {
            alert(scope.component.name)
            //this.api('home').enter('')
        };


    }
}

angular
    .module(module.angularModules)
    .directive('hubComment', DirectiveFactory.getFactoryFor<HubComment>(HubComment));
