// Declare app level module which depends on views, and components
var myApp = angular.module('myApp', [
    'ui.router',
    'restangular',
    'angular-google-gapi',
    'ui.bootstrap',
    'angularFileUpload',
    'LocalStorageModule',
    'myApp.main',
    'myApp.home',
    'myApp.person',
    'myApp.news',
    'myApp.about'
]);
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



function AddFileCtrl($scope, FileUploader) {
    var uploader = $scope.uploader = new FileUploader({
        url: 'resources/file/upload'
    });

    // filters
    uploader.filters.push({
        name: 'customFilter',
        fn: function (item /*{File|FileLikeObject}*/, options) {
            return this.queue.length < 10;
        }
    });
    // callbacks
    uploader.onSuccessItem = function (fileItem, response, status, headers) {
        console.info('onSuccessItem', fileItem, response, status, headers);
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        console.info('onErrorItem', fileItem, response, status, headers);
    };
    uploader.onCompleteItem = function (fileItem, response, status, headers) {
        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function () {
        console.info('onCompleteAll');
    };

    console.info('uploader', uploader);

}
function AddLectureCtrl($scope, $stateParams, $state, $modal, CourseContentService, FileUploader, mode, lecture) {
    var BASE_URL_FILE_TO_STORAGE = 'resources/file/upload';
    var isEditMode = (mode == 'edit');

    $scope.lecture = isEditMode ? lecture : {};
    $scope.okBtnLabel = isEditMode ? 'Изменить лекцию' : 'Добавить лекцию';
    var removedLinks = [];
    var removedPractices = [];

    $scope.isLectureValid = function (lectureForm) {
        return !lectureForm.$invalid && areLinksValid() && arePracticesValid();
    };

    var areLinksValid = function () {
        return areOrderNumsValid($scope.lecture.links);
    };

    var arePracticesValid = function () {
        return areOrderNumsValid($scope.lecture.practices);
    };

    var areOrderNumsValid = function (ar) {
        var valid = true;
        for (var i = 0; i < ar.length; i++) {
            if (ar[i].orderNum == undefined) {
                valid = false;
                break;
            }
        }
        if (valid) {
            for (var i = 0; i < ar.length; i++) {
                for (var j = i + 1; j < ar.length; j++) {
                    if (ar[i].orderNum == ar[j].orderNum) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    break;
                }
            }
        }
        return valid;
    };

    var createLecture = function () {
        $scope.lecture.createDate = new Date();
        $scope.lecture.orderNum = $stateParams.lectureOrderNum;
        $scope.lecture.courseId = $stateParams.courseId;
        return CourseContentService.createLecture($scope.lecture);
    };

    var updateLecture = function () {
        return CourseContentService.updateLecture($scope.lecture, removedLinks, removedPractices);
    };

    $scope.alertData = {
        boldTextTitle: "Ошибка",
        mode: 'danger'
    };

    $scope.ok = function () {
        var promise = isEditMode ? updateLecture() : createLecture();
        promise.then(
            function (successResult) {
                if (successResult.responseStatus == 200 || successResult.responseStatus == 201) {
                    $state.go('person.course.content', {}, {reload: true});
                } else {
                    $scope.alertData.textAlert = successResult;
                    $scope.showAlertWithError();
                }
            },
            function (fail) {
                $scope.alertData.textAlert = fail;
            }
        );
    };

    $scope.cancel = function () {
        $state.go('person.course.content');
    };

    $scope.showAlertWithError = function () {

        var modalInstance = $modal.open(
            {
                templateUrl: 'angular/templates/alertModal.html',
                controller: function ($scope, $modalInstance, data) {
                    $scope.data = data;
                    $scope.close = function () {
                        $modalInstance.close(data);
                    }
                },
                backdrop: true,
                keyboard: true,
                backdropClick: true,
                size: 'lg',
                resolve: {
                    data: function () {
                        return $scope.alertData;
                    }
                }
            });
    };

    // file uploading
    var uploader = $scope.uploader = new FileUploader({
        url: BASE_URL_FILE_TO_STORAGE
    });
    // filters
    uploader.filters.push({
        name: 'customFilter',
        fn: function (item /*{File|FileLikeObject}*/, options) {
            return this.queue.length < 10;
        }
    });
    // callbacks
    uploader.onBeforeUploadItem = function (item) {
        item.isUploaded = true;
        console.info('onBeforeUploadItem', item);
    };
    uploader.onSuccessItem = function (fileItem, response, status, headers) {
        console.info('onSuccessItem', fileItem, response, status, headers);
        fileItem.isUploaded = false;
        response.orderNum = $scope.lecture.links.length + 1;
        $scope.lecture.links.push(response);
        $scope.newLink.orderNum = $scope.newLink.orderNum + 1;
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        fileItem.isUploaded = false;
        console.info('onErrorItem', fileItem, response, status, headers);
    };
    uploader.onCompleteItem = function (fileItem, response, status, headers) {
        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function () {
        console.info('onCompleteAll');
    };

    // lecture links operations
    $scope.lecture.links = $scope.lecture.links || [];
    $scope.newLink = $scope.newLink || {'orderNum': $scope.lecture.links.length + 1};
    $scope.addLink = function () {
        $scope.lecture.links.push(angular.copy($scope.newLink));
        $scope.newLink.orderNum = $scope.lecture.links.length + 1;
        $scope.newLink.description = '';
        $scope.newLink.link = '';
    };
    $scope.removeLink = function (linkToRemove) {
        for (var i = 0; i < $scope.lecture.links.length; i++) {
            if (angular.equals($scope.lecture.links[i], linkToRemove)) {
                $scope.lecture.links.splice(i, 1);
                for (var j = i; j < $scope.lecture.links.length; j++) {
                    $scope.lecture.links[j].orderNum = $scope.lecture.links[j].orderNum - 1;
                }
                $scope.newLink.orderNum = $scope.newLink.orderNum - 1;
                break;
            }
        }
        removedLinks.push(linkToRemove.id);
    };

    // practice tasks operations
    $scope.lecture.practices = $scope.lecture.practices || [];
    $scope.newPractice = $scope.newPractice || {'orderNum': $scope.lecture.practices.length + 1};
    $scope.addPractice = function () {
        $scope.lecture.practices.push(angular.copy($scope.newPractice));
        $scope.newPractice.orderNum = $scope.lecture.practices.length + 1;
        $scope.newPractice.task = '';
    };
    $scope.removePractice = function (practiceToRemove) {
        for (var i = 0; i < $scope.lecture.practices.length; i++) {
            if (angular.equals($scope.lecture.practices[i], practiceToRemove)) {
                $scope.lecture.practices.splice(i, 1);
                for (var j = i; j < $scope.lecture.practices.length; j++) {
                    $scope.lecture.practices[j].orderNum = $scope.lecture.practices[j].orderNum - 1;
                }
                $scope.newPractice.orderNum = $scope.newPractice.orderNum - 1;
            }
        }
        removedPractices.push(practiceToRemove.id);
    };
}
function AddNewsCtrl($scope, $stateParams, NewsService) {
    $scope.news = {};

    $scope.createNews = function () {
        $scope.news.date = new Date();
        console.log("controller: create news");

        NewsService.createNews($scope.news, $stateParams.courseId);
    };
}

function AddOrEditCourseCtrl($rootScope, $scope, $state, $modal, CourseService, allTeachers, coursePrototypes, mode, course) {

    $scope.isAddMode = function () {
        return mode == 'add';
    };
    // ----------------- initialization -----------------
    $scope.course = $scope.isAddMode() ? {} : course;
    $scope.courseTeachers = [parseInt($rootScope.getUserId())];
    $scope.teachers = allTeachers;
    $scope.coursePrototypes = coursePrototypes;
    $scope.coursePrototypeId = -1;
    $scope.okLabel = $scope.isAddMode() ? 'Добавить курс' : 'Обновить курс';

    // -----------------   validation   -----------------
    $scope.isValidDates = function () {
        if (!$scope.course.startDate && !$scope.course.endDate) {
            return true;
        }
        if ($scope.course.startDate && !$scope.course.endDate) {
            return true;
        }
        return $scope.course.startDate <= $scope.course.endDate;
    };
    $scope.isValidTeachers = function () {
        return $scope.isAddMode() ? ($scope.courseTeachers.length > 0) : true;
    };
    $scope.areValidFields = function () {
        return ($scope.course.name != undefined && $scope.course.name != '') &&
            ($scope.course.description != undefined && $scope.course.description != '')
    };
    $scope.isValidCourse = function () {
        return $scope.areValidFields() &&
            $scope.isValidTeachers() &&
            $scope.isValidDates();
    };

    // --------------- course prototyping -----------------
    var getFieldFromPrototype = function (field) {
        if ($scope.coursePrototypeId == -1) {
            return '';
        }
        for (var i = 0; i < $scope.coursePrototypes.length; i++) {
            if ($scope.coursePrototypes[i].id == $scope.coursePrototypeId) {
                switch (field) {
                    case 'name':
                        return $scope.coursePrototypes[i].name;
                    case 'description':
                        return $scope.coursePrototypes[i].description;
                }
            }
        }
        return '';
    };
    $scope.setTitleAsPrototype = function () {
        $scope.course.name = getFieldFromPrototype('name');
    };
    $scope.setDescriptionAsPrototype = function () {
        $scope.course.description = getFieldFromPrototype('description');
    };
    $scope.setFieldsAsInPrototype = function () {
        $scope.setTitleAsPrototype();
        $scope.setDescriptionAsPrototype();
    };

    // ------------  creating and updating -----------------
    var fillTeachers = function () {
        $scope.course.teachers = [];
        for (var i = 0; i < $scope.courseTeachers.length; i++) {
            $scope.course.teachers[i] = {'id': $scope.courseTeachers[i]};
        }
    };
    var alertData = {
        boldTextTitle: "Ошибка",
        mode: 'danger'
    };

    $scope.createCourse = function () {
        fillTeachers();
        CourseService.createCourse($scope.course, $scope.coursePrototypeId)
            .then(
            function (createdCourse) {
                if (CourseService.isCourseSuccessfullyCreated(createdCourse)) {
                    $state.go('person', {}, {reload: true});
                } else {
                    alertData.textAlert = createdCourse;
                    showAlertWithError(alertData);
                }
            },
            function () {
                alertData.textAlert = 'Причина неизвестна';
                showAlertWithError(alertData);
            })
            .finally(function () {
                $scope.course.teachers = [];
            });
    };

    $scope.updateCourse = function () {
        CourseService.updateCourse($scope.course).then(
            function (updatedCourse) {
                if (updatedCourse.responseStatus / 100 == 2) {
                    $state.go('person.course.content', {'courseId': $scope.course.id}, {reload: true});
                } else {
                    alertData.textAlert = updatedCourse;
                    showAlertWithError(alertData);
                }
            },
            function () {
                alertData.textAlert = 'Причина неизвестна';
                showAlertWithError(alertData);
            }
        )
    };

    $scope.cancel = function () {
        console.log("course: " + JSON.stringify($scope.course));
        $scope.isAddMode() ? $state.go('home') : $state.go('person.course.content', {'courseId': $scope.course.id}, {reload: true});
    };

    var showAlertWithError = function (alertData) {
        var modalInstance = $modal.open(
            {
                templateUrl: 'angular/templates/alertModal.html',
                controller: function ($scope, $modalInstance) {
                    $scope.data = alertData;
                    $scope.close = function () {
                        $modalInstance.close();
                    }
                },
                backdrop: true,
                keyboard: true,
                backdropClick: true,
                size: 'lg'
            }
        );
    };

}

/**
 * Created by olga on 25.06.15.
 */
function CourseContentCtrl($scope, lectures, course, CourseContentService) {
    $scope.lectures = lectures;
    $scope.hidePractices = false;
    $scope.editMode = false;

    $scope.removeLecture = function (lectureOrderNum) {
        CourseContentService.removeLecture(course.id, lectureOrderNum).then(function (success) {
            if (success.responseStatus == 200) {
                CourseContentService.getLectures(course.id).then(function (lectures) {
                   if(lectures.responseStatus == 200) {
                       $scope.lectures = lectures;
                   }
                });
            }
        });
    };
}
/**
 * Created by olga on 24.06.15.
 */
function DatepickerCtrl($scope) {
    $scope.today = function () {
        $scope.dt = new Date();
    };
    $scope.today();

    $scope.clear = function () {
        $scope.dt = null;
    };

    $scope.disabled = function (date, mode) {
        return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
    };

    $scope.toggleMin = function () {
        $scope.minDate = ( $scope.minDate ) ? null : new Date();
    };
    $scope.toggleMin();

    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.opened = true;
    };

    $scope.dateOptions = {
        'year-format': "'yy'",
        'starting-day': 1
    };

    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'shortDate'];
    $scope.format = $scope.formats[0];
}

