define(['angular', './controllers/index', './states/index', 'angular-route'], function(angular) {
	'use strict';
	var app =  angular.module('app', ['app.controllers', 'app.states', 'ngRoute']);
	
	return app;
});