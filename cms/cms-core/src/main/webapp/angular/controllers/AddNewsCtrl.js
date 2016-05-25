function AddNewsCtrl($scope, $stateParams, NewsService ,$modal,$state) {
    $scope.news = {};

    $scope.createNews = function () {
        $scope.news.date = new Date();
        $scope.news.courseId =  $stateParams.courseId;
         NewsService.createNews($scope.news, $stateParams.courseId)
            .then(
            function (createdNews) {
                if (NewsService.isNewsSuccessfullyCreated(createdNews)) {
                     $state.go('news', {}, {reload: true});
                } else {
                    alertData.textAlert = createdNews;
                    showAlertWithError(alertData);
                }
            },
            function () {
                alertData.textAlert = 'Причина неизвестна';
                showAlertWithError(alertData);
            });
    };

    $scope.removeNews = function (newsId) {

        NewsService.removeNews(newsId).then(
            function (success) {

                if (success.responseStatus / 100 != 2) {
                    showAlertWithError(
                        {
                            boldTextTitle: "Ошибка",
                            textAlert: success,
                            mode: 'danger'
                        });
                }else{
                    $state.go('news', {}, {reload: true});
                }
            });
    };


    var alertData = {
        boldTextTitle: "Ошибка",
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



    var alertData = {
        boldTextTitle: "Ошибка",
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
}
