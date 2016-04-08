import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;

class UserUpdateController {
    public user:any;

    static $inject = ['HomeService', '$location', 'AuthenticationService'];

    constructor(private api, private $location, private authService) {

        this.getUser().$promise
            .then((user:any) => {
                return this.user = user
            })
            .catch(reason => {
                UserUpdateController.promiseRejected(reason);
            });
    };

    private getUser():IResource {
        return this.api('home').enter('user', { userId: this.authService.subjectPrincipals().userId }).get();
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
    .controller('UserUpdateController', UserUpdateController);