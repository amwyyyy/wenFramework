define(['angular', 'domReady', 'app'], function(angular) {
	require(['domReady!'], function(document) {
		angular.bootstrap(document, ['app']);
	});
});