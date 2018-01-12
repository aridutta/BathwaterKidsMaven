(function(angular) {
    'use strict';


    function openPopUpCompleted(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'userRequestCompleteModal',
            windowClass: 'app-modal-window-large',
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
                //if(data == "update") this.$state.reload();

            }),
            function(err) {
                console.log('Error in user-request-completed Modal');
                console.log(err);
            }
    };

    function openPopUpnotstarted(details, drivers) {

        var popUpCtrl = this;

        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'userRequestNotStartedModal',
            windowClass: 'app-modal-window-large',
            keyboard: false,
            resolve: {
                details: function() {
                    return (details || {});

                },
                drivers: function() {
                    return (drivers || {})
                }
            },
            backdrop: 'static'
        });

        modalInstance.result.then(function(data) {
                //popUpCtrl.loader = false;
                //data passed when pop up closed.
                //if(data == "update") this.$state.reload();

            }),
            function(err) {
                console.log('Error in user-request-notStarted Modal');
                console.log(err);
            }
    };

    function openPopUpinProgress(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'userRequestInProgressModal',
            windowClass: 'app-modal-window-large',
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
                //if(data == "update") this.$state.reload();

            }),
            function(err) {
                console.log('Error in user-request-inProgress Modal');
                console.log(err);
            }
    };

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
      function userDetailPopUp(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'viewUserDetailModal',
            windowClass: 'app-modal-window-large',
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

            }),
            function(err) {
                console.log('Error in user detail Modal of Incoming Warehouse');
                console.log(err);
            }
    }

    function UserRequestController($state, $uibModal, UserRequestService, DriverService, $q) {
        var ctrl = this;
        ctrl.$uibModal = $uibModal;
        ctrl.$state = $state;
        ctrl.completeRequest = [];
        ctrl.inProgressRequest = [];
        ctrl.notstartedRequest = [];
        ctrl.loader = true;
        ctrl.showNotStarted = true;
        ctrl.showInProgress = false;
        ctrl.showComplete = false;
        ctrl.showCancel = false;


        ctrl.init = function() {
            $q.all([UserRequestService.getUserList(), DriverService.getAllDrivers()])
                .then(function(response) {
                    ctrl.timeslots = response[0].data;
                    ctrl.drivers = response[1].data;

                    ctrl.completeRequest = ctrl.timeslots.filter(function(data) {
                        return data.status == "completed";
                    });
                    ctrl.inProgressRequest = ctrl.timeslots.filter(function(data) {
                        return data.status == "in progress";
                    });
                    ctrl.notstartedRequest = ctrl.timeslots.filter(function(data) {
                        return data.status == "not started";
                    });
                    ctrl.cancelRequest = ctrl.timeslots.filter(function(data) {
                        return data.status == "cancelled";
                    });

                    ctrl.loader = false;
                })
                .catch(function(err) {
                    console.log('Error User Request/Driver Service..')
                });
        };

        ctrl.complete = function() {
            ctrl.showNotStarted = false;
            ctrl.showInProgress = false;
            ctrl.showComplete = true;
            ctrl.showCancel = false;
            //angular.bind(ctrl, openPopUpCompleted, ctrl.completeRequest)();
        };
        ctrl.notStarted = function() {

            ctrl.showNotStarted = true;
            ctrl.showInProgress = false;
            ctrl.showComplete = false;
            ctrl.showCancel = false;

            //ctrl.loader = true;
            // angular.bind(ctrl, openPopUpnotstarted, ctrl.notstartedRequest , ctrl.drivers)();
        };
        ctrl.inprogress = function() {

            ctrl.showNotStarted = false;
            ctrl.showInProgress = true;
            ctrl.showComplete = false;
            ctrl.showCancel = false;
            // angular.bind(ctrl, openPopUpinProgress, ctrl.inProgressRequest)();
        };
        ctrl.cancel = function() {

            ctrl.showNotStarted = false;
            ctrl.showInProgress = false;
            ctrl.showComplete = false;
            ctrl.showCancel = true;
            // angular.bind(ctrl, openPopUpinProgress, ctrl.inProgressRequest)();
        };

        ctrl.viewUserDetail = function(userDetail) {

            angular.bind(ctrl, userDetailPopUp, userDetail)();
        };

        ctrl.assignDriver = function(reqId, dr) {

            angular.bind(ctrl, assignDriverPopUp, reqId, dr)();
        };

        ctrl.init();
    }

    angular.module('userRequest')
        .component('userRequest', {
            templateUrl: 'pickup-delivery-management/user-request/user-request-details/user-request.template.html',
            controller: ['$state', '$uibModal', 'UserRequestService', 'DriverService', '$q', UserRequestController]
        });
})(window.angular);
