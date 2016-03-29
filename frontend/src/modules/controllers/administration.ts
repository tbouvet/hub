import module = require('./module');
import angular = require('{angular}/angular');
import _ = require('{lodash}/lodash');
import IResource = angular.resource.IResource;

class AdminController {
    public pendingComponents: Card[];
    public published: boolean;

    static $inject = ['HomeService', '$location', '$mdToast'];
    constructor(private api: any, private location: ng.ILocationService, private $mdToast) {
        this.pendingComponents = [];

        this.getPendingComponents()
            .$promise
            .then((components: any) => {
                this.pendingComponents = components.$embedded('components');
            }).catch(AdminController.promiseRejected);
    }

    public getPendingComponents(): IResource {
        return this.api('home').enter('pending').get();
    }

    public publishComponent(component): void {
        component.$links('state', {}, { publish: { method: 'PUT' }})
            .publish('PUBLISHED')
            .$promise
            .then(() => {
                _.remove(this.pendingComponents, c => c.id === component.id);
                this.$mdToast.show(
                    this.$mdToast.simple()
                        .textContent(component.name + ' has been published!')
                        .position('top right')
                        .hideDelay(3000)
                );
            })
            .catch(rejected => {
                throw new Error(rejected.data);
            });
    }

    private static promiseRejected(reason): void {
        throw new Error(reason);
    }

    public view(card: Card): void {
        this.location.path('hub/component/' + card.id);
    }
}

angular
    .module(module.angularModules)
    .controller('AdminController', AdminController);