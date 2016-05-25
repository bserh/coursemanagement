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