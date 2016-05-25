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