function HomeCtrl($scope, courses) {
    $scope.courses = courses;
}

/**
 * Created by olga on 08.07.15.
 */
function LectureContentCtrl($scope, lecture, lecturesCnt) {
    $scope.lecture = lecture;
    $scope.enablePreview = ($scope.lecture.orderNum != 1);
    $scope.enableNext = (lecturesCnt != lecture.orderNum);
}
function NewsCtrl($scope, news) {
    for (var i = 0; i < news.length; i++) {
        var timestamp = news[i].date;
        news[i].date = timestampConvector(timestamp);
    }
        $scope.news = news;
}

var timestampConvector = function (timestamp) {
    var newsCreateDate = new Date(timestamp);
    var currentDate = new Date();
    var formattedDate;
    if (currentDate.getDate() == newsCreateDate.getDate()) {
        formattedDate = "сегодня";
    } else if (currentDate.getDate() == newsCreateDate.getDate() + 1) {
        formattedDate = "вчера";
    } else {
        var date = ((newsCreateDate.getDate()) < 10) ? "0" + newsCreateDate.getDate() : newsCreateDate.getDate();
        var month = ((newsCreateDate.getMonth() + 1) < 10) ? "0" + (newsCreateDate.getMonth() + 1) : (newsCreateDate.getMonth() + 1);
        formattedDate = date + "-" + month + "-" + newsCreateDate.getFullYear();
   }

    var hours = (newsCreateDate.getHours() < 10) ? "0" + newsCreateDate.getHours() : newsCreateDate.getHours();
    var minutes = (newsCreateDate.getMinutes() < 10) ? "0" + newsCreateDate.getMinutes() : newsCreateDate.getMinutes();
    var formattedTime = hours + ":" + minutes;
    return formattedDate + " в " + formattedTime;

}
function NoNewsCtrl($scope, news) {

        $scope.news = news;
}

function NotificationCtrl(Restangular, $scope){
    var restBase = '';//FIXME write ws
    var Notification = Restangular.all(restBase);

    $scope.mailModel = {
        sms: true,
        email: false
    };

    //$scope.onClickChangeFlagsInSms = function () {
    //    if(mailModel.sms) mailModel.email = false;
    //    if(!mailModel.sms) mailModel.email = true;
    //};
    //
    //$scope.onClickChangeFlagsInEmail = function () {
    //    if(mailModel.email) mailModel.sms = false;
    //    if(!mailModel.email) mailModel.sms = true;
    //};

    $scope.doNotifying = function(){
        //if($scope.mailModel.email){
        //    Notification.post({
        //        mailtype: "MAIL",
        //        message: $scope.emailMessage,
        //        subject: $scope.subject
        //    })
        //}
        //if($scope.mailModel.sms){
        //    Notification.post({
        //        mailtype: "SMS",
        //        message: $scope.smsMessage
        //    })
        //}
    };
}
function PersonCtrl($state, $scope, $modal, PersonPersistenceService, courseService, personService, coursesGroups, oldCourses) {

    $scope.personCourses = coursesGroups.coursesEnrolled;
    $scope.newCourses = coursesGroups.coursesToSubscribe;
    $scope.oldCourses = oldCourses;

    $scope.getActionMsg = function (status, teacherRole) {
        var msg = 'Подписаться';
        if (teacherRole) {
            msg = 'Редактировать';
        } else {
            switch (status) {
                case "REQUESTED":
                    msg = 'Перейти к курсу';
                    break;
                case "SIGNED":
                    msg = 'Перейти к курсу';
                    break;
                case "UNSIGNED":
                    msg = 'Подписаться';
                    break;
            }
        }
        return msg;
    };

    $scope.removeCourse = function (courseId) {
        courseService.removeCourse(courseId).then(
            function (success) {
                if (success.responseStatus / 100 != 2) {
                    alertData.textAlert = success;
                    showAlertWithError(alertData);
                } else {
                    removeCourseFromView(courseId);
                }
            });
    };

    var alertData = {
        boldTextTitle: "Ошибка",
        textAlert: '',
        mode: 'danger'
    };
    var showAlertWithError = function (alertData) {
        var modalInstance = $modal.open(
            {
                templateUrl: 'angular/templates/alertModal.html',
                controller: function ($scope, $modalInstance) {
                    $scope.data = alertData;
                    $scope.close = function () {
                        $modalInstance.close();
                    }
                },
                backdrop: true,
                keyboard: true,
                backdropClick: true,
                size: 'lg'
            }
        );
    };

    var removeCourseFromView = function (courseId) {
        var removed = false;
        removed = removeFromArray($scope.personCourses, courseId);
        if (!removed) {
            removed = removeFromArray($scope.newCourses, courseId);
        }
        if (!removed) {
            removed = removeFromArray($scope.oldCourses, courseId);
        }

    };
    var removeFromArray = function (array, courseId) {
        for (var i = 0; i < array.length; i++) {
            if (array[i].id == courseId) {
                array.splice(i, 1);
                return true;
            }
        }
        return false;
    };

    // subscribe person to the course
    var personPhoneNumber = undefined;

    var hasPersonPhoneNumber = function () {
        return personPhoneNumber != undefined && personPhoneNumber != null;
    };

    var getPersonPhoneNumber = function () {
        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'myModalContent.html',
            controller: 'SettingInstanceCtrl',
            size: 'lg'
        });
        modalInstance.result.then(function (number) {
            personPhoneNumber = number;
        });
    };

    var requestPersonPhoneNumber = function (callback) {
        alertData.textAlert = 'Ошибка при получении контактной информации';
        personService.getPersonDescription(PersonPersistenceService.getId()).then(
            function (description) {
                if (description.responseStatus / 100 == 2) {
                    callback(description.phoneNumber);
                } else {
                    showAlertWithError(alertData);
                }
            },
            function (error) {
                showAlertWithError(alertData);
            });
    };

    $scope.subscribePersonToCourse = function (courseId) {
        if (!hasPersonPhoneNumber()) {
            if (personPhoneNumber == undefined) {
                requestPersonPhoneNumber(function (phone) {
                    personPhoneNumber = phone;
                    if (personPhoneNumber != undefined) {
                        $scope.subscribePersonToCourse(courseId);
                    }
                });
            }
            else if (personPhoneNumber == null) {
                getPersonPhoneNumber();
                if (personPhoneNumber != null) {
                    $scope.subscribePersonToCourse(courseId);
                }
            }
        }
        else {
            courseService.subscribePersonToCourse(courseId, PersonPersistenceService.getId()).then(
                function (success) {
                    $state.go($state.current.name, $state.params, {reload: true});
                })
        }
    };

}

function SettingCtrl($scope, PersonPersistenceService, PersonService, $modal) {
    // init
    $scope.animationsEnabled = true;

    $scope.person = $scope.person || {};
    var description = PersonService.getPersonDescription(PersonPersistenceService.getId());
    $scope.person.id = PersonPersistenceService.getId();
    $scope.person.name = PersonPersistenceService.getName().split(" ")[0];
    $scope.person.surname = PersonPersistenceService.getName().split(" ")[1];
    $scope.person.phoneNumber = description.phoneNumber;
    $scope.person.graduation = description.graduation;
    $scope.person.experience = description.experience;

    $scope.open = function (size) {
        console.log("hello");
        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'myModalContent.html',
            controller: 'SettingInstanceCtrl',
            size: size
            //resolve: {
            //    items: function () {
            //        return $scope.items;
            //    }
            //}
        });

        modalInstance.result.then(function (number) {
            $scope.person.phoneNumber = number;
        });

    };

    $scope.isValidUser = function () {
        if (!this.isValidFIO()) {
            var alertData = {
                boldTextTitle: 'Проверьте Ваши введённые ФИО. Имя или фамилия не могут быть пустыми!',
                mode: 'warning'
            };
            showAlertWithError(alertData);
        }

        return this.isValidFIO();
    };

    $scope.isPersonHasPhone = function () {
        return $scope.person.phoneNumber.length > 0 && $scope.person.phoneNumber.length < 11;
    };

    $scope.isValidFIO = function () {
        return $scope.person.name.length > 0 &&
            $scope.person.surname.length > 0
    };

    $scope.phoneValidator = function () {
        $scope.person.phoneNumber = $scope.person.phoneNumber.replace(/[^\d]/g, '');//замена символов на пустые, для ввода только цифр
    };

}
function SettingInstanceCtrl($scope, $modalInstance) {

    $scope.ok = function () {
        $modalInstance.close($scope.phoneNumber);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}
myApp.directive('gravatar', function() {
    return {
        restrict: 'AE',
        replace: true,
        scope: {
            name: '@',
            width: '@',
            hash: '@'
        },
        link: function(scope, el, attr) {
            scope.defaultImage = 'https://cdn4.iconfinder.com/data/icons/e-commerce-icon-set/48/Username_2-32.png';
        },
        template: '<img class="circular" alt="{{name}}" ng-src="https://secure.gravatar.com/avatar/{{hash}}.jpg?s={{width}}&d={{defaultImage}}">'
    };
});
myApp.directive('numberFormat', ['$filter', '$parse', function ($filter, $parse) {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelController) {
            var decimals = $parse(attrs.decimals)(scope);

            ngModelController.$parsers.push(function (data) {
                //convert data from view format to model format
                return $filter('number')(data, decimals); //converted
            });

            ngModelController.$formatters.push(function (data) {
                //convert data from model format to view format
                return $filter('number')(data, decimals); //converted
            });

            element.bind('focus', function () {
                element.val(ngModelController.$modelValue);
            });

            element.bind('blur', function () {
                element.val(ngModelController.$viewValue);
            });
        }
    }
}]);

function SessionInjector($rootScope, sessionService) {
    var sessionInjector = {
        request: function (config) {
            if (!sessionService.isAnonymous()) {
                config.headers['x-session-token'] = sessionService.getAccessToken();
                config.headers['x-session-id'] = sessionService.getUserId();
            }
            return config;
        },
        response: function (response) {
            response.data = response.data || {};
            response.data.responseStatus = response.status;
            return response;
        },
        responseError: function (response) {
            if (response.status === 401) {
                $rootScope.$broadcast('app.unauthorized');
            }
            response.data = response.data || {};
            response.data.responseStatus = response.status;
            return response;
        }
    };
    return sessionInjector;
}
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
angular.module('myApp.news', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider

            .state('noNews', {
                parent: 'main',
                url:'/nonews',
                templateUrl: 'angular/views/noNews.html',
                controller: 'NoNewsCtrl'
            })
            .state('news', {
                parent: 'main',
                url: '/news',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/news.html',
                        controller: 'NewsCtrl'
                    }
                },
                resolve: {

                    newsService: 'NewsService',
                    news: function ($rootScope, newsService) {
                        var promise;
                        $rootScope.redirectToDraftPage= function () {
                           $location.path('/nonews');
                        };


                        if ($rootScope.getUserId() == null||$rootScope.getUserId() == undefined) {
                            promise = newsService.getNews();

                        } else {
                           promise = newsService.getPersonNews($rootScope.getUserId());

                        }
                        return promise.then(function (news) {
                            return news;
                        });
                    }
                }
            })
    }])
    .service('NewsService', NewsService)
    .controller('NewsCtrl', NewsCtrl);






/**
 * Created by olga on 24.06.15.
 */
angular.module('myApp.person', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            //PERSON
            .state('person', {
                parent: 'main',
                url: '/person',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/course.html',
                        controller: 'PersonCtrl'
                    }
                },
                resolve: {
                    courseService: 'CourseService',
                    personService: 'PersonService',
                    coursesGroups: function ($rootScope, courseService, personService) {
                        var promise = personService.getCoursesStatusesForPerson($rootScope.getUserId());
                        promise = promise.then(function (personCourses) {
                            if (personCourses.responseStatus != 200) {
                                return promise;
                            }
                            promise = courseService.getNewCourses();
                            promise = promise.then(function (newCourses) {
                                if (newCourses.responseStatus != 200) {
                                    return promise;
                                }
                                for (var i = 0; i < personCourses.length; i++) {
                                    for (var j = 0; j < newCourses.length; j++) {
                                        if (personCourses[i].id == newCourses[j].id) {
                                            newCourses.splice(j, 1);
                                            break;
                                        }
                                    }
                                }
                                return {
                                    'coursesEnrolled': personCourses,
                                    'coursesToSubscribe': newCourses
                                };
                            });
                            return promise;
                        });
                        return promise;
                    },
                    oldCourses: function ($rootScope, courseService) {
                        if (!$rootScope.isTeacher()) {
                            return [];
                        }
                        var promise = courseService.getOldCourses();
                        promise = promise.then(function (oldCourses) {
                            if (oldCourses.responseStatus != 200) {
                                return promise;
                            }
                            return oldCourses;
                        });
                        return promise;
                    }
                }
            })
            //ADDCOURSE
            .state('person.addCourse', {
                url: '/addCourse',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/person-course/teacher/addCourse.html',
                        controller: "AddOrEditCourseCtrl"
                    }
                },
                resolve: {
                    personService: 'PersonService',
                    courseService: 'CourseService',
                    allTeachers: function (mode, personService) {
                        var promise = personService.getTeachers();
                        promise = promise.then(function (teachers) {
                            return teachers;
                        });
                        return promise;
                    },
                    coursePrototypes: function (mode, courseService) {
                        var promise = courseService.getCourses();
                        promise = promise.then(function (courses) {
                            courses.push({'id': -1, 'name': 'Новый курс'});
                            return courses;
                        });
                        return promise;
                    },
                    mode: function () {
                        return 'add';
                    },
                    course: function () {
                        return {};
                    }
                }
            })
            //COURSE
            .state('person.course', {
                url: '/course/:courseId',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/person-course/main.html'
                    },
                    "top@person.course": {
                        templateUrl: 'angular/views/person-course/top.html',
                        controller: function ($scope, course) {
                            $scope.courseName = course.name;
                        }
                    },
                    "menubar@person.course": {
                        templateUrl: 'angular/views/person-course/menu.html'
                    },
                    "content@person.course": {
                        templateUrl: 'angular/views/person-course/content.html'
                    }
                },
                resolve: {
                    courseService: 'CourseService',
                    course: function ($stateParams, courseService) {
                        var promise = courseService.getCourse($stateParams.courseId);
                        promise = promise.then(function (course) {
                            if (course.responseStatus != 200) {
                                return null;
                            }
                            return courseService.normalizeCourse(course);
                        });
                        return promise;
                    }
                }
            })
            .state('person.course.edit', {
                url: '/edit',
                templateUrl: 'angular/views/person-course/teacher/addCourse.html',
                controller: 'AddOrEditCourseCtrl',
                resolve: {
                    courseService: 'CourseService',
                    allTeachers: function () {
                        return [];
                    },
                    coursePrototypes: function () {
                        return [];
                    },
                    mode: function () {
                        return 'edit';
                    }
                }
            })
            .state('person.course.content', {
                url: '/content',
                templateUrl: 'angular/views/person-course/courseContent.html',
                controller: 'CourseContentCtrl',
                resolve: {
                    courseContentService: 'CourseContentService',
                    lectures: function ($stateParams, courseContentService) {
                        var promise = courseContentService.getLectures($stateParams.courseId);
                        promise = promise.then(function (lectures) {
                            return lectures;
                        });
                        return promise;
                    }
                }
            })
            .state('person.course.editLecture', {
                url: '/editLecture/:lectureOrderNum',
                templateUrl: 'angular/views/person-course/teacher/addLecture.html',
                controller: 'AddLectureCtrl',
                resolve: {
                    courseContentService: 'CourseContentService',
                    lecture: function ($stateParams, courseContentService) {
                        var promise = courseContentService.getLecture($stateParams.courseId, $stateParams.lectureOrderNum);
                        promise = promise.then(function (lecture) {
                            if (lecture.responseStatus != 200) {
                                return null;
                            }
                            return courseContentService.normalizeLecture(lecture);
                        });
                        return promise;
                    },
                    mode: function () {
                        return 'edit';
                    }
                }
            })
            .state('person.course.calendar', {
                url: '/calendar',
                templateUrl: 'angular/views/person-course/calendarContent.html',
                controller: function ($scope, calendarUrl) {
                    $scope.calendarUrl = calendarUrl;

                },
                resolve: {
                    calendarUrl: function ($sce, course) {
                        return $sce.trustAsResourceUrl(
                            "https://www.google.com/calendar/embed?" +
                            "wkst=2&hl=ru&bgcolor=%23ffffff&" +
                            "src=" + course.calendarId +
                            "&color=%235229A3&ctz=Europe%2FKiev");
                    }
                }
            })
            .state('person.course.newsContent', {
                url: '/news-content',
                templateUrl: 'angular/views/person-course/newsContent.html',
                controller: 'NewsCtrl',
                resolve: {
                    newsService: 'NewsService',
                    news: function ($stateParams, newsService) {
                        var promise = newsService.getNewsFromCourse($stateParams.courseId);
                        promise = promise.then(function (news) {
                            return news;
                        });
                        return promise;
                    }
                }
            })
            .state('person.course.notification', {
                url: '/notification',
                templateUrl: 'angular/views/person-course/teacher/notification.html'
            })
            .state('person.course.addLecture', {
                url: '/addLecture/:lectureOrderNum',
                templateUrl: 'angular/views/person-course/teacher/addLecture.html',
                controller: 'AddLectureCtrl',
                resolve: {
                    lecture: function () {
                        return {};
                    },
                    mode: function () {
                        return 'add';
                    }
                }
            })
            .state('person.course.lecture', {
                url: '/lecture/:lectureId',
                templateUrl: 'angular/views/person-course/lectureContent.html',
                controller: 'LectureContentCtrl',
                resolve: {
                    courseContentService: 'CourseContentService',
                    lecture: function ($stateParams, courseContentService) {
                        var promise = courseContentService.getLecture($stateParams.courseId, $stateParams.lectureId);
                        promise = promise.then(function (lecture) {
                            if (lecture.responseStatus != 200) {
                                return null;
                            }
                            return lecture;
                        });
                        return promise;
                    },
                    lecturesCnt: function ($stateParams, courseContentService) {
                        var promise = courseContentService.getLectures($stateParams.courseId);
                        promise = promise.then(function (lectures) {
                            if (lectures.responseStatus != 200) {
                                return null;
                            }
                            return lectures.length;
                        });
                        return promise;
                    }
                }
            })
            .state('person.course.addNews', {
                url: '/addNews',
                templateUrl: 'angular/views/person-course/teacher/addNewNews.html',
                controller: 'AddNewsCtrl'
            })
            .state('person.course.progress', {
                url: '/progress',
                templateUrl: 'angular/views/person-course/progressContent.html'
            })
            //SETTINGs
            .state('person.settings', {
                url: '/settings',
                views: {
                    "body@main": {
                        templateUrl: 'angular/views/settings/body.html'
                    },
                    "setting-top@person.settings": {
                        templateUrl: 'angular/views/settings/setting-top.html'
                    },
                    "setting-menubar@person.settings": {
                        templateUrl: 'angular/views/settings/menubar.html'
                    },
                    "setting-content@person.settings": {
                        templateUrl: 'angular/views/person-course/content.html'
                    }
                }
            })
            .state('person.settings.personal', {
                url: '/personal',
                templateUrl: 'angular/views/settings/personal.html'
            })
            .state('person.settings.addition', {
                url: '/addition',
                templateUrl: 'angular/views/settings/settings.html'
            })
    }])
    .service('PersonService', PersonService)
    .service('NewsService', NewsService)
    .service('CourseService', CourseService)
    .controller('AddNewsCtrl', AddNewsCtrl)
    .service('CourseContentService', CourseContentService)
    .controller('NewsCtrl', NewsCtrl)
    .controller('PersonCtrl', PersonCtrl)
    .controller('AddOrEditCourseCtrl', AddOrEditCourseCtrl)
    .controller('DatepickerCtrl', DatepickerCtrl)
    .controller('CourseContentCtrl', CourseContentCtrl)
    .controller("AddLectureCtrl", AddLectureCtrl)
    .controller("LectureContentCtrl", LectureContentCtrl)
    .controller("SettingCtrl", SettingCtrl)
    .controller("SettingInstanceCtrl", SettingInstanceCtrl);




