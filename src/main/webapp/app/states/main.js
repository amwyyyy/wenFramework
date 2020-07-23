define(['states/states', 'app'], function(states, app) {
	app.config(['$routeProvider', function($routeProvider) {
		$routeProvider.when('/view', {
			templateUrl : 'app/templates/test.html',
			controller : 'MyCtrl as vm'
		});
		
		$routeProvider.otherwise({
			redirectTo : '/view'
		});
	}]);
});