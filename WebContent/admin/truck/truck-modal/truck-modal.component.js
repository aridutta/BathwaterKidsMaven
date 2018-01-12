(function(angular) {
'use strict';

function TruckModalController($scope, $rootScope,$state, resizeService, TruckService) {
	var ctrl = this;
	ctrl.truck = (ctrl.resolve && ctrl.resolve.details) || {};
	ctrl.isDisabled = Object.keys(ctrl.truck).length > 0;
	if(ctrl.truck.images && ctrl.truck.images.length > 0){
		ctrl.imageUrl = ctrl.truck.images[0].url;
	}

	// Watch the image change and show from base 64 value
	// $scope is only used here for watch.
	$scope.$watch(angular.bind(ctrl, function(){
		return ctrl.selectedImage;
	}), function(value){
		value ? 
			(ctrl.imageUrl = 'data:image/jpeg;base64, ' + value.base64, ctrl.truck.truckImage = value.base64)
			: (ctrl.truck.truckImage = '');
	});

	ctrl.save = function(){

		TruckService.addTruckwithImage(ctrl.truck)
			.then(function(result){
				ctrl.modalInstance.close({action: 'update'});
		})
		.catch(function(err){
			console.log('Error Adding Truck');
			console.log(err);
		});
	}

	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('truckModal')
	.component('truckModal',{
		templateUrl: 'admin/truck/truck-modal/truck-modal.template.html',
		controller:['$scope','$rootScope','$state', 'resizeService','TruckService', TruckModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);