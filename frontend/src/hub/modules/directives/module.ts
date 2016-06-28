/// <amd-dependency path="{hub}/modules/model/components"/>
/// <amd-dependency path="{hub}/modules/directives/directive-factory"/>
/// <amd-dependency path="{hub}/modules/services/github-service"/>
import angular = require("{angular}/angular");
angular.module('Hub.directives', ['ngMaterial', 'Hub.services']);
export = { angularModules: 'Hub.directives' }