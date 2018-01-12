(function(angular){
	'use strict';
	function openPopupCreditUpdate(details){

		var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'updateCreditModal',
            windowClass: 'app-modal-window-small',
            keyboard: false,
            resolve: {
                details: function() {
                    return (details || {});
                }
            },
            backdrop: 'static'
        });

        modalInstance.result.then(function(data) {
            //data passed when pop up closed.
            //if (data && data.action == "update");
            if(data && data.action == "update") popUpCtrl.init();
            
        }), function(err) {
            console.log('Error in inventory-incoming-credit-update Modal');
            console.log(err);
        }
	}
	function inventoryIncomingDetailsController($state, $uibModal,Lightbox, inventoryService){
		var ctrl = this;
		ctrl.$state = $state;
		ctrl.$uibModal = $uibModal;

		ctrl.init = function(){

			inventoryService.getInventory()
					.then(function(response){
						ctrl.Inventory = response.data;
					})
					.catch(function(err){
						console.log('Error getting user-items details:');
						console.log(err);
					});	

		};

		ctrl.openLightboxModal = function (images) {
		//LightBox Library used as Image Viewer.
			Lightbox.openModal(images, 0);
  		};
  		ctrl.selectRow = function(rowIndex){
         ctrl.selectedRow = rowIndex;
    	};

		ctrl.addUpdateCredit = function(item){
			angular.bind(ctrl, openPopupCreditUpdate, angular.copy(item))();
		};

		ctrl.init();
	}
	
	angular.module('inventoryIncomingDetails')
	.component('inventoryIncomingDetails',{
		templateUrl: 'inventory/incoming-details/incoming-details.template.html',
		controller:['$state','$uibModal','Lightbox','inventoryService', inventoryIncomingDetailsController]
	});

})(window.angular);