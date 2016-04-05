define(["require", "exports", '../module', '{angular}/angular', '[text]!{hub}/modules/directives/components/card.html'], function (require, exports, module, angular, cardTemplate) {
    "use strict";
    var HubCard = (function () {
        function HubCard() {
            this.scope = { card: '=', onClick: '&' };
            this.replace = true;
            this.template = cardTemplate;
        }
        return HubCard;
    }());
    angular
        .module(module.angularModules)
        .directive('hubCard', DirectiveFactory.getFactoryFor(HubCard));
});

//# sourceMappingURL=card.js.map
