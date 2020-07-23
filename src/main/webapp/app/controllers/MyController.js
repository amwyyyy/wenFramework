define([ 'controllers/controllers' ], function(controllers) {
	'use strict';
	controllers.controller('MyCtrl', [ function($scope) {
		alert($scope);
	}]);
});