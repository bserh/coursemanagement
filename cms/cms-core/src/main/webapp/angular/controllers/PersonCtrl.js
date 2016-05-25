function PersonCtrl($state, $scope, $modal, PersonPersistenceService, courseService, personService, coursesGroups, oldCourses) {

    $scope.personCourses = coursesGroups.coursesEnrolled;
    $scope.newCourses = coursesGroups.coursesToSubscribe;
    $scope.oldCourses = oldCourses;

    $scope.getActionMsg = function (status, teacherRole) {
        return courseService.getActionMsgByStatus(status, teacherRole);
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
    var requestPhoneNumberFromPerson = function (callback) {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'angular/templates/phoneModal.html',
            controller: function ($scope, $modalInstance) {
                $scope.person = {'phone': ''};
                $scope.ok = function () {
                    $modalInstance.close($scope.person.phone);
                };
                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            },
            size: 'sm'
        });
        modalInstance.result.then(function (number) {
            personService.setPersonPhone(PersonPersistenceService.getId(), number).then(
                function (success) {
                    if (success.responseStatus / 100 == 2) {
                        callback(number);
                    }
                });
        });
    };

    var requestPhoneNumberFromServer = function (callback) {
        alertData.textAlert = 'Ошибка при получении контактной информации';
        personService.getPersonPhone(PersonPersistenceService.getId()).then(
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

    var validatePhoneNumber = function (phone) {
        return phone;
    };
    var doSubscribing = function (courseId) {
        courseService.subscribePersonToCourse(courseId, PersonPersistenceService.getId()).then(
            function (success) {
                $state.go($state.current.name, $state.params, {reload: true});
            });
    };

    $scope.subscribePersonToCourse = function (courseId) {
        requestPhoneNumberFromServer(function (phoneNumber) {
            if (!validatePhoneNumber(phoneNumber)) {
                requestPhoneNumberFromPerson(function (number) {
                    doSubscribing(courseId);
                })
            } else {
                doSubscribing(courseId);
            }
        });
    };
}
