import module = require('./module');
import angular = require("{angular}/angular");
import IResource = angular.resource.IResource;
import IResourceClass = angular.resource.IResourceClass;
import IResourceClass = angular.resource.IResourceClass;
import ISearchCriteria from "./";
import ISearchCriterias from "./";

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
        this.getComponents(this.searchCriterias, results => {
            if (results.$embedded('components')) {
                this.components = results.$embedded('components');
            }
        });
    }

    public components:Card[];

    public searchCriterias:ISearchCriterias = {
        search: this.$location.search().search || '',
        sort: Sort.DATE,
        pageIndex: 1,
        pageSize: 9
    };

    public getComponents (searchCriterias: ISearchCriterias, callback: (results: any) => void):void {
        this.api('home').enter('components', searchCriterias).get().$promise
            .then((results:any) => {
                callback(results);
            })
            .catch(error => {
                throw new Error(error);
            });
    }

    public view(card:Card):void {
        this.$location.path('hub/component/' + card.name);
    }
}

angular
    .module(module.angularModules)
    .controller('SearchController', SearchController);