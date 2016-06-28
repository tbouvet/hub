import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;
import IResourceClass = angular.resource.IResourceClass;

enum Sort {
    DATE = <any> 'DATE',
    STARS = <any> 'STARS',
    NAME = <any> 'NAME',
}

interface ISearchCriterias {
    search: string,
    sort: Sort,
    size: number
}

class SearchController {
    public components:Card[];
    public resultSize: number = 0;

    static $inject = ['HomeService', '$location'];

    constructor(private api:any, private $location:ng.ILocationService) {
        this.components = [];
        this.loadNewCards(this.searchCriterias);
    }

    public searchCriterias:ISearchCriterias = {
        search: '',
        sort: Sort.DATE,
        size: 12
    };

    public onSortChange(): void {
        this.nextPage = undefined;
        this.components = [];
        this.loadNewCards(this.searchCriterias);
    }

    private nextPage:Function;

    public loadNewCards(searchCriterias) {
        searchCriterias.search =  this.$location.search().search || '';
        this.getCards(searchCriterias, results => {
            this.resultSize = results.resultSize;
            if (results.$embedded('components') && results.$embedded('components').constructor === Array) {
                this.components = this.components.concat(results.$embedded('components'));
            }
        });
    }

    private getCards(searchCriterias:ISearchCriterias, successCb?:(results:any) => void, errorCb?:(rejected?:any) => void):void {
        var fetchFunction = this.nextPage ? this.nextPage : this.api('home').enter('components');

        if (fetchFunction.get) {
            fetchFunction.get(searchCriterias).$promise
                .then((results:any) => {
                    this.nextPage = results.$links('next') || angular.noop;
                    if (successCb) {
                        successCb(results);
                    }
                })
                .catch(rejected => {
                    if (errorCb) {
                        errorCb();
                    }
                    throw new Error(rejected);
                });
        }
    }

    public view(card:Card):void {
        this.$location.path('hub/component/' + card.id).search('tab', 'info');
    }
}

angular
    .module(module.angularModules)
    .controller('SearchController', SearchController);