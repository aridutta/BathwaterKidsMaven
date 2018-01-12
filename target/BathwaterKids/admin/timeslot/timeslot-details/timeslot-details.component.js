(function(angular) {

'use strict';
function addTimeslotPopUp(details){

	var popUpCtrl = this;
	var modalInstance = popUpCtrl.$uibModal.open({
			component: 'timeslotModal',
			windowClass: 'app-modal-window-large',
			resolve: function(){
				return (details||{});
			},
			keyboard: false,
			backdrop: 'static'
		});

		modalInstance.result.then(function(data){
			//data passed when pop up closed.
					if(data && data.action == "update") popUpCtrl.init();
				
				}),function(err){
					console.log('Error in add-timeslot Modal');
					console.log(err);
			}
		
}
function showTimeslotPopup(details){

	var popUpCtrl = this;
	var modalInstance = popUpCtrl.$uibModal.open({
			component: 'timeslotShowAllModal',
			windowClass: 'app-modal-window-large',
			keyboard: false,
			backdrop: 'static'
		});

		modalInstance.result.then(function(data){
			//data passed when pop up closed.
			//if(data == "update") this.$state.reload();
			
				}),function(err){
					console.log('Error in show-timeslot Modal');
					console.log(err);
		}
		
}


function TimeslotController($state, $uibModal, TimeslotService) {
	var ctrl = this;
	ctrl.$uibModal = $uibModal;
	ctrl.$state = $state;

	ctrl.init = function(){

		TimeslotService.getTimeslotsForTheWeek()
			.then(function(timeslotDetails){
				ctrl.timeslots = timeslotDetails.data;
			})
			.catch(function(err){
				console.log('Error getting timeslot details:');
				console.log(err);
		})
	};

	//Add Timeslot
	ctrl.addTimeslot = function(){
		angular.bind(ctrl,addTimeslotPopUp,null)();
	};
	//Show Timeslot
	ctrl.showallTimeslot = function(){
		angular.bind(ctrl, showTimeslotPopup, null)();
	};

	ctrl.init(); 
}

angular.module('timeslotDetails')
	.component('timeslotDetails',{
		templateUrl: 'admin/timeslot/timeslot-details/timeslot-details.template.html',
		controller:['$state','$uibModal','TimeslotService', TimeslotController]
	});
})(window.angular);
