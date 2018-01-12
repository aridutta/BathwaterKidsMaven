(function(angular) {
	"use strict";

	angular.module('bathwaterApp.common')
		.filter('YesNo', function(){
			return function(value){
				return value ? 'YES' : 'NO';
			}
		});
		
})(window.angular)