(function(angular) {
    'use strict';

    function assignDriverPopUp(details, userDetail) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'assignDriverModal',
            windowClass: 'app-modal-window-small',
            resolve: {
                details: function() {
                    return (details || {});
                },
                userDetail: function() {
                    return (userDetail || {});
                }
            },
            keyboard: false,
            backdrop: 'static'
        });

        modalInstance.result.then(function(data) {
                //data passed when pop up closed.
                if (data && data.action == 'update') {

                    userDetail.driver = userDetail.driver || {};
                    userDetail.driver.firstName = data.userRequestDetail.firstName;
                    userDetail.driver.lastName = data.userRequestDetail.lastName;
                }


            }),
            function(err) {
                console.log('Error in assign Driver Modal');
                console.log(err);
            }

    }

    function UserRequestNotStartedModalController($state, $uibModal, UserRequestService, DriverService) {
        var ctrl = this;
        ctrl.$uibModal = $uibModal;
        ctrl.$state = $state;
        ctrl.notstarted = (ctrl.resolve && ctrl.resolve.details) || {};
        ctrl.drivers = (ctrl.resolve && ctrl.resolve.drivers) || {};

        ctrl.init = function() {


        };

        ctrl.assignDriver = function(reqId, dr) {

            angular.bind(ctrl, assignDriverPopUp, reqId, dr)();
        };

        ctrl.cancel = function() {
            ctrl.modalInstance.close();
        };
        ctrl.init();
    }

    angular.module('userRequestNotStartedModal')
        .component('userRequestNotStartedModal', {
            templateUrl: 'pickup-delivery-management/user-request/user-request-modal/user-request-notstarted-modal/user-request-notstarted-modal.template.html',
            controller: ['$state', '$uibModal', 'UserRequestService', 'DriverService', UserRequestNotStartedModalController],
            bindings: {
                modalInstance: '<',
                resolve: '<'
            }
        });

})(window.angular);
