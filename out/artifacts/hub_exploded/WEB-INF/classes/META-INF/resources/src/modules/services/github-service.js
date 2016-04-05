define(["require", "exports", './module', "{angular}/angular"], function (require, exports, module, angular) {
    "use strict";
    var GITHUB_API = 'https://api.github.com/';
    var GITHUB_API_VERSION = 'application/vnd.github.v3+json';
    var GITHUB_TOKEN = "";
    var GithubSort;
    (function (GithubSort) {
        GithubSort[GithubSort["STARS"] = 'stars'] = "STARS";
        GithubSort[GithubSort["FORKS"] = 'forks'] = "FORKS";
        GithubSort[GithubSort["UPDATED"] = 'updated'] = "UPDATED";
    })(GithubSort || (GithubSort = {}));
    var GithubOrder;
    (function (GithubOrder) {
        GithubOrder[GithubOrder["ASC"] = 'asc'] = "ASC";
        GithubOrder[GithubOrder["DESC"] = 'desc'] = "DESC";
    })(GithubOrder || (GithubOrder = {}));
    var GithubService = (function () {
        function GithubService($q, $httpParamSerializer) {
            this.$q = $q;
            this.$httpParamSerializer = $httpParamSerializer;
            this.createCORSRequest = function (method, url) {
                var xhr = new XMLHttpRequest();
                if ("withCredentials" in xhr) {
                    xhr.open(method, url, true);
                }
                else if (typeof window['XDomainRequest'] != "undefined") {
                    xhr = new window['XDomainRequest']();
                    xhr.open(method, url);
                }
                else {
                    xhr = null;
                }
                return xhr;
            };
        }
        // todo need to authenticate to fix rate limit
        GithubService.prototype.searchUserRepositories = function (user, per_page, sort, order) {
            var _this = this;
            return this.$q(function (resolve, reject) {
                if (user) {
                    var url = GITHUB_API + 'users/' + user + '/repos?' + _this.$httpParamSerializer({ sort: sort, order: order, per_page: per_page || 50 });
                    var xhr = _this.createCORSRequest('GET', url);
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
                }
                else {
                    resolve();
                }
            });
        };
        GithubService.$inject = ['$q', '$httpParamSerializer'];
        return GithubService;
    }());
    exports.GithubService = GithubService;
    angular
        .module(module.angularModules)
        .service('GithubService', GithubService);
});

//# sourceMappingURL=github-service.js.map
