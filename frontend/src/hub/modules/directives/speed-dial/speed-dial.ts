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
    showImportComponent(event:IAngularEvent);
}

class HubSpeedDial implements ng.IDirective {

    static $inject = ['$mdMedia', '$mdDialog', '$location'];
    constructor(private $mdMedia, private $mdDialog, private $location: ng.ILocationService) {
    };

    template:string = speedDialTemplate;

    link:ng.IDirectiveLinkFn = (scope:ISpeedDialScope) => {
        scope.showImportComponent = event => {
            this.$location.path('/hub/import-component');
        };
    };
}

angular
    .module(module.angularModules)
    .directive('hubSpeedDial', DirectiveFactory.getFactoryFor<HubSpeedDial>(HubSpeedDial));

