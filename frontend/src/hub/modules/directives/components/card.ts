import module = require('../module');
import angular = require('{angular}/angular');
import cardTemplate = require('[text]!{hub}/modules/directives/components/card.html')

class HubCard implements ng.IDirective {
    scope: any = { card: '=', onClick: '&' };
    replace: boolean = true;
    template: string = cardTemplate;
}

angular
    .module(module.angularModules)
    .directive('hubCard', DirectiveFactory.getFactoryFor<HubCard>(HubCard));