var teacherAccess = function ($location, PersonPersistenceService) {
    if (PersonPersistenceService.getRole() == "teacher") {
        return true;
    } else {
        $location.path("/home");
    }
};
var studentAccess = function ($location, PersonPersistenceService) {
    if (PersonPersistenceService.getRole() == "student") {
        return true;
    } else {
        $location.path("/home");
    }
};
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


function AuthService(PersonService, Restangular, PersonPersistenceService) {
    this.goAuth = PersonService.createPersonForAuth;
    this.getEmailHash = function (email) {
        //console.log(email);
        return Restangular.all("resources").one("mailhash").get({"email": email}).then(function(result){
            return result;
        })
    };
}
/**
 * Created by olga on 25.06.15.
 */
function CourseContentService(Restangular) {
    var restBase = 'resources/course';

    var createLectureRest = function (courseId) {
        return Restangular.one(restBase, courseId).all('lesson');
    };

    this.getLectures = function (courseId) {
        if (courseId === undefined) {
            return [];
        }
        return createLectureRest(courseId).getList();
    };

    this.createLecture = function (newLecture) {
        return createLectureRest(newLecture.courseId).post(newLecture);
    };

    this.removeLecture = function (courseId, lectureOrderNum) {
        return Restangular.one(restBase, courseId).one('lesson', lectureOrderNum).remove();
    };

    this.updateLecture = function (lecture, removedLinks, removedPractices) {
        return Restangular.one(restBase, lecture.courseId).one('lesson', lecture.orderNum)
            .customPUT(lecture, undefined, {removedLinks: removedLinks, removedPractices: removedPractices}, undefined);
    };

    this.getLecture = function (courseId, lectureOrderNum) {
        return Restangular.one(restBase, courseId).one('lesson', lectureOrderNum).get();
    };

    this.normalizeLecture = function(lecture) {
        return {
            'id': lecture.id,
            'orderNum': lecture.orderNum,
            'courseId': lecture.courseId,
            'topic': lecture.topic,
            'content': lecture.content,
            'createDate': lecture.createDate,
            'links': lecture.links,
            'practices': lecture.practices
        }
    };
}
function CourseService(Restangular) {
    var restBase = 'resources/course';
    var Course = Restangular.all(restBase);

    this.getCourses = function () {
        return Course.getList();
    };

    this.getNewCourses = function () {
        var today = new Date();
        var todayAsString = today.getUTCFullYear() + '-' + (today.getUTCMonth() + 1) + '-' + today.getUTCDay();
        return Course.getList({'date': todayAsString, 'period': 'start_after'});
    };

    this.getOldCourses = function () {
        var today = new Date();
        var todayAsString = today.getUTCFullYear() + '-' + (today.getUTCMonth() + 1) + '-' + today.getUTCDay();
        return Course.getList({'date': todayAsString, 'period': 'end_before'});
    };

    this.createCourse = function (newCourse, prototypeId) {
        // POST returns promise, in which successHandler is executed ALWAYS when response contains any text
        // so, it's necessary to check is response status equal 2xx or not
        // or another way --- any successfully returned object contains field 'fromServer' with value 'true'
        return Course.post(newCourse, {'prototypeId': prototypeId});
    };
    this.isCourseSuccessfullyCreated = function (returnedObject) {
        return returnedObject.responseStatus == 201;
    };

    this.removeCourse = function (id) {
        return Restangular.one(restBase, id).remove();
    };

    this.updateCourse = function (course) {
        return Restangular.one(restBase).customPUT(course, course.id);
    };

    this.getCourse = function (courseId) {
        return Restangular.one(restBase, courseId).get();
    };

    this.normalizeCourse = function (course) {
        return {
            'id': course.id,
            'name': course.name,
            'description': course.description,
            'startDate': course.startDate,
            'endDate': course.endDate,
            'calendarId': course.calendarId
        };
    };

    this.subscribePersonToCourse = function (courseId, personId) {
        if (courseId === undefined || personId === undefined) {
            return {};
        }
        return Restangular.one(restBase, courseId).all('subscribe').customPUT({"id": personId});
    };
    //FIXME заглушка, для нормальной работы на без запуска WildFly
    this.getCoursesCap = function () {
        return [
            {id: 1, name: "Java EE", description: "Description for Java EE"},
            {id: 2, name: "Java SE", description: "Description for Java SE"},
            {id: 3, name: "Android", description: "Description for Android"}
        ];
    }
}

