angular.module('myApp.about', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('about', {
                parent: 'main',
                url: '/about',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/about.html'
                    }
                }
            })
    }]);




