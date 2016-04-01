import module = require('./module');
import angular = require('{angular}/angular');
import _ = require('{lodash}/lodash');
import IResource = angular.resource.IResource;
import {GithubService} from "../services/github-service";


class AdminController {
    public pendingComponents: Card[];
    public githubSearchQuery: string;
    public searchedGithubComponents: Card[];
    public selectedRepositories: any[];
    public importing: boolean;
    private SEARCH_LIMIT = 100;

    static $inject = [
        'HomeService',
        '$location',
        '$window',
        '$mdToast',
        'GithubService',
        '$httpParamSerializer'
    ];
    constructor(
        private api: any,
        private $location: ng.ILocationService,
        private $window: ng.IWindowService,
        private $mdToast,
        private githubService: GithubService,
        private $httpParamSerializer
    ) {
        this.pendingComponents = [];
        this.githubSearchQuery = '';
        this.searchedGithubComponents = [];
        this.selectedRepositories = [];
        this.importing = false;

        this.getPendingComponents ()
            .$promise
            .then((components: any) => { this.pendingComponents = components.$embedded('components'); })
            .catch(AdminController.promiseRejected);
    }

    public getPendingComponents (): IResource {
        return this.api('home').enter('pending').get();
    }

    public publishComponent (component: any): void {
        component.$links('state', {}, { publish: { method: 'PUT' }})
            .publish('PUBLISHED')
            .$promise
            .then(() => {
                _.remove(this.pendingComponents, c => c.id === component.id);
                this.toast(component.name + ' has been published!');
            })
            .catch(AdminController.promiseRejected);
    }

    private toast (message: string) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(message)
                .position('top right')
                .hideDelay(3000)
        );
    }

    public searchGithubRepositories (query: string): void {
        this.githubService.searchUserRepositories(query, this.SEARCH_LIMIT)
            .then((results) => { this.searchedGithubComponents = results; })
            .catch(AdminController.promiseRejected);
    }

    public importComponents (selectedComponents): void {
         this.importing = true;
         selectedComponents = AdminController.formatGithubRepositories(selectedComponents);
         this.api('home').enter('import_list').save({}, selectedComponents).$promise
            .then(result => {
                this.importing = false;
                this.searchedGithubComponents = [];
                this.selectedRepositories = [];
                this.githubSearchQuery = '';
                selectedComponents = [];
                this.toast('All components have been successfully imported!');

                this.getPendingComponents ()
                    .$promise
                    .then((components: any) => { this.pendingComponents = components.$embedded('components'); })
                    .catch(AdminController.promiseRejected);
            })
            .catch((reason: any) => {
                this.importing = false;
                this.toast('Oh no ! An error occured ! We could not import your component');
                throw new Error(reason.statusText || reason.data || reason);
            });
    }

    public view (card: Card): void {
        this.$location.path('hub/component/' + card.id);
    }

    private static formatGithubRepositories (results) {
        return results.map((c: any) => {
            return { url: c.full_name, vcsType: 'GITHUB' };
        });
    }

    private static promiseRejected (reason): void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

angular
    .module(module.angularModules)
    .controller('AdminController', AdminController);