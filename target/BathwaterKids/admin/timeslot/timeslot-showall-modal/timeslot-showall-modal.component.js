(function(angular){

'use strict';

function TimeslotShowAllModal($state, TimeslotService) {
    var ctrl = this;

    ctrl.init = function(){

         TimeslotService.getTimeslots()
                .then(function(timeslotShowAllModal) {
                    ctrl.alltimeslot = timeslotShowAllModal.data;
                })
                .catch(function(err){
                    console.log('Error Timeslot detail');
                    console.log(err);
                });
    };

    ctrl.cancelshowall= function(){
		ctrl.modalInstance.close();
    };

    ctrl.init();
}

angular.module('timeslotShowAllModal')
    .component('timeslotShowAllModal', {
        templateUrl: 'admin/timeslot/timeslot-showall-modal/timeslot-showall-modal.template.html',
        controller: ['$state','TimeslotService', TimeslotShowAllModal],
        bindings: {
            modalInstance: '<'
        }
    });
    
})(window.angular);