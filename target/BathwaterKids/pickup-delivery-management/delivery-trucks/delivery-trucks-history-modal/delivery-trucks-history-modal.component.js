(function(angular) {
'use strict';

function historyModalController($state,PickupTruckService) {
	var ctrl = this;
	
	ctrl.init = function(){
		//get truck details.
		 PickupTruckService.getAllDriverTruckHistory()
			.then(function(truckDetails){
				ctrl.trkhistories = truckDetails.data;
			})
			.catch(function(err){
				console.log('Error getting truck histories details:');
				console.log(err);
			})
	};


	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
	 ctrl.init();
}

angular.module('historyModal')
	.component('historyModal',{
		templateUrl: 'pickup-delivery-management/delivery-trucks/delivery-trucks-history-modal/delivery-trucks-history-modal.template.html',
		controller:['$state','PickupTruckService', historyModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);