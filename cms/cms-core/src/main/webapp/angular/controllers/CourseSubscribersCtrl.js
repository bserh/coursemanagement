function CourseSubscribersCtrl($scope, courseService, courseId, subscribers) {
    $scope.hideProcessed = true;
    $scope.subscribers = subscribers.filter(courseService.isStatusNotProcessed);
    $scope.getLabelForStatus = function (status) {
        return courseService.convertStatusToLabel(status);
    };
    $scope.availableStatuses = courseService.getAvailableStatuses();
    $scope.updateStatuses = function () {
        courseService.updateCourseSubscribers(courseId, subscribers).then(function (success) {
            if($scope.hideProcessed) {
                $scope.showHideProcessed();
            }
        });
    };

    $scope.showHideProcessed = function () {
        $scope.subscribers = $scope.hideProcessed ? subscribers.filter(courseService.isStatusNotProcessed) : subscribers;
    };
    // ----------------  search -------------------------
    $scope.search = '';
    var searchFunction = function (value) {
        return value.personLastName.indexOf($scope.search) == 0;
    };
    $scope.searchNames = function () {
        $scope.showHideProcessed();
        $scope.subscribers = $scope.subscribers.filter(searchFunction);
    };
    // ----------------  pagination -------------------------
    $scope.currentPage = 1;
    $scope.maxPersonsPerPage = 2;
    $scope.numPages = 5;
}
