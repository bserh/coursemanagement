function SessionInjector($rootScope, sessionService) {
    var sessionInjector = {
        request: function (config) {
            if (!sessionService.isAnonymous()) {
                config.headers['x-session-token'] = sessionService.getAccessToken();
                config.headers['x-session-id'] = sessionService.getUserId();
            }
            return config;
        },
        response: function (response) {
            response.data = response.data || {};
            response.data.responseStatus = response.status;
            return response;
        },
        responseError: function (response) {
            if (response.status === 401) {
                $rootScope.$broadcast('app.unauthorized');
            }
            response.data = response.data || {};
            response.data.responseStatus = response.status;
            return response;
        }
    };
    return sessionInjector;
}