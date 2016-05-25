function NewsCtrl($scope, news, newsService,$modal) {
    for (var i = 0; i < news.length; i++) {
        var timestamp = news[i].date;
        news[i].date = timestampConvector(timestamp);
    }


    $scope.removeNews = function (newsId) {
        newsService.removeNews(newsId).then(
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

    $scope.news = news;



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
var timestampConvector = function (timestamp) {
    var newsCreateDate = new Date(timestamp);
    var currentDate = new Date();
    var formattedDate;
    if (currentDate.getDate() == newsCreateDate.getDate()) {
        formattedDate = "сегодня";
    } else if (currentDate.getDate() == newsCreateDate.getDate() + 1) {
        formattedDate = "вчера";
    } else {
        var date = ((newsCreateDate.getDate()) < 10) ? "0" + newsCreateDate.getDate() : newsCreateDate.getDate();
        var month = ((newsCreateDate.getMonth() + 1) < 10) ? "0" + (newsCreateDate.getMonth() + 1) : (newsCreateDate.getMonth() + 1);
        formattedDate = date + "-" + month + "-" + newsCreateDate.getFullYear();
    }

    var hours = (newsCreateDate.getHours() < 10) ? "0" + newsCreateDate.getHours() : newsCreateDate.getHours();
    var minutes = (newsCreateDate.getMinutes() < 10) ? "0" + newsCreateDate.getMinutes() : newsCreateDate.getMinutes();
    var formattedTime = hours + ":" + minutes;
    return formattedDate + " в " + formattedTime;

}