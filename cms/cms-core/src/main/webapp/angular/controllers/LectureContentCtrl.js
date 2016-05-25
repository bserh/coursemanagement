/**
 * Created by olga on 08.07.15.
 */
function LectureContentCtrl($scope, lecture, lecturesCnt) {
    $scope.lecture = lecture;
    $scope.enablePreview = ($scope.lecture.orderNum != 1);
    $scope.enableNext = (lecturesCnt != lecture.orderNum);
}