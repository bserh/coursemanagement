angular.module('myApp.student', ['ui.router'])
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('student', {
                url: '/student',
                //FIXME Сделать страницу профиля для студента
                views: {
                    "": {
                        templateUrl: 'angular/views/home.html'
                    },
                    "content@student": {
                        templateUrl: 'angular/views/studentIndex.html'
                    }
                },
                resolve: {studentAccess: studentAccess}
            })
    }]);

