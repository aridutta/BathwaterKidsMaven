
(function(angular){
'use strict';

function TimeslotModalController($state, TimeslotService) {
	var ctrl = this;
	ctrl.timeslot = {days:{}, timeslots:{}, availables:{}};
	
	ctrl.save = function(){

		TimeslotService.createTimeSlotsRange(ctrl.timeslot)
			.then(function(result){
				ctrl.modalInstance.close({action: "update"});
			})
			.catch(function(err){
				console.log('Error Timeslot detail');
				console.log(err);
			});
			
		};

	ctrl.cancel = function(){
		ctrl.modalInstance.close();
	};
}

angular.module('timeslotModal')
	.component('timeslotModal',{
		templateUrl: 'admin/timeslot/timeslot-modal/timeslot-modal.template.html',
		controller:['$state','TimeslotService', TimeslotModalController],
		bindings:{
			modalInstance: '<'
		}
	});

})(window.angular)