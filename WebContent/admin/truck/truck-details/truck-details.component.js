(function(angular) {
'use strict';

//transformed data for display
function transformData(truck){
	truck.dealerStreetAddress = truck.dealerAddress.streetAddress;
	truck.dealerCity = truck.dealerAddress.city;
	truck.dealerState = truck.dealerAddress.state;
	truck.dealerZip = truck.dealerAddress.zipCode;
	truck.dealerPhNumber = truck.phoneNumber;
	truck.leaseExpirationDate = truck.leaseExpiration;
	return truck;
}

function openPopUp(details){

	var popUpCtrl = this;
	var modalInstance = popUpCtrl.$uibModal.open({
			component: 'truckModal',
			windowClass: 'app-modal-window-large',
			keyboard: false,
			resolve:{
				details: function(){
					return (details || {});
				}
			},
			backdrop: 'static'
		});

		modalInstance.result.then(function(data){
			//data passed when pop up closed.
			if(data && data.action == "update") popUpCtrl.init();
			
		}),function(err){
			console.log('Error in add-truck Modal');
			console.log(err);
		}
		
}

function TruckDetailsController($state, $uibModal, resizeService, TruckService) {
	var ctrl = this;
	ctrl.$uibModal = $uibModal;
	ctrl.$state = $state;

	ctrl.init = function(){
		//get truck details.
			TruckService.getAllTrucks()
				.then(function(truckDetails){
					ctrl.trucks = truckDetails.data;
				})
				.catch(function(err){
					console.log('Error getting truck details:');
					console.log(err);
				});
	};

	//Add Truck Modal
	ctrl.addTruck = function(){
		angular.bind(ctrl, openPopUp, null)();
	};

	ctrl.showDetails = function(truckDetails){
		angular.bind(ctrl, openPopUp, transformData(truckDetails))();
	}

	ctrl.init();
}

angular.module('truckDetails')
	.component('truckDetails',{
		templateUrl: 'admin/truck/truck-details/truck-details.template.html',
		controller:['$state', '$uibModal', 'resizeService','TruckService', TruckDetailsController]
	});
})(window.angular);