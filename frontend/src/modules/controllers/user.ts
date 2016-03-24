import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

class UserController {
    public userComponents: Component[] = [];
    public favoriteComponents: Component[] = [];
    public userPrincipals: { userId: string };

    static $inject = ['HomeService', '$location', 'AuthenticationService', 'EventService'];
    constructor (private api, private $location, private authService, private eventService) {
        this.userPrincipals = this.authService.subjectPrincipals();

        this.getUserComponents().$promise.then((components: any) => {
            this.userComponents = components.$embedded('components');
        });

        this.getUserStarredComponents().$promise.then((components: any) => {
            this.favoriteComponents = components.$embedded('components');
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