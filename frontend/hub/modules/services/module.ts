/// <amd-dependency path="{w20-core}/modules/hypermedia"/>
/// <amd-dependency path="{angular-material}/angular-material"/>
import angular = require("{angular}/angular");
angular.module('Hub.services', ['w20CoreHypermedia', 'ngMaterial']);
export = { angularModules: 'Hub.services' }