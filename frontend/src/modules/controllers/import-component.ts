import module = require('./module');
import angular = require('{angular}/angular');
import _ = require('{lodash}/lodash');
import IResource = angular.resource.IResource;
import {GithubService} from "../services/github-service";
import ILocationService = angular.ILocationService;

enum RepositoryType {
    GIT = <any> 'GIT',
    SVN = <any> 'SVN'
}

interface IRepository {
    sourceType: RepositoryType;
    sourceUrl: string;
}

class ImportComponentController {

    public static promiseRejected(reason):void {
        throw new Error(reason.statusText || reason.data || reason);
    }
}

class QuickImportController {
    public repositoryTypes:RepositoryType[] = [RepositoryType.GIT, RepositoryType.SVN];
    public repository:IRepository = <any>{};
    public importing:boolean = false;
    public failure:boolean = false;
    public success:boolean = false;
    public newComponent:Card;

    static $inject = ['HomeService', '$httpParamSerializer', '$location'];

    constructor(private api, private $httpParamSerializer, private $location:ILocationService) {
        this.repository.sourceType = this.repositoryTypes[0];
        this.repository.sourceUrl = '';
    };

    public confirm(repository:IRepository):void {
        this.importing = true;
        var action = {
            save: {
                method: 'POST',
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            }
        };
        var formParam = this.$httpParamSerializer({
            'sourceUrl': repository.sourceUrl,
            'sourceType': repository.sourceType
        });
        this.api('home').enter('components', {}, action).save({}, formParam)
            .$promise
            .then(newComponent => {
                if (newComponent) {
                    this.success = true;
                    this.newComponent = newComponent;
                }
            })
            .catch(reason => {
                this.failure = true;
                ImportComponentController.promiseRejected(reason);
            });
    };

    public terminate():void {
        this.importing = false;
        this.success = false;
        this.failure = false;
    }

    public view(card:Card):void {
        this.$location.path('hub/component/' + card.id);
    }
}

class GithubImportController {
    public githubSearchQuery:string;
    public searchedGithubComponents:Card[];
    public selectedRepositories:any[];
    public importing:boolean;
    private SEARCH_LIMIT = 100;

    static $inject = [
        'HomeService',
        '$location',
        '$window',
        '$mdToast',
        'GithubService',
        '$httpParamSerializer'
    ];

    constructor(private api:any,
                private $location:ng.ILocationService,
                private $window:ng.IWindowService,
                private $mdToast,
                private githubService:GithubService,
                private $httpParamSerializer) {
        this.githubSearchQuery = '';
        this.searchedGithubComponents = [];
        this.selectedRepositories = [];
        this.importing = false;
    }

    private toast(message:string) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(message)
                .position('top right')
                .hideDelay(3000)
        );
    }

    public searchGithubRepositories(query:string):void {
        this.githubService.searchUserRepositories(query, this.SEARCH_LIMIT)
            .then((results) => {
                this.searchedGithubComponents = results.map((result) => {
                    return { avatar: result.owner['avatar_url'], full_name: result.full_name};
                });
            })
            .catch(ImportComponentController.promiseRejected);
    }

    public importComponents(selectedComponents):void {
        if (!selectedComponents.length) {
            this.toast('You did not select any repositories!');
            return;
        }
        this.importing = true;
        selectedComponents = GithubImportController.formatGithubRepositories(selectedComponents);
        this.api('home').enter('import_list').save({}, selectedComponents).$promise
            .then(result => {
                this.importing = false;
                this.searchedGithubComponents = [];
                this.selectedRepositories = [];
                this.githubSearchQuery = '';
                this.toast('All components have been successfully imported!');
            })
            .catch((reason:any) => {
                this.importing = false;
                this.toast('Oh no ! An error occurred ! We could not import your component');
                ImportComponentController.promiseRejected(reason);
            });
    }

    public view(card:Card) {
        this.$location.path('hub/component/' + card.id);
    }

    private static formatGithubRepositories(results) {
        return results.map((c:any) => {
            return { url: c.full_name, sourceType: 'GITHUB' };
        });
    }
}

class VcsUrlValidator implements ng.IDirective {
    require:string = 'ngModel';
    scope:any = {repository: '='};
    link:ng.IDirectiveLinkFn = (scope:ng.IScope, element:ng.IAugmentedJQuery, attributes:ng.IAttributes, ngModel:ng.INgModelController) => {
        ngModel.$validators['isRepoUrl'] = (modelValue, viewValue) => {
            switch (scope['repository'].sourceType) {
                case RepositoryType.GIT:
                    return /(?:git|ssh|https?|git@[\w\.]+):(?:\/\/)?[\w\.@:\/~_-]+\.git(?:\/?|\#[\d\w\.\-_]+?)$/.test(viewValue);
                default:
                    return true;
            }
        };
        scope.$watch('repository', () => {
            ngModel.$validate();
        }, true);
    };
}

angular
    .module(module.angularModules)
    .controller('ImportComponentController', ImportComponentController)
    .controller('QuickImportController', QuickImportController)
    .controller('GithubImportController', GithubImportController);