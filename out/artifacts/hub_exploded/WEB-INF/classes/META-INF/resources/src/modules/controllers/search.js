define(["require", "exports", './module', "{angular}/angular"], function (require, exports, module, angular) {
    "use strict";
    var Sort;
    (function (Sort) {
        Sort[Sort["DATE"] = 'date'] = "DATE";
        Sort[Sort["POPULARITY"] = 'popularity'] = "POPULARITY";
    })(Sort || (Sort = {}));
    var SearchController = (function () {
        function SearchController(api, $location) {
            this.api = api;
            this.$location = $location;
            this.searchCriterias = {
                search: '',
                sort: Sort.DATE,
                size: 12
            };
            this.components = [];
            this.loadNewCards(this.searchCriterias);
        }
        SearchController.prototype.loadNewCards = function (searchCriterias) {
            var _this = this;
            searchCriterias.search = this.$location.search().search || '';
            this.getCards(searchCriterias, function (results) {
                _this.resultSize = results.resultSize;
                if (results.$embedded('components') && results.$embedded('components').constructor === Array) {
                    _this.components = _this.components.concat(results.$embedded('components'));
                }
            });
        };
        SearchController.prototype.getCards = function (searchCriterias, successCb, errorCb) {
            var _this = this;
            var fetchFunction = this.nextPage ? this.nextPage : this.api('home').enter('components');
            if (fetchFunction.get) {
                fetchFunction.get(searchCriterias).$promise
                    .then(function (results) {
                    _this.noResults = false;
                    _this.nextPage = results.$links('next') || angular.noop;
                    if (successCb) {
                        successCb(results);
                    }
                })
                    .catch(function (rejected) {
                    if (errorCb) {
                        errorCb();
                    }
                    _this.noResults = true;
                    throw new Error(rejected);
                });
            }
        };
        SearchController.prototype.view = function (card) {
            this.$location.path('hub/component/' + card.id);
        };
        SearchController.$inject = ['HomeService', '$location'];
        return SearchController;
    }());
    angular
        .module(module.angularModules)
        .controller('SearchController', SearchController);
});

//# sourceMappingURL=search.js.map
