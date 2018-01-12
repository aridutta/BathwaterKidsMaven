(function(angular) {


    'use strict';

    function AssignDriverModalController($state, DriverService, UserRequestService) {
        var ctrl = this;
        ctrl.userRequestDetail = (ctrl.resolve && ctrl.resolve.userDetail) || {};
        ctrl.setDriver = {};

        ctrl.init = function() {
            DriverService.getAllDrivers()
                .then(function(response) {
                    ctrl.drivers = response.data;
                })
                .catch(function(err) {
                    console.log("Error while calling DriverService:" + err);
                })
        }

        ctrl.assignDriver = function() {

            var updatedDriverDetails = angular.fromJson(ctrl.userRequestDetail.selectedDriver);
            UserRequestService.assignDriver(ctrl.userRequestDetail.userRequestID, updatedDriverDetails)
                .then(function(result) {
                    ctrl.modalInstance.close({ action: 'update', userRequestDetail: updatedDriverDetails });
                })
                .catch(function(err) {
                    console.log('Error while assiging driver to user-request');
                    console.log(err);
                });


        }

        ctrl.cancel = function() {
            ctrl.modalInstance.close();
        };

        ctrl.init();
    }

    angular.module('assignDriverModal')
        .component('assignDriverModal', {
            templateUrl: 'pickup-delivery-management/user-request/assign-driver-modal/assign-driver-modal.template.html',
            controller: ['$state', 'DriverService', 'UserRequestService', AssignDriverModalController],
            bindings: {
                modalInstance: '<',
                resolve: '<'
            }
        });

})(window.angular);
