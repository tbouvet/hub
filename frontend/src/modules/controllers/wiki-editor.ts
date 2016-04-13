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

interface WikiPageInfo {
    title: string,
    source: string
}

class WikiEditorController {
    public hubMarkdownEditor:any;
    public message: string;
    public wikiPageInfo: WikiPageInfo;

    static $inject = ['HomeService', '$mdDialog', 'locals', '$timeout', '$route'];
    constructor(private api:any, private $mdDialog, public locals, private $timeout, private $route) {
        this.$timeout(() => {
            this.hubMarkdownEditor = new SimpleMDE({
                autoDownloadFA: false,
                element: angular.element('#markdown-area')[0],
                autosave: {
                    enabled: true,
                    uniqueId: "wiki-edition",
                    delay: 1000,
                }
            });
        });

        var wikiToUpdate  = this.locals.wiki;

        if (wikiToUpdate) {

        } else {
            this.wikiPageInfo = { title: '', source: '' };
        }
    }

    public cancel():void {
        this.$mdDialog.cancel();
    }

    public createWIki(message: string, wikiPageInfo: WikiPageInfo):void {
        wikiPageInfo.source = this.hubMarkdownEditor.value();

        this.locals.component
            .$links('wiki', { componentId: this.locals.component.id }, { create: { method: 'POST', headers: { 'Accept': 'text/html' }}})
            .create({ message: message }, wikiPageInfo)
            .$promise
            .then(html => {
                this.hubMarkdownEditor.clearAutosavedValue();
                this.$route.reload();
                this.$mdDialog.hide(html)
            })
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