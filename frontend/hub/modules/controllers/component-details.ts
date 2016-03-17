/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

class ComponentDetailsController {
    public component: any;

    static $inject = ['HomeService', '$routeParams'];
    constructor(private api: any, private $routeParams: any) {
        this.getComponent().$promise.then((component: any) => {
            this.component = component;
        });
    }

    public getComponent (): IResource {
        return this.api('home').enter('component', { componentId: this.$routeParams.id }).get();
    }

    public starComponent (component): void {
        // todo use link from hypermedia
    }
}

angular
    .module(module.angularModules)
    .controller('ComponentDetailsController', ComponentDetailsController);