angular.module('myApp.home', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'main',
                url: '/home',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/course.html',
                        controller: 'HomeCtrl'
                    }
                },
                resolve: {
                    courseService: 'CourseService',
                    courses: function (courseService) {
                        return courseService.getNewCourses().then(function (result) {
                            return result;
                        });
                    }
                }
            })
    }])
    .service('CourseService', CourseService)
    .controller('HomeCtrl', HomeCtrl);



