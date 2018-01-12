(function(angular) {
	"use strict";
	
	function InventoryServiceHandler($http){

		
		var getInventory = function(){
			return $http({
		            url: '/rest/getItems',
		            method: "GET",
		            headers:{
	            		"Authorization": 'Basic YWRtaW46YWRtaW4='
	            }
	        });
		};

		var updateInventory = function(storedItemID, credit){
			return $http({
		            url: '/rest/admin/updateCredits?storedID='+storedItemID+'&credits='+credit,
		            method: "GET",
		            headers:{
	            		"Authorization": 'Basic YWRtaW46YWRtaW4='
	            }
	        });
		};

		
		

		//EXPORTED Object
		return {
			getInventory,
			updateInventory,
			
		}
	}

	angular.module('bathwaterApp.services')
		.factory('inventoryService',['$http',InventoryServiceHandler]);	

})(window.angular);