myApp.directive('numberFormat', ['$filter', '$parse', function ($filter, $parse) {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelController) {
            var decimals = $parse(attrs.decimals)(scope);

            ngModelController.$parsers.push(function (data) {
                //convert data from view format to model format
                return $filter('number')(data, decimals); //converted
            });

            ngModelController.$formatters.push(function (data) {
                //convert data from model format to view format
                return $filter('number')(data, decimals); //converted
            });

            element.bind('focus', function () {
                element.val(ngModelController.$modelValue);
            });

            element.bind('blur', function () {
                element.val(ngModelController.$viewValue);
            });
        }
    }
}]);
