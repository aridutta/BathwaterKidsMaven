(function(angular) {


'use strict';
function UpdateLocStoredModalController($state, ngToast, warehouseMoveItemService){
	var ctrl = this;
	ctrl.itemDetail = (ctrl.resolve && ctrl.resolve.details) || {};

	ctrl.save = function(location, credit){
		// if(location == ""){
		// 	location = "nolocation";
		// }     
		var credits = parseInt(credit);

		warehouseMoveItemService.checkInStoredItem(ctrl.itemDetail.storedItemId, "STORED", location, credits)
				.then(function(result){
			ctrl.modalInstance.close({action: 'update'});
		})
		.catch(function(err){
			console.log('Error updating status & location of item in warehouse');
			console.log(err);
		});
	}

	// ctrl.save = function(location){
	// 	if(location == ""){
	// 		location = "nolocation";
	// 	}     
	// 	warehouseMoveItemService.updateItemInWarehouse(ctrl.itemDetail.storedItemId, location, "STORED")
	// 			.then(function(result){
	// 		ctrl.modalInstance.close({action: 'update'});
	// 		ngToast.create({
 //                content: 'Item Moved to Stored Items',
                
 //            });
	// 	})
	// 	.catch(function(err){
	// 		console.log('Error updating status & location of item in warehouse');
	// 		console.log(err);
	// 	});

	// 	// warehouseMoveItemService.updateDropItemStatus(ctrl.itemDetail.storedItemId, location, status, ctrl.itemDetail.itemCode[0])
	// 	// 		.then(function(result){
	// 	// 	//ctrl.modalInstance.close({action: 'update'});
	// 	// 	console.log(result)
	// 	// })
	// 	// .catch(function(err){
	// 	// 	console.log('Error updating status & location of item in warehouse');
	// 	// 	console.log(err);
	// 	// });
	// }


	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('updateLocStoredModal')
	.component('updateLocStoredModal',{
		templateUrl: 'warehouse/incoming/updateLocStatus-stored-modal/updateLocStatus-stored-modal.template.html',
		controller:['$state','ngToast','warehouseMoveItemService', UpdateLocStoredModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);