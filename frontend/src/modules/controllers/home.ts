import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

class HomeController {
    public popularComponents: Card[];
    public recentComponents: Card[];

    static $inject = ['HomeService', '$location'];
    constructor(private api: any, private location: ng.ILocationService) {

        this.getPopularCards().$promise.then((components: any) => {
            this.popularComponents = components.$embedded('components');
        });

        this.getRecentCards().$promise.then((components: any) => {
            this.recentComponents = components.$embedded('components');
        });

    }

    public getPopularCards(): IResource {
        return this.api('home').enter('popular').get();
    }

    public getRecentCards(): IResource {
        return this.api('home').enter('recent').get();
    }

    public view(card: Card): void {
        this.location.path('hub/component/' + card.id);
    }
}

angular
    .module(module.angularModules)
    .controller('HomeController', HomeController);