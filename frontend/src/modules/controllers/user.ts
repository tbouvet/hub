import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

function filterPendingIfNotOwner() {
    return function (components, userIsOwner) {
        return userIsOwner ? components : components.filter(c => c.state !== 'PENDING');
    };
}

class UserController {
    public userComponents:Component[] = [];
    public favoriteComponents:Component[] = [];
    public userComponentsCount:number;
    public favoriteComponentsCount:number;
    public user:any;
    public userIsOwner: boolean;

    static $inject = ['HomeService', '$location', '$routeParams', 'AuthenticationService'];

    constructor(private api, private $location, private $routeParams, private authService) {

        this.getUser().$promise
            .then((user:any) => {
                return this.user = user
            })
            .then(user => {
                this.userIsOwner = this.authService.subjectPrincipals().userId === this.user.name;

                UserController.getUserComponents(user).$promise
                    .then((components:any) => {
                        this.userComponents = components.$embedded('components');
                        this.userComponentsCount = components.resultSize;
                    })
                    .catch(reason => {
                        UserController.promiseRejected(reason);
                    });

                UserController.getUserStarredComponents(user).$promise
                    .then((components:any) => {
                        this.favoriteComponents = components.$embedded('components');
                        this.favoriteComponentsCount = components.resultSize;
                    })
                    .catch(reason => {
                        UserController.promiseRejected(reason);
                    });

                UserController.getUserIcon(user).$promise
                    .then((icon: any) => {
                        var icon = icon;
                    })
                    .catch(reason => {
                        UserController.promiseRejected(reason);
                    });
            })
            .catch(reason => {
                UserController.promiseRejected(reason);
            });
    };

    private getUser():IResource {
        return this.api('home').enter('user', {userId: this.$routeParams.id}).get();
    }

    private static getUserComponents(user):IResource {
        return user.$links('users/components').get();
    }

    public static getUserStarredComponents(user):IResource {
        return user.$links('users/stars').get();
    }

    public static getUserIcon(user):IResource {
        return user.$links('users/icon').get();
    }

    public view(component:Component):void {
        this.$location.path('hub/component/' + component.id);
    }

    public static promiseRejected(reason):void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

angular
    .module(module.angularModules)
    .filter('filterPendingIfNotOwner', filterPendingIfNotOwner)
    .controller('UserController', UserController);