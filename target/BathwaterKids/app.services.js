angular.module('bathwaterApp')
	.factory('InterceptorService',[function () {
		var object;

		object = {
			'request': function(config){

				//Adding base URL to the Request object
				if(["GET","POST"].indexOf(config.method) > -1 && !config.url.includes('.html')){
					config.url = angular.config.baseUrl + config.url;
				}

				return config;
			},
			'requestError':function(rejection){
			},
			'response': function(response){
				return response;
			},
			'responseError': function(rejection){
			}
		}

		return object;
	}]);