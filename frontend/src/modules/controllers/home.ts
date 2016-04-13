import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

class HomeController {
    public recentComponents: Card[];
    public popularComponents: Card[];
    private NUMBER_OF_RECENT = 5;
    private NUMBER_OF_POPULAR = 12;

    static $inject = ['HomeService', '$location'];
    constructor(private api: any, private location: ng.ILocationService) {

        this.popularComponents = [];
        this.recentComponents = [];

        this.getRecentCards(this.NUMBER_OF_RECENT).$promise.then((components: any) => {
                this.recentComponents = components.$embedded('components');
            }).catch(HomeController.promiseRejected);

        this.getPopularCards(this.NUMBER_OF_POPULAR).$promise.then((components: any) => {
                this.popularComponents = components.$embedded('components');
            }).catch(HomeController.promiseRejected);
    }

    public getPopularCards(size: number): IResource {
        return this.api('home').enter('popular').get({ size: size });
    }

    public getRecentCards(size: number): IResource {
        return this.api('home').enter('recent').get({ size: size });
    }

    private static promiseRejected(reason): void {
        throw new Error(reason)
    }

    public viewCard(card: Card): void {
        this.location.path('hub/component/' + card.id);
    }
}

angular
    .module(module.angularModules)
    .controller('HomeController', HomeController);