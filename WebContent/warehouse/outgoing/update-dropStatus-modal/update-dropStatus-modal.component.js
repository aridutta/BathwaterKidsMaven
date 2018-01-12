(function(angular) {


'use strict';
function updateDropStatusModalController($state, ngToast, warehouseMoveItemService
){
	var ctrl = this;
	ctrl.itemDetail = (ctrl.resolve && ctrl.resolve.details) || {};

	ctrl.save = function(storedItemID){

		 warehouseMoveItemService.updateDropItemStatus(storedItemID, "OUTBOUND")
                .then(function(response) {
                    ctrl.modalInstance.close();
                })
                .catch(function(err) {
                    console.log('Error getting update-Drop-Item-Status of outgoing item details:');
                    console.log(err);
                });
	 }


	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('updateDropStatusModal')
	.component('updateDropStatusModal',{
		templateUrl: 'warehouse/outgoing/update-dropStatus-modal/update-dropStatus-modal.template.html',
		controller:['$state','ngToast','warehouseMoveItemService', updateDropStatusModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);