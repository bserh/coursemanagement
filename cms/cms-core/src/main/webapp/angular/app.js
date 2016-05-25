// Declare app level module which depends on views, and components
var myApp = angular.module('myApp', [
    'ui.router',
    'ui.router.tabs',
    'restangular',
    'angular-google-gapi',
    'ui.bootstrap',
    'angularFileUpload',
    'angular-redactor',
    'LocalStorageModule',
    'myApp.main',
    'myApp.home',
    'myApp.person',
    'myApp.news',
    'myApp.about'
]);

myApp.service('sessionService', ['$window', '$rootScope', SessionService]);
myApp.service('sessionService', ['PersonPersistenceService', SessionService]);
myApp.service('PersonService', ['Restangular', PersonService]);
myApp.service('AuthService', ['PersonService', 'Restangular', AuthService]);
myApp.service('PersonPersistenceService', ['localStorageService', PersonPersistenceService]);
myApp.factory('sessionInjector', ['$rootScope', 'sessionService', SessionInjector]);

myApp.config(function (RestangularProvider) {
    //Изменяем базовый Url для REST
    RestangularProvider.setBaseUrl('http://localhost:8080/cms-core-1.0/');
});
myApp.config(function (localStorageServiceProvider) {
    localStorageServiceProvider
        .setPrefix('cms')
        .setStorageType('localStorage')
        .setNotify(true, true);
});
myApp.config(['$stateProvider', '$urlRouterProvider', '$httpProvider',
    function ($stateProvider, $urlRouterProvider, $httpProvider) {
        $urlRouterProvider.otherwise('/home');
        $httpProvider.interceptors.push('sessionInjector');
    }]);

myApp.run(['GAuth', 'GApi', 'GData', '$state', '$rootScope', '$window', 'AuthService', 'PersonPersistenceService',
    function (GAuth, GApi, GData, $state, $rootScope, $window, AuthService, PersonPersistenceService) {

        // ----------------  Google API lib initialization -------------------------
        var CLIENT = '895405022160-pi238d0pi57fsmsov8khtpr4415hj5j5.apps.googleusercontent.com';
        GAuth.setClient(CLIENT);
        GAuth.setScope('https://www.googleapis.com/auth/userinfo.email');

        // ---------------------  Login / Logout  -------------------------
        $rootScope.doLogin = function () {
            GAuth.login().then(function () {
                AuthService.goAuth(GData.getUser()).then(function (data) {
                    PersonPersistenceService.saveInfo(
                        data.id,
                        data.personRole.toLowerCase(),
                        data.name + " " + data.lastName,
                        data.email.toLowerCase(),
                        $window.gapi.auth.getToken().access_token);
                    AuthService.getEmailHash($rootScope.getEmail()).then(function (hash) {
                        PersonPersistenceService.saveHash(hash.hash);
                    });
                    $state.go("person");
                });
            }, function () {
                $state.go("home");
                console.log('login fail');
            });
        };
        $rootScope.doLogOut = function () {
            PersonPersistenceService.clearInfo();
            GAuth.logout().then(function () {
                $state.go("home");
            });
        };
        $rootScope.isLogin = function () {
            return !(($rootScope.getUserId() == undefined) || ($rootScope.getUserId() == null));
        };

        // --------------------  Main person info -------------------------
        $rootScope.getEmail = function () {
            return PersonPersistenceService.getEmail();
        };
        $rootScope.getUserId = function () {
            return PersonPersistenceService.getId();
        };
        $rootScope.getUsername = function () {
            return PersonPersistenceService.getName();
        };
        $rootScope.isTeacher = function () {
            return PersonPersistenceService.isTeacher();
        };
        $rootScope.getEmailHash = function () {
            return PersonPersistenceService.getEmailHash();
        };
        // ------------------  Broadcast receivers -------------------------
        $rootScope.$on('app.unauthorized', function () {
            $rootScope.doLogOut();
            $state.go("home");
            console.log('attempt to get secure data');
        });
        $rootScope.$on('$stateChangeStart', function (event, toState) {
            if ((toState.name == 'home') && $rootScope.isLogin()) {
                event.preventDefault();
                $state.go('person');
            }
        });
    }]);