function NewsService(Restangular) {
    var restBase = 'resources/news';
    var News = Restangular.all(restBase);
    this.getNews = function () {
        return News.getList();
    };

    this.getPersonNews = function (personId) {
        if (personId === undefined) {
            return {};
        }
        return Restangular.one(restBase, personId).all('news-person').getList();
    };

    this.getNewsFromCourse = function (courseId) {
        if (courseId === undefined) {
            return {};
        }
        return Restangular.one(restBase, courseId).all('news').getList();
    };


    this.createNews = function (newNews, courseId) {
        console.log("service: create news");
        return Restangular.one(restBase, courseId).all('news').post(newNews);
    };



}

/**
 * Created by olga on 14.07.15.
 */
function PersonPersistenceService(localStorageService) {
    this.saveInfo = function (id, role, name, email, token) {
        localStorageService.set('id', id);
        localStorageService.set('role', role);
        localStorageService.set('name', name);
        localStorageService.set('email', email);
        localStorageService.set('token', token);
    };
    this.saveHash = function (hash) {
        localStorageService.set('emailhash', hash);
    };
    this.clearInfo = function () {
        localStorageService.clearAll();
    };
    this.getId = function () {
        return localStorageService.get('id');
    };
    this.getRole = function () {
        return localStorageService.get('role');
    };
    this.getName = function () {
        return localStorageService.get('name');
    };
    this.getEmail = function () {
        return localStorageService.get('email');
    };
    this.getToken = function () {
        return localStorageService.get('token');
    };
    this.getEmailHash = function () {
        return localStorageService.get('emailhash');
    };
    this.isTeacher = function () {
        return this.getRole() == 'teacher';
    };
}
function PersonService(Restangular, PersonPersistenceService) {
    var REST_BASE = 'resources/person';
    var Person = Restangular.all(REST_BASE);
    //person/:person_id/description

    this.getPersonDescription = function (userId) {
        return Restangular.one(REST_BASE, userId).customGET('description', {'field': 'phone'});//Возвращение описание человека для личного кабинета
    };

    this.createPerson = function (user) {
        var name = user.name.split(" ");

        return Person.post(
            {
                "email": user.email,
                "id": null,
                "lastName": name[1],
                "name": name[0],
                "personRole": null,
                "secondName": null
            }
        )
    };

    this.createPersonForAuth = function (user) {
        var name = user.name.split(" ");
        return Person.post(
            {
                "email": user.email,
                "id": null,
                "lastName": name[1],
                "name": name[0],
                "personRole": null,
                "secondName": null
            })
    };

    this.updatePerson = function (user) {
        return Person.one(PersonPersistenceService.getId()).put(
                {
                    "name": user.name,
                    "surname": user.surname,
                    "secondName": user.secondName
                }
            ) && PersonDescription.put({
                "experience": user.experience,
                "graduation": user.graduation,
                "phoneNumber": user.phoneNumber
            });
    };

    this.getTeachers = function () {
        return Person.getList({'role': 'teacher'});
    };

    this.getCoursesStatusesForPerson = function (personId) {
        if (personId === undefined) {
            return [];
        }
        return Restangular.one("resources/person", personId).all("course").getList();
    };

    this.getCoursesForPersonCap = function () {
        return [
            {id: 2, name: "Java SE", description: "Description for Java SE"}
        ];
    };

}
function SessionService(PersonPersistenceService) {
    var service = this;
    service.isAnonymous = function () {
        return (PersonPersistenceService.getToken() === undefined);
    };
    service.getAccessToken = function () {
        return PersonPersistenceService.getToken();
    };
    service.getUserId = function () {
        return PersonPersistenceService.getId();
    };
}