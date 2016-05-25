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