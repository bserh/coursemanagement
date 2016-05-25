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