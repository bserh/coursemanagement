myApp.directive('gravatar', function() {
    return {
        restrict: 'AE',
        replace: true,
        scope: {
            name: '@',
            width: '@',
            hash: '@'
        },
        link: function(scope, el, attr) {
            scope.defaultImage = 'https://cdn4.iconfinder.com/data/icons/e-commerce-icon-set/48/Username_2-32.png';
        },
        template: '<img class="circular" alt="{{name}}" ng-src="https://secure.gravatar.com/avatar/{{hash}}.jpg?s={{width}}&d={{defaultImage}}">'
    };
});