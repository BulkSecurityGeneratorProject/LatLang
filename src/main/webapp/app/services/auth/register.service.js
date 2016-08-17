(function () {
    'use strict';

    angular
        .module('adobeLatLangApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
