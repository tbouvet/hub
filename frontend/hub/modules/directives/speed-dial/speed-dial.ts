/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import module = require('../module');
import angular = require('{angular}/angular');
import speedDialTemplate = require('[text]!{hub}/modules/directives/speed-dial/speed-dial.html');
import IAngularEvent = angular.IAngularEvent;

interface ISpeedDialScope extends ng.IScope {
    showAddComponent(event:IAngularEvent);
}

class HubSpeedDial implements ng.IDirective {

    template:string = speedDialTemplate;

    link:ng.IDirectiveLinkFn = (scope:ISpeedDialScope) => {
        scope.showAddComponent = event => {
            var useFullScreen = (this.$mdMedia('sm') || this.$mdMedia('xs'));
            var promise = this.$mdDialog.show({
                controller: 'AddComponentController',
                controllerAs: '$ctrl',
                templateUrl: require.toUrl('{hub}/modules/directives/speed-dial/actions/templates/component.tmpl.html'),
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: false,
                fullscreen: useFullScreen
            });

            promise.then(resolvedComponent => {
                this.$location.path('/hub/component/' + resolvedComponent.name);
            });
        };
    };

    static $inject = ['$mdMedia', '$mdDialog', '$location'];
    constructor(private $mdMedia, private $mdDialog, private $location: ng.ILocationService) {
    };
}

angular
    .module(module.angularModules)
    .directive('hubSpeedDial', DirectiveFactory.getFactoryFor<HubSpeedDial>(HubSpeedDial));

