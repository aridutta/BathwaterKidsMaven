(function(angular) {
	"use strict";
	
	function UserRequestServiceHandler($http){

		var getUserList = function(){
			return $http({
					        url: "/rest/getUserTS",
					        headers: {
					            'Authorization': "Basic YWRtaW46YWRtaW4=",
					            'Content-Type':'application/json'
					        },
					        method: "GET"
            			})
					};

		var assignDriver = function(reqId, driver){
			return $http({
				            url: '/rest/assignDriverToUserRequest/' + driver.driverID + '?userReqID=' + reqId,
				            headers: {
				                'Content-Type': 'text/plain',
				                'Authorization': "Basic YWRtaW46YWRtaW4="
				            },
				            method: "GET"
        				});
					};

		//EXPORTED Object
		return {
			getUserList,
			assignDriver
			
		}
	}

	angular.module('bathwaterApp.services')
		.factory('UserRequestService',['$http',UserRequestServiceHandler]);	

})(window.angular);