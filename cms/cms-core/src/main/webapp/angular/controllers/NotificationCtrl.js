function NotificationCtrl(Restangular, $scope){
    var restBase = '';//FIXME write ws
    var Notification = Restangular.all(restBase);

    $scope.mailModel = {
        sms: true,
        email: false
    };

    //$scope.onClickChangeFlagsInSms = function () {
    //    if(mailModel.sms) mailModel.email = false;
    //    if(!mailModel.sms) mailModel.email = true;
    //};
    //
    //$scope.onClickChangeFlagsInEmail = function () {
    //    if(mailModel.email) mailModel.sms = false;
    //    if(!mailModel.email) mailModel.sms = true;
    //};

    $scope.doNotifying = function(){
        //if($scope.mailModel.email){
        //    Notification.post({
        //        mailtype: "MAIL",
        //        message: $scope.emailMessage,
        //        subject: $scope.subject
        //    })
        //}
        //if($scope.mailModel.sms){
        //    Notification.post({
        //        mailtype: "SMS",
        //        message: $scope.smsMessage
        //    })
        //}
    };
}