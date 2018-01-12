(function(angular) {
'use strict';

function ZipModalModalController($rootScope,$state, ZipcodeService) {
	var ctrl = this;
	ctrl.zip = (ctrl.resolve && ctrl.resolve.details) || {};

	ctrl.save = function(){                  
		
		ZipcodeService.addZipCode(ctrl.zip)
			.then(function(result){
				ctrl.modalInstance.close({action: 'update'});
		})
		.catch(function(err){
			console.log('Error Adding Driver');
			console.log(err);
		});
	}

	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('zipModal')
	.component('zipModal',{
		templateUrl: 'admin/zipcode/zipcode-modal/zip-modal.template.html',
		controller:['$rootScope','$state','ZipcodeService', ZipModalModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});
})(window.angular);