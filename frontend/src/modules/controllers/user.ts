import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

class UserController {
    public userComponents: Component[] = [];
    public userStarredComponents: Component[] = [];

    static $inject = ['HomeService', '$location'];
    constructor (private api, private $location) {
        this.getUserComponents().$promise.then((components: any) => {
            this.userComponents = components.$embedded('components');
        });

        this.getUserStarredComponents().$promise.then((components: any) => {
            this.userStarredComponents = components.$embedded('components');
        });
    };

    public getUserComponents(): IResource {
        return this.api('home').enter('user/components', {}).get();
    }

    public getUserStarredComponents(): IResource {
        return this.api('home').enter('stars', {}).get();
    }

    public view(component: Component): void {
        this.$location.path('hub/component/' + component.id);
    }
}

angular
    .module(module.angularModules)
    .controller('UserController', UserController);