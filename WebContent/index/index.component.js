(function(angular) {
'use strict';

function IndexController($state , AdminRightsService) {
	var ctrl = this;

	var userRights = AdminRightsService.getRights();
	ctrl.open = function(name){
		switch(name){
			case 'admin': {
				if(userRights.Admin) $state.go('adminLayout.drivers');
				else if(userRights.Pickup) $state.go('deliveryLayout.userRequests');
				else if(userRights.Customers) $state.go('customers.user');
				else $state.go('adminLayout.drivers');
				break;
			}
			case 'junit': break;
			case 'pick': break;
			case 'consumer': break;
		}
	};
}

angular.module('index')
.component('index',{
	templateUrl: 'index/index.template.html',
	controller: ['$state','AdminRightsService', IndexController]
	});

})(window.angular);