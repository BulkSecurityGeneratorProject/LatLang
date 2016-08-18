(function() {
    'use strict';

    angular
        .module('adobeLatLangApp')
        .directive('demoFileModel', demoFileModel);
    
    demoFileModel.$inject = ['$parse'];

    function demoFileModel($parse) {
        var directive = {
            restrict: 'A',
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            var model = $parse(attrs.demoFileModel),
                modelSetter = model.assign; //define a setter for demoFileModel

            //Bind change event on the element
            element.bind('change', function () {
                //Call apply on scope, it checks for value changes and reflect them on UI
                scope.$apply(function () {
                    //set the model value
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    }
})();
