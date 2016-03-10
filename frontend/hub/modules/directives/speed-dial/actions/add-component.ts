/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import module = require('../../module');
import angular = require('{angular}/angular');

enum RepositoryType {
    GIT = <any> 'GIT',
    SVN = <any> 'SVN'
}

interface IRepository {
    type: RepositoryType;
    url: string;
}

class VcsUrlValidator implements ng.IDirective {
    require:string = 'ngModel';
    scope:any = {repository: '='};
    link:ng.IDirectiveLinkFn = (scope:ng.IScope, element:ng.IAugmentedJQuery, attributes:ng.IAttributes, ngModel:ng.INgModelController) => {
        ngModel.$validators['isRepoUrl'] = (modelValue, viewValue) => {
            switch (scope['repository'].type) {
                case RepositoryType.GIT:
                    return /(?:git|ssh|https?|git@[\w\.]+):(?:\/\/)?[\w\.@:\/~_-]+\.git(?:\/?|\#[\d\w\.\-_]+?)$/.test(viewValue);
                default:
                    return true;
            }
        };
        scope.$watch('repository', () => {
            ngModel.$validate();
        }, true);
    };
}

class AddComponentController {
    public repositoryTypes:RepositoryType[] = [RepositoryType.GIT, RepositoryType.SVN];
    public repository:IRepository = <any>{};
    public promise:ng.resource.IResource<any>;
    public failure:boolean = false;

    static $inject = ['HomeService', '$mdDialog'];

    constructor(private api, private $mdDialog) {
        this.repository.type = this.repositoryTypes[0];
        this.repository.url = '';
    };

    public cancel():void {
        this.$mdDialog.cancel();
    };

    public confirm(repository:IRepository):void {
        this.promise = this.api('home').enter('components', {}).save({}, repository);
        this.promise.$promise
            .then(newComponent => {
                this.$mdDialog.hide(newComponent);
            })
            .catch(reason => {
                this.failure = true;
                throw new Error(reason);
            });
    };
}

angular
    .module(module.angularModules)
    .directive('vcsUrlValidator', DirectiveFactory.getFactoryFor<VcsUrlValidator>(VcsUrlValidator))
    .controller('AddComponentController', AddComponentController);
