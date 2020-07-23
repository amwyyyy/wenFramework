require.config({
	paths : {
		'angular' : '../vendor/angularjs/angular',
		'angular-ui-router' : '../vendor/angular-ui-router/angular-ui-router',
		'angular-route' : '../vendor/angularjs/angular-route',
		'domReady' : '../vendor/require-domready/domReady'
	},
	
	shim : {
		'angular' : {
			exports : 'angular'
		},
		'angular-route' : {
			deps : ['angular'],
			exports : 'angular-route' 
		},
		'angular-ui-router' : {
            deps : ['angular'],
            exports : 'angular-ui-router'
        },
	},
	
	deps : [
		'./bootstrap'
	]
});