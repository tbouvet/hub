/// <amd-dependency path="{w20-core}/modules/hypermedia"/>
/// <amd-dependency path="{hub}/modules/model/components"/>
/// <amd-dependency path="{hub}/modules/directives/scrolling/infinite-scroll"/>
/// <amd-dependency path="{hub}/modules/directives/utils/error"/>
/// <amd-dependency path="{hub}/modules/directives/navigation/history-back"/>
/// <amd-dependency path="{angular-messages}/angular-messages"/>
/// <amd-dependency path="{angular-material}/angular-material"/>
/// <amd-dependency path="{ng-file-upload}/ng-file-upload.min"/>
/// <amd-dependency path="{ng-img-crop}/ng-img-crop"/>
/// <amd-dependency path="[css]!{ng-img-crop}/ng-img-crop.css"/>
import angular = require("{angular}/angular");
angular.module('Hub.controllers', ['w20CoreHypermedia', 'ngMaterial', 'ngMessages', 'ngFileUpload', 'ngImgCrop']);
export = { angularModules: 'Hub.controllers' }