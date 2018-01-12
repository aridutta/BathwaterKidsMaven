(function(angular) {
'use strict';

function deliverytruckModalController($state,TruckService,DriverService,PickupTruckService) {
	var ctrl = this;
	ctrl.assign = (ctrl.resolve && ctrl.resolve.details) || {};
	
	ctrl.init = function(){

		TruckService.getAllTrucks()
			.then(function(truckDetails){
				ctrl.trucks = truckDetails.data;
			})
				.catch(function(err){
					console.log('Error getting truck details:');
					console.log(err);
			});

		DriverService.getAllDrivers()
            .then(function (response) {
               ctrl.drivers = response.data;
            })
            .catch(function(err){
					console.log('Error getting driver list details:');
					console.log(err);
			})

	};

	ctrl.assign = function(driverid,truckid){
		
		PickupTruckService.assignDriverToTruck(driverid,truckid)
					.then(function(result){
						//ctrl.modalInstance.close('update');
				})
					.catch(function(err){
						console.log('Error in assigning truck & driver');
						console.log(err);
			});
		};
	
	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}

	 ctrl.init();
}

angular.module('deliverytruckModal')
	.component('deliverytruckModal',{
		templateUrl: 'pickup-delivery-management/delivery-trucks/delivery-trucks-assign-modal/delivery-trucks-assign-modal.template.html',
		controller:['$state','TruckService','DriverService','PickupTruckService', deliverytruckModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);