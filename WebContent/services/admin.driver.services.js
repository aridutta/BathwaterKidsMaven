(function(angular) {
	"use strict";
	
	function DriverServiceHandler($http){

		var getAllDrivers = function(){
			return $http({
			    url: '/rest/getAllDrivers',
			    method: "GET",
			    headers:{
			    	"Authorization": 'Basic YWRtaW46YWRtaW4='
			    }
			});
		};

		var addDriver = function(driver){
			return $http({
						url: '/rest/addDriver',
			            method: "POST",
			            data: driver,
			            transformRequest: function(obj) {
					        var str = [];
					        for(var p in obj)
					        	str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
					        return str.join("&");
					    },
			            headers: {
			                'Authorization': "Basic YWRtaW46YWRtaW4=",
			                "Content-Type": "application/x-www-form-urlencoded"
			            }
					});
		}

		//EXPORTED Object
		return {
			getAllDrivers,
			addDriver
		}
	}

	angular.module('bathwaterApp.services')
		.factory('DriverService',['$http',DriverServiceHandler]);	

})(window.angular);