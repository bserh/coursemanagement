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