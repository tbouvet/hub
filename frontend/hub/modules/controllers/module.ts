/// <amd-dependency path="{w20-core}/modules/hypermedia"/>
/// <amd-dependency path="{hub}/modules/model/components"/>
import angular = require("{angular}/angular");
angular.module('Hub.controllers', ['w20CoreHypermedia']);
export = { angularModules: 'Hub.controllers' }