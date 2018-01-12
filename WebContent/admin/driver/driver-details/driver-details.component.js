(function(angular) {
'use strict';

//transformed data for display
function transformData(driver){
	driver.streetAddress = driver.address.streetAddress;
	driver.apartment = driver.address.apartment;
	driver.city = driver.address.city;
	driver.state = driver.address.state;
	driver.zipCode = driver.address.zipCode;
	driver.emergencyPhoneNumber = driver.emergencyContactNumber;
	driver.licenseId = driver.licenseID
	return driver;
}

function openPopUp(details){
	
	var popUpCtrl = this;
	var modalInstance = popUpCtrl.$uibModal.open({
			component: 'driverModal',
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
			
		}, function(err){
			console.log('Error in add-driver Modal');
			console.log(err);
		})
}

function DriverDetailsController($state, $uibModal, DriverService) {
	var ctrl = this;
	ctrl.$uibModal = $uibModal;
	ctrl.$state = $state;
	ctrl.init = function(){
		//get driver details.
		DriverService.getAllDrivers()
		.then(function(driverDetails){
			ctrl.drivers = driverDetails.data;
		})
		.catch(function(err){
			console.log('Error getting driver details:');
			console.log(err);
		})
	};

	// Add Driver Modal
	ctrl.addDriver = function(){
		angular.bind(ctrl, openPopUp, null)();
	};

	//Show Driver's Modal
	ctrl.showDetails = function(driverDetails){
		angular.bind(ctrl, openPopUp, transformData(driverDetails))();
	}

	ctrl.init();
}

angular.module('driverDetails')
	.component('driverDetails',{
		templateUrl: 'admin/driver/driver-details/driver-details.template.html',
		controller:['$state', '$uibModal','DriverService', DriverDetailsController]
	});
})(window.angular);