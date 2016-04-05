/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/// <amd-dependency path="[css]!{simplemde}/simplemde.css"/>
import module = require('./module');
import angular = require("{angular}/angular");
import SimpleMDE = require("{simplemde}/simplemde");
import IResource = angular.resource.IResource;

class WikiEditorController {
    public markdownEditor: any;

    static $inject = ['HomeService', '$mdDialog', '$mdMedia'];
    constructor(private api:any, private $mdDialog, private $mdMedia) {
        this.markdownEditor = new SimpleMDE();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }

    public static promiseRejected(reason):void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

angular
    .module(module.angularModules)
    .controller('WikiEditorController', WikiEditorController);