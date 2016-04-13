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
import IResource = angular.resource.IResource;

interface WikiPageInfo {
    title: string,
    source: string
}

class WikiEditorController {
    public hubMarkdownEditor:any;
    public message:string;
    public wikiPageInfo:WikiPageInfo;

    static $inject = ['HomeService', '$mdDialog', 'locals', '$timeout', '$route', '$templateCache'];

    constructor(private api:any, private $mdDialog, public locals, private $timeout, private $route, private $templateCache) {
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

            if (this.locals && this.locals.wiki) {

                WikiEditorController.getWikiInfo(this.locals.wiki).$promise
                    .then((wikiPageInfo:WikiPageInfo) => {
                        this.wikiPageInfo = wikiPageInfo;
                        this.hubMarkdownEditor.value(wikiPageInfo.source);
                    })
                    .catch(WikiEditorController.promiseRejected);

            } else {
                this.wikiPageInfo = {
                    title: '',
                    source: ''
                };
            }
        });


    }

    public static getWikiInfo(wiki):IResource {
        return wiki.$links('info').get();
    }

    public createWiki(message:string, wikiPageInfo:WikiPageInfo):void {
        wikiPageInfo.source = this.hubMarkdownEditor.value();

        this.locals.component
            .$links('wiki', {componentId: this.locals.component.id}, {
                create: {
                    method: 'POST',
                    headers: {'Accept': 'text/html'}
                }
            })
            .create({message: message}, wikiPageInfo)
            .$promise
            .then(html => {
                this.successAction(html);
            })
            .catch(WikiEditorController.promiseRejected);
    }

    public updateWiki(message:string, wikiPageInfo:WikiPageInfo):void {
        wikiPageInfo.source = this.hubMarkdownEditor.value();

        this.locals.wiki
            .$links('self', {}, {
                update: {
                    method: 'PUT',
                    headers: {'Accept': 'text/html '}
                }
            })
            .update({message: message}, wikiPageInfo)
            .$promise
            .then(html => {
                this.successAction(html);
            })
            .catch(WikiEditorController.promiseRejected);
    }

    public deleteWiki():void {
        this.locals.wiki
            .$links('self')
            .delete()
            .$promise
            .then(html => {
                this.successAction(html);
            })
            .catch(WikiEditorController.promiseRejected);
    }

    private successAction (value) {
        this.hubMarkdownEditor.clearAutosavedValue();
        this.$templateCache.removeAll();
        this.$route.reload();
        this.$mdDialog.hide(value)
    }

    public cancel():void {
        this.$mdDialog.cancel();
    }

    public static promiseRejected(reason):void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

angular
    .module(module.angularModules)
    .controller('WikiEditorController', WikiEditorController);