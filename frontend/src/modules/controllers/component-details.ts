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

enum SubView {
    IDENTITY = <any> 'identity'
}

function displayDocName() {
    return function(input) {
        return input.split('/').pop();
    };
}

class ComponentDetailsController {
    public component: any;
    public detailsView: SubView;

    static $inject = ['HomeService', '$routeParams'];
    constructor(private api: any, private $routeParams: any) {

        this.detailsView = SubView.IDENTITY;

        this.getComponent().$promise.then((component: any) => {
            this.component = component;
        });
    }

    public getComponent (): IResource {
        return this.api('home').enter('component', { componentId: this.$routeParams.id }).get();
    }

    public rateComponent (component): void {
        if (component.isStarred) {
            this.unStarComponent(component);
        } else {
            this.starComponent(component);
        }
    }

    private starComponent (component): void {
        component.$links('star').save(() => {
            component.isStarred = true;
            component.stars++;
        }, (reject) => { throw new Error(reject); });
    }

    private unStarComponent (component): void {
        component.$links('star').delete(() => {
            component.isStarred = false;
            if (component.stars > 0) {
                component.stars--;
            }
        }, (reject) => { throw new Error(reject); })
    }
}

angular
    .module(module.angularModules)
    .filter('displayDocName', displayDocName)
    .controller('ComponentDetailsController', ComponentDetailsController);