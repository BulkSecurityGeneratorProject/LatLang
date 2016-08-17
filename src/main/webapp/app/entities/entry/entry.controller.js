(function() {
    'use strict';

    angular
        .module('adobeLatLangApp')
        .controller('EntryController', EntryController);

    EntryController.$inject = ['$scope', '$state', 'Entry', 'Entry1', 'Entry2', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants'];

    function EntryController ($scope, $state, Entry, Entry1, Entry2, ParseLinks, AlertService, pagingParams, paginationConstants) {
        var vm = this;
        
        vm.urlFull = '';
        vm.myFile = '';
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.processUrl = processUrl;
        vm.processUpload = processUpload;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        
        loadAll();

        function loadAll () {
            Entry.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.entries = data;
                vm.page = pagingParams.page;
                vm.urlFull = '';
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }
        
        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
        
        function processUrl () {
        	var values = vm.urlFull.split('/');
        	vm.url = values[5];
            Entry1.update({url: vm.url},onSuccess, onerror
            );
            
            function onSuccess() {
            	loadAll();
            }
            function onError() {
            }
        }
        
        function processUpload () {
            Entry2.get({file: vm.myFile},onSuccess, onerror
            );
            
            function onSuccess() {
            	loadAll();
            }
            function onError() {
            }
        }
        
        function proc (files) {
            var fd = new FormData();
            fd.append("file", files[0]);
            Entry2.get({file: vm.myFile},onSuccess, onerror
            );
            function onSuccess() {
            	loadAll();
            }
            function onError() {
            }
        };
    }
})();
