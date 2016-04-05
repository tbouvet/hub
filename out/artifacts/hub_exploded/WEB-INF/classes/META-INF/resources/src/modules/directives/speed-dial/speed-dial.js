/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
define(["require", "exports", '../module', '{angular}/angular', '[text]!{hub}/modules/directives/speed-dial/speed-dial.html'], function (require, exports, module, angular, speedDialTemplate) {
    "use strict";
    var HubSpeedDial = (function () {
        function HubSpeedDial($mdMedia, $mdDialog, $location) {
            var _this = this;
            this.$mdMedia = $mdMedia;
            this.$mdDialog = $mdDialog;
            this.$location = $location;
            this.template = speedDialTemplate;
            this.link = function (scope) {
                scope.showImportComponent = function (event) {
                    _this.$location.path('/hub/import-component');
                };
            };
        }
        ;
        HubSpeedDial.$inject = ['$mdMedia', '$mdDialog', '$location'];
        return HubSpeedDial;
    }());
    angular
        .module(module.angularModules)
        .directive('hubSpeedDial', DirectiveFactory.getFactoryFor(HubSpeedDial));
});

//# sourceMappingURL=speed-dial.js.map
