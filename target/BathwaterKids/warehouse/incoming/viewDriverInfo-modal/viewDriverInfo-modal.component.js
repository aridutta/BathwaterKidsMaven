(function(angular) {


'use strict';
function DriverInfoModalController($state){
	var ctrl = this;
	ctrl.driverInfo = (ctrl.resolve && ctrl.resolve.details) || {};

	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('driverInfoModal')
	.component('driverInfoModal',{
		templateUrl: 'warehouse/incoming/viewDriverInfo-modal/viewDriverInfo-modal.template.html',
		controller:['$state', DriverInfoModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);