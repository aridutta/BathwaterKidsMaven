(function(angular) {
'use strict';

function UserRequestInProgressModalController($state,UserRequestService,DriverService) {
    var ctrl = this;
    ctrl.inprogress = (ctrl.resolve && ctrl.resolve.details) || {};
    
    ctrl.init = function(){
        UserRequestService.getUserList()
                .then(function (uqinprogress) {
                ctrl.timeslotIP = uqinprogress.data;
            });

        DriverService.getAllDrivers()
            .then(function (response) {
               ctrl.drivers = response.data;
            });
       };


     ctrl.assignDriver = function (reqId, drIP) {
        var updatedDriverInP = angular.fromJson(drIP.selecteddriverP);
        UserRequestService.assignDriver(reqId,updatedDriverInP)
            .then(function (response) {
                if(response.data.response == "success"){
                    drIP.driver = drIP.driver || {};
                    drIP.driver.firstName = updatedDriverInP.firstName;
                    drIP.driver.lastName = updatedDriverInP.lastName;
                }else{
                    console.log("Invalid driver");

                }
            });
        };


    ctrl.cancel = function(){
        ctrl.modalInstance.close();
    }
    ctrl.init();
}

angular.module('userRequestInProgressModal')
    .component('userRequestInProgressModal',{
        templateUrl: 'pickup-delivery-management/user-request/user-request-modal/user-request-inprogress-modal/user-request-inprogress-modal.template.html',
        controller:['$state','UserRequestService','DriverService', UserRequestInProgressModalController],
        bindings:{
            modalInstance: '<',
            resolve: '<'
        }
    });

})(window.angular);