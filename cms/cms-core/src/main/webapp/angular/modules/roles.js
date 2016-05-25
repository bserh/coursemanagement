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