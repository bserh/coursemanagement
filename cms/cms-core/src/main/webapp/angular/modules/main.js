/**
 * Created by olga on 01.07.15.
 */
angular.module('myApp.main', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('main', {
                abstract: true,
                views: {
                    "@": {
                        templateUrl: 'angular/views/mainTemplate.html'
                    },
                    "header@main": {
                        templateUrl: 'angular/views/navbar-main.html'
                    }
                }
            });
    }]);