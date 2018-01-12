(function(angular) {
'use strict';

function ViewUserDetailModalController($state) {
	var ctrl = this;
	ctrl.noUserDetail = false;
	ctrl.userDetail = (ctrl.resolve && ctrl.resolve.details) || {};
	ctrl.isDisabled = Object.keys(ctrl.userDetail).length > 0;

	ctrl.init = function(){
		if(ctrl.userDetail.user == null){
			ctrl.noUserDetail = true;
		}
	}
	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	};
	ctrl.init();
	
}

angular.module('viewUserDetailModal')
	.component('viewUserDetailModal',{
		templateUrl: 'warehouse/incoming/view-UserDetail-Modal/view-UserDetail-Modal.template.html',
		controller:['$state', ViewUserDetailModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});
})(window.angular);