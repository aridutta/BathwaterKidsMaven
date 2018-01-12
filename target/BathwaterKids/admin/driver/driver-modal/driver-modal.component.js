(function(angular) {
'use strict';

function DriverModalController($state, DriverService) {
	var ctrl = this;

	ctrl.driver = (ctrl.resolve && ctrl.resolve.details) || {};
	ctrl.isDisabled = Object.keys(ctrl.driver).length > 0;

	//Add Driver
	ctrl.save = function(){  

		DriverService.addDriver(ctrl.driver)
		.then(function(result){
			ctrl.modalInstance.close({action: 'update'});
		})
		.catch(function(err){
			console.log('Error Adding Driver');
			console.log(err);
		});
	};
	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('driverModal')
	.component('driverModal',{
		templateUrl: 'admin/driver/driver-modal/driver-modal.template.html',
		controller:['$state','DriverService', DriverModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});
})(window.angular);