import module = require('./module');
import angular = require('{angular}/angular');
import _ = require('{lodash}/lodash');
import IResource = angular.resource.IResource;
import {GithubService} from "../services/github-service";


class AdminController {
    public pendingComponents: Card[];

    static $inject = ['HomeService', '$location', '$mdToast'];
    constructor(private api: any, private $location: ng.ILocationService, private $mdToast) {
        this.pendingComponents = [];

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

    public view (card: Card): void {
        this.$location.path('hub/component/' + card.id);
    }

    private static promiseRejected (reason): void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

angular
    .module(module.angularModules)
    .controller('AdminController', AdminController);