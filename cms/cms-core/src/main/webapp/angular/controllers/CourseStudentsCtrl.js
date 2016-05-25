function CourseStudentsCtrl($scope, students, practices, personService) {
    $scope.students = students.sort(personService.personsCompare);
    $scope.practices = practices.sort(
        function (v1, v2) {
            return v1.virtOrderNum - v2.virtOrderNum;
        }
    );

    $scope.getMark = function (marks, practiceId) {
        for (var i = 0; i < marks.length; i++) {
            if (marks[i].lessonId == practiceId) {
                return marks[i].mark;
            }
        }
        return 0;
    };
    $scope.updateMarksForStudent = function (student) {
        var marks = [];
        var marksCnt = 0;
        for (var i = 0; i < student.viewPracticeMarks.length; i++) {
            if (student.viewPracticeMarks[i].updated) {
                marks[marksCnt] = {
                    'id': student.viewPracticeMarks[i].mark.id,
                    'mark': student.viewPracticeMarks[i].mark.value,
                    'lessonId': student.viewPracticeMarks[i].lessonId
                };
                marksCnt++;
            }
        }
        personService.setMarksForPerson(student.id, marks);
    };
    // ----------------  search -------------------------
    $scope.search = '';
    var searchFunction = function (value) {
        return value.lastName.indexOf($scope.search) == 0;
    };
    $scope.searchByName = function () {
        $scope.students = students.filter(searchFunction).sort(personService.personsCompare);
    };
    // ----------------  pagination -------------------------
    $scope.currentPage = 1;
    $scope.maxPersonsPerPage = 2;
    $scope.numPages = 5;
    // ----------------  practice pagination -------------------------
    $scope.maxPracticesPerPage = 5;
    $scope.numPracticePages = Math.ceil($scope.practices.length / $scope.maxPracticesPerPage);
    $scope.currentPracticePage = $scope.numPracticePages;
    var getMockPracticesCnt = function () {
        var last = $scope.practices.length % $scope.maxPracticesPerPage;
        if (last == 0) {
            return 0;
        }
        return $scope.maxPracticesPerPage - last;
    };
    $scope.mockPracticesCnt = getMockPracticesCnt();
    $scope.mockPractices = [];
    for (var i = 0; i < $scope.mockPracticesCnt; i++) {
        $scope.mockPractices.push(i);
    }
    $scope.isFirstPracticePage = function () {
        return $scope.currentPracticePage == 1;
    };
    $scope.isLastPracticePage = function () {
        return $scope.currentPracticePage == $scope.numPracticePages;
    };
    $scope.getLeftLimit = function () {
        if ($scope.isFirstPracticePage()) {
            return 0;
        }
        return ($scope.currentPracticePage - 1) * $scope.maxPracticesPerPage - $scope.mockPracticesCnt;
    };
    $scope.getRightLimit = function () {
        if ($scope.isFirstPracticePage()) {
            return $scope.maxPracticesPerPage - $scope.mockPracticesCnt;
        }
        return $scope.currentPracticePage * $scope.maxPracticesPerPage - $scope.mockPracticesCnt;
    };
}
