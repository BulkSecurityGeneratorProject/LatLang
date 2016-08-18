(function() {
    'use strict';
    angular
        .module('adobeLatLangApp')
        .factory('Entry', Entry)
        .factory('Entry1', Entry1)
    	.factory('Entry2', Entry2);

    Entry.$inject = ['$resource'];
    Entry1.$inject = ['$resource'];
    Entry2.$inject = ['$resource'];

    function Entry ($resource) {
        var resourceUrl =  'api/entries/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
    
    function Entry1 ($resource) {
        var resourceUrl =  'api/urls/:url';

        return $resource(resourceUrl, {}, {
        	'update': { method:'GET', isArray: true }
        });
    }
    
    function Entry2 ($resource) {
        var resourceUrl =  'api/upload';

        return $resource(resourceUrl, {}, {
        	'save': { method:'POST',
        		headers: {
                    'Content-Type': 'multipart/form-data'
                }
        		}
        });
    }
})();
