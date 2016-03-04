import module = require('./module');
import angular = require("{angular}/angular");
import IRequestConfig = angular.IRequestConfig;
import IQResolveReject = angular.IQResolveReject;
import IQResolveReject = angular.IQResolveReject;

const GITHUB_API = 'https://api.github.com/search/repositories';
const GITHUB_API_VERSION = 'application/vnd.github.v3+json';

interface SourceService {
    searchRepositories(query:string): ng.IPromise<any>;
}

enum GithubSort {
    STARS = <any> 'stars',
    FORKS = <any> 'forks',
    UPDATED = <any> 'updated'
}

enum GithubOrder {
    ASC = <any> 'asc',
    DESC = <any> 'desc'
}

export class GithubService implements SourceService {
    static $inject = ['$q', '$httpParamSerializer'];
    constructor(private $q:ng.IQService, private $httpParamSerializer) {}

    private createCORSRequest = (method, url):XMLHttpRequest => {
        var xhr = new XMLHttpRequest();
        if ("withCredentials" in xhr) {
            xhr.open(method, url, true);
        } else if (typeof window['XDomainRequest'] != "undefined") {
            xhr = new window['XDomainRequest']();
            xhr.open(method, url);
        } else {
            xhr = null;
        }
        return xhr;
    };

    // todo need to fix rate limit
    public searchRepositories(query:string, sort?:GithubSort, order?:GithubOrder):ng.IPromise<any> {
        return this.$q((resolve, reject) => {
            if (query) {
                var url: string = GITHUB_API + '?' + this.$httpParamSerializer({ q: query, sort: sort, order: order, per_page: 5 });
                var xhr: XMLHttpRequest = this.createCORSRequest('GET', url);
                if (!xhr) {
                    console.info('CORS not supported');
                    resolve();
                }
                xhr.onload = function () {
                    resolve(JSON.parse(xhr.responseText));
                };
                xhr.onerror = function (error) {
                    reject(error);
                };
                xhr.send();
            } else {
                resolve();
            }
        });
    }
}

/* Usage with md-autocomplete

 <md-autocomplete md-input-name="url"
 md-search-text="query"
 md-items="item in $ctrl.githubSearch(query)"
 md-selected-item="$ctrl.repository.url"
 md-search-text-change="$ctrl.githubSearch(query)"
 ng-model-options="{ debounce: 500 }"
 ng-pattern="(?:git|ssh|https?|git@[\w\.]+):(?:\/\/)?[\w\.@:\/~_-]+\.git(?:\/?|\#[\d\w\.\-_]+?)"
 required="true"
 flex
 md-autofocus
 md-floating-label="URL">
 <md-item-template>
 <span>{{ item }}</span>
 </md-item-template>
 <div ng-messages="form.url.$error" ng-if="form.url.$touched">
 <div ng-message="url">The URL does not appear to be valid.</div>
 <div ng-message="required">You did not enter a URL.</div>
 <div ng-message="pattern">The URL does not appear to be a valid .git URL.</div>
 </div>
 </md-autocomplete>


 public githubSearch = (query:string):ng.IPromise => {
 this.repository.url = query;
 return this.githubService.searchRepositories(query)
 .then((results) => {
 return results.items.map(item => {
 return item['clone_url'];
 });
 })
 .catch(reject => {
 throw new Error(reject);
 })
 }
 */

angular
    .module(module.angularModules)
    .service('GithubService', GithubService);