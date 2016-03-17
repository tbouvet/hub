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

interface ICommentScope extends ng.IScope {
    submitComment(comment: string): void
}
class HubComment implements ng.IDirective {
    static $inject = ['HomeService'];
    constructor(private api) {};
    template = commentTemplate;
    link: ng.IDirectiveLinkFn = (scope: ICommentScope) => {
        scope.submitComment = (comment) => {
            alert(comment);
        };

        scope.comments = [
            {
                author: 'pith',
                publicationDate: '1/2/3',
                text: 'Lorem ipsum sin dolor amet comment inutile mock test'
            },
            {
                author: 'kavi87',
                publicationDate: '1/2/3',
                text: 'Lorem ipsum sin dolor amet comment inutile mock test'
            }
        ]
    }
}

angular
    .module(module.angularModules)
    .directive('hubComment', DirectiveFactory.getFactoryFor<HubComment>(HubComment));
