import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;
import IResourceClass = angular.resource.IResourceClass;

enum Sort {
    DATE = <any> 'date',
    POPULARITY = <any> 'popularity',
}

interface ISearchCriterias {
    search: string,
    sort: Sort,
    pageIndex: number,
    pageSize: number
}

class SearchController {
    static $inject = ['HomeService', '$location'];
    constructor(private api:any, private $location:ng.ILocationService) {
        this.getComponents(
            this.searchCriterias,
            results => {
                if (results.$embedded('components')) {
                    this.components = results.$embedded('components');
                }
            },
            () => {
                this.components = [];
            });
    }

    public components:Card[];

    public searchCriterias:ISearchCriterias = {
        search: this.$location.search().search || '',
        sort: Sort.DATE,
        pageIndex: 1,
        pageSize: 9
    };

    public getComponents(searchCriterias:ISearchCriterias, successCb:(results:any) => void, errorCb:(rejected?:any) => void):void {
        this.api('home').enter('components', searchCriterias).get().$promise
            .then((results:any) => {
                successCb(results);
            })
            .catch(rejected => {
                errorCb();
                throw new Error(rejected);
            });
    }

    public view(card:Card):void {
        this.$location.path('hub/component/' + card.name);
    }
}

angular
    .module(module.angularModules)
    .controller('SearchController', SearchController);