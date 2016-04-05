define(["require", "exports", './module', "{angular}/angular"], function (require, exports, module, angular) {
    "use strict";
    var SubView;
    (function (SubView) {
        SubView[SubView["IDENTITY"] = 'identity'] = "IDENTITY";
    })(SubView || (SubView = {}));
    function displayDocName() {
        return function (input) {
            return input.split('/').pop();
        };
    }
    var ComponentDetailsController = (function () {
        function ComponentDetailsController(api, $routeParams, $http, $mdToast, $mdDialog, $mdMedia) {
            var _this = this;
            this.api = api;
            this.$routeParams = $routeParams;
            this.$http = $http;
            this.$mdToast = $mdToast;
            this.$mdDialog = $mdDialog;
            this.$mdMedia = $mdMedia;
            this.detailsView = SubView.IDENTITY;
            this.getComponent().$promise.then(function (component) {
                _this.component = component;
            });
        }
        ComponentDetailsController.prototype.getComponent = function () {
            return this.api('home').enter('component', { componentId: this.$routeParams.id }).get();
        };
        ComponentDetailsController.prototype.rateComponent = function (component) {
            if (component.isStarred) {
                this.unStarComponent(component);
            }
            else {
                this.starComponent(component);
            }
        };
        ComponentDetailsController.prototype.starComponent = function (component) {
            component.$links('star').save(function () {
                component.isStarred = true;
                component.stars++;
            }, function (reject) {
                throw new Error(reject);
            });
        };
        ComponentDetailsController.prototype.unStarComponent = function (component) {
            component.$links('star').delete(function () {
                component.isStarred = false;
                if (component.stars > 0) {
                    component.stars--;
                }
            }, function (reject) {
                throw new Error(reject);
            });
        };
        ComponentDetailsController.prototype.toast = function (message) {
            this.$mdToast.show(this.$mdToast.simple()
                .textContent(message)
                .position('top right')
                .hideDelay(3000));
        };
        ComponentDetailsController.prototype.syncComponent = function (component) {
            var _this = this;
            this.$mdDialog.show({
                templateUrl: require.toUrl('{hub}/views/templates/syncing.tmpl.html'),
                parent: angular.element(document.body),
                clickOutsideToClose: false,
                fullscreen: this.$mdMedia('sm') || this.$mdMedia('xs')
            });
            component.$links('self', {}, { sync: { method: 'PUT' } })
                .sync()
                .$promise
                .then(function () {
                _this.getComponent()
                    .$promise
                    .then(function (component) {
                    _this.component = component;
                    _this.$mdDialog.hide();
                    _this.toast(component.name + ' has been synced!');
                })
                    .catch(ComponentDetailsController.promiseRejected);
            })
                .catch(ComponentDetailsController.promiseRejected);
        };
        ComponentDetailsController.prototype.archiveComponent = function (component) {
            var _this = this;
            component.$links('state', {}, { archive: { method: 'PUT' } })
                .archive('ARCHIVED')
                .$promise
                .then(function () {
                _this.component.state = 'ARCHIVED';
                _this.toast(component.name + ' has been archived!');
            })
                .catch(ComponentDetailsController.promiseRejected);
        };
        ComponentDetailsController.prototype.restoreComponent = function (component) {
            var _this = this;
            component.$links('state', {}, { restore: { method: 'PUT' } })
                .restore('PUBLISHED')
                .$promise
                .then(function () {
                _this.component.state = 'PUBLISHED';
                _this.toast(component.name + ' has been restored!');
            })
                .catch(ComponentDetailsController.promiseRejected);
        };
        ComponentDetailsController.prototype.setWikiPage = function (wikiName) {
            this.activeWikiName = wikiName;
            this.activeWikiURL = '/components/generator-w20/wiki/' + wikiName;
        };
        ;
        ComponentDetailsController.promiseRejected = function (reason) {
            throw new Error(reason.statusText || reason.data || reason);
        };
        ComponentDetailsController.$inject = ['HomeService', '$routeParams', '$http', '$mdToast', '$mdDialog', '$mdMedia'];
        return ComponentDetailsController;
    }());
    angular
        .module(module.angularModules)
        .filter('displayDocName', displayDocName)
        .controller('ComponentDetailsController', ComponentDetailsController);
});

//# sourceMappingURL=component-details.js.map
