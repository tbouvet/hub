import module = require('../module');
import angular = require('{angular}/angular');
import IAugmentedJQuery = angular.IAugmentedJQuery;

class ErrSrc implements ng.IDirective {
    static $inject = ['$compile'];
    constructor(private $compile) {};

    link = (scope, element:JQuery, attrs:ng.IAttributes) => {
        element.bind('error', () => {
            if (attrs['component']) {
                var component = JSON.parse(attrs['component']);
                var firstLetter = component.name.split('')[0];
                var div = this.$compile('<div class="component-card-icon">' + firstLetter + '</div>')(scope);
                element.replaceWith(div);
                element = div;
                element.css('background-color', 'rgb(176,190,197)');
            } else {
                if (attrs['src'] != attrs['errSrc']) {
                    attrs.$set('src', attrs['errSrc']);
                }
            }
        });
    }
}

angular
    .module(module.angularModules)
    .directive('errSrc', DirectiveFactory.getFactoryFor<ErrSrc>(ErrSrc));