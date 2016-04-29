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
    public failedSources:{};
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
                if (newComponent && newComponent['componentCards'][0]) {
                    this.success = true;
                    this.newComponent = newComponent['componentCards'][0];
                }
            })
            .catch(reason => {
                this.failure = true;
                if (reason.data && reason.data.failedSources) {
                    this.failedSources = reason.data.failedSources;
                }
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
    public searching:boolean;
    public importing:boolean;
    public importComplete:boolean;
    public errorMessage:string;
    public failedSources:{};
    public importedComponents:any[];
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
        this.importComplete = false;
    }

    private toast(message:string) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(message)
                .position('top right')
                .hideDelay(3000)
        );
    }

    public setUserToken (token: string) {
        if (token) {
            this.githubService.setUserToken(token);
        }
    }

    public searchGithubRepositories(query:string):void {
        this.searching = true;
        this.importComplete = false;
        this.errorMessage = '';
        this.githubService.searchUserRepositories(query, this.SEARCH_LIMIT)
            .then((results) => {
                if (results.length) {
                    this.searchedGithubComponents = results.map((result) => {
                        return {avatar: result.owner['avatar_url'], full_name: result.full_name};
                    });
                } else if (results.message) {
                    if (results.message.indexOf('API rate limit') !== -1) {
                        this.errorMessage = 'Github API rate limit reached. Please use authenticated requests';
                    }
                }
            })
            .catch(ImportComponentController.promiseRejected)
            .finally(() => {
                this.searching = false;
            });
    }

    public toggleAll():void {
        this.selectedRepositories = this.selectedRepositories.length ? [] : this.searchedGithubComponents;
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
                if (Object.getOwnPropertyNames(result.failedSources).length) {
                    this.failedSources = result.failedSources;
                }
                if (result['componentCards'].length) {
                    this.importedComponents = result['componentCards'];
                }
                this.toast('All components have been successfully imported!');
            })
            .catch((reason:any) => {
                this.importing = false;
                this.toast('Oh no ! An error occurred ! We could not import your components');
                ImportComponentController.promiseRejected(reason);
            })
            .finally(() => {
                this.importComplete = true;
            });
    }

    public view(card:Card) {
        this.$location.path('hub/component/' + card.id);
    }

    private static formatGithubRepositories(results) {
        return results.map((c:any) => {
            return {url: c.full_name, sourceType: 'GITHUB'};
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