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
import IAugmentedJQuery = angular.IAugmentedJQuery;

interface IWiki {
    page: string,
    message: string,
    content: string
}


class WikiEditorController {
    public hubMarkdownEditor:any;
    public wiki: IWiki;

    static $inject = ['HomeService', '$mdDialog', 'locals', '$timeout'];
    constructor(private api:any, private $mdDialog, public locals, private $timeout) {
        this.$timeout(() => {
            this.hubMarkdownEditor = new SimpleMDE({ autoDownloadFA: false, element: angular.element('#markdown-area')[0] });
        });

        // Update if wiki is present
        if (this.locals.wiki) {
            // TODO updating a wiki, needs to get markdown
        }
    }

    public cancel():void {
        this.$mdDialog.cancel();
    }

    public createWIki(wiki: IWiki):void {
        wiki.content = this.hubMarkdownEditor.value();

        var params = { componentId: this.locals.component.id, page: wiki.page, message: wiki.message};
        var createWikiAction = { createWiki: { method: 'POST', headers: {'Content-Type': 'text/markdown'}}};

        this.locals.component
            .$links('wiki', params, createWikiAction)
            .createWiki(null, wiki.content)
            .$promise
            .then(r => this.$mdDialog.hide(r))
            .catch(WikiEditorController.promiseRejected);
    }

    public deleteWiki(wiki: string): void {

    }

    public updateWiki(): void {
        if (this.locals.wiki) {

        }
    }

    public static promiseRejected(reason):void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

angular
    .module(module.angularModules)
    .controller('WikiEditorController', WikiEditorController);