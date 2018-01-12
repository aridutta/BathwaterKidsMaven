(function(angular) {
	"use strict";
	
	function ZipcodeServiceHandler($http){

		var getZipCodes = function(){
			return $http({
						    url: '/rest/getZipCodes',
						    method: "GET",
						    headers:{
						    	"Authorization": 'Basic YWRtaW46YWRtaW4='
						    }
						});
					};

		var addZipCode = function(zipcode){
			
			return $http({
							url: '/rest/addZipCode',
				            method: "POST",
				            data: zipcode,
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
					};

		var deleteZipCode = function(zipcode){
			return $http({
						    url: '/rest/deleteZipCode/' + zipcode,
						    method: "GET",
						    headers:{
						    	"Authorization": 'Basic YWRtaW46YWRtaW4='
						    }
						});
					};
		//EXPORTED Object
		return {

			getZipCodes,
			addZipCode,
			deleteZipCode
		}
	}

	angular.module('bathwaterApp.services')
		.factory('ZipcodeService',['$http',ZipcodeServiceHandler]);	

})(window.angular);