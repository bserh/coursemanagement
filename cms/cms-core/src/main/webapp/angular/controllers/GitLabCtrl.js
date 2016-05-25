'use strict';

var GitLabCtrl = ['$rootScope', '$state', '$scope', '$stateParams', function ($rootScope, $state, $scope) {

    $scope.initialise = function () {

        $scope.go = function (state) {
            $state.go(state);
        };

        $scope.tabData = [
            {
                heading: 'Settings',
                route: 'person.course.gitlab.createrepos'
            },
            {
                heading: 'GitLab users',
                route: 'person.course.gitlab.users'
            }
        ];
    };

    $scope.initialise();
}];

myApp.module('myApp.person').controller('GitLabCtrl', GitLabCtrl);