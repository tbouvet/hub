import module = require('./module');
import angular = require("{angular}/angular");

class HomeController {

    public popularComponents: Card[];
    public recentComponents: Card[];

    static $inject = ['HomeService', '$location'];
    constructor(private api: any, private location: ng.ILocationService) {

        api('home').enter('popular').get(components => {
            this.popularComponents = <Card[]> components.$embedded('components');
        });

        api('home').enter('recent').get(components => {
            this.recentComponents = <Card[]> components.$embedded('components');
        })
    }

    public seeDetails(card: Card): void {
        this.location.path('hub/component/' + card.name);
    }

}

angular
    .module(module.angularModules)
    .controller('HomeController', HomeController);


