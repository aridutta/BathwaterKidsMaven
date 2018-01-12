(function(angular){
	'use strict';

	function openPopUpAssign(details){

		var popUpCtrl = this;
		var modalInstance = popUpCtrl.$uibModal.open({
			component: 'deliverytruckModal',
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
			//if(data == "update") popUpCtrl.$state.reload();
			
		}), function(err){
			console.log('Error in assign trucks & driver Modal');
			console.log(err);
		}
	}
	function openPopUpHistory(details){

		var popUpCtrl = this;
		var modalInstance = popUpCtrl.$uibModal.open({
			component: 'historyModal',
			windowClass: 'app-modal-window-large',
			keyboard: false,
			resolve:{
				details: function(){
					return (details||{});
				}
			},
			backdrop: 'static'
		});
		modalInstance.result.then(function(data){
			//data passed when pop up closed.
			//if(data == "update") popUpCtrl.$state.reload();
			
		}),function(err){
			console.log('Error in history of trucks details Modal');
			console.log(err);
		}

	}

	function trucksController($state,$uibModal){
		var ctrl = this;
		ctrl.$uibModal = $uibModal;
		ctrl.$state = $state;

		ctrl.assign = function(){
			angular.bind(ctrl,openPopUpAssign,null)();
		};
		ctrl.history = function(){
			angular.bind(ctrl,openPopUpHistory,null)();
		};
		

	}
	
	angular.module('deliveryTrucks')
	.component('deliveryTrucks',{
		templateUrl: 'pickup-delivery-management/delivery-trucks/delivery-trucks-details/delivery-trucks-details.template.html',
		controller:['$state','$uibModal', trucksController]
	});

})(window.angular);