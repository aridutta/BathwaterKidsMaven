(function(angular) {
	"use strict";
	
	function PickupTruckServiceHandler($http){

		var assignDriverToTruck = function(driverid,truckid){
			return $http({
							url: '/rest/assignDriverToTruck/'+ truckid + '?driverID=' + driverid,
				            method: "GET",
				            headers: {
				                'Authorization': "Basic YWRtaW46YWRtaW4="
				                
				            }
						});
					};
					
		var getAllDriverTruckHistory = function(){
			return $http({
				            url: '/rest/getAllDriverTruckHistory',
				            method: "GET",
				            headers:{
				            	"Authorization": 'Basic YWRtaW46YWRtaW4='
				         }
	        		});
				};

		//EXPORTED Object
		return {
			assignDriverToTruck,
			getAllDriverTruckHistory	
		}
	}

	angular.module('bathwaterApp.services')
		.factory('PickupTruckService',['$http',PickupTruckServiceHandler]);	

})(window.angular);