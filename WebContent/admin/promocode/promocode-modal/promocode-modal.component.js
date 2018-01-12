(function(angular) {


'use strict';
function PromoModalModalController($state, PromocodeService){
	var ctrl = this;
	ctrl.promo = (ctrl.resolve && ctrl.resolve.detailsofPromo) || {};

	//Add PromoCode 
	ctrl.save = function(promocode){     

		PromocodeService.uploadPromoFile(promocode,ctrl.promo)
		.then(function(result){
			ctrl.modalInstance.close({action: 'update'});
		})
		.catch(function(err){
			console.log('Error Adding promocode');
			console.log(err);
		});
	}

	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	}
}

angular.module('promoModal')
	.component('promoModal',{
		templateUrl: 'admin/promocode/promocode-modal/promocode-modal.template.html',
		controller:['$state','PromocodeService', PromoModalModalController],
		bindings:{
			modalInstance: '<',
			resolve: '<'
		}
	});

})(window.angular);