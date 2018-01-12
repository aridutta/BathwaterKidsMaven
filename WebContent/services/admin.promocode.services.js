(function(angular) {
	"use strict";
	
	function PromocodeServiceHandler($http){

		var getPromos = function(){
			return $http({
						    url: '/rest/getPromos',
						    method: "GET",
						    headers:{
						    	"Authorization": 'Basic YWRtaW46YWRtaW4='
						    }
						});
					};

		var uploadPromoFile = function(promocode, promo){
			
			return $http({
							url: '/rest/uploadPromoFile/' + promocode,
				            method: "POST",
				            data: promo,
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

		var deletePromoCode = function(promocode){
			return $http({
						    url: '/rest/deletePromoCode/' + promocode,
						    method: "GET",
						    headers:{
						    	"Authorization": 'Basic YWRtaW46YWRtaW4='
						    }
						});
					};

		//EXPORTED Object
		return {
			getPromos,
			uploadPromoFile,
			deletePromoCode
		}
	}

	angular.module('bathwaterApp.services')
		.factory('PromocodeService',['$http',PromocodeServiceHandler]);	

})(window.angular);