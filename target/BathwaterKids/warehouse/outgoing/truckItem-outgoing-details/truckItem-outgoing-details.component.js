(function(angular) {

    'use strict';

    function driverInfoPopup(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'driverInfoModal',
            windowClass: 'app-modal-window-small',
            resolve: {
                details: function() {
                    return (details || {});
                }
            },
            keyboard: false,
            backdrop: 'static'
        });

        modalInstance.result.then(function(data) {
                //data passed when pop up closed.


            }),
            function(err) {
                console.log('Error in driver-Info Modal');
                console.log(err);
            }

    }

    function viewItemPopup(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'viewOutgoingTruckItemModal',
            windowClass: 'app-modal-window-large',
            resolve: {
                details: function() {
                    return (details || {});
                }
            },
            keyboard: false,
            backdrop: 'static'
        });

        modalInstance.result.then(function(data) {
                //data passed when pop up closed.


            }),
            function(err) {
                console.log('Error while viewing outgoing item Modal');
                console.log(err);
            }

    }

    function outboundItemPopUp(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'outboundProductModal',
            windowClass: 'app-modal-window-large',
            resolve: {
                details: function() {
                    return (details || {});
                }
            },
            keyboard: false,
            backdrop: 'static'
        });

        modalInstance.result.then(function(data) {
                //data passed when pop up closed.


            }),
            function(err) {
                console.log('Error while viewing outgoing item Modal');
                console.log(err);
            }
    }

    function TruckItemOutgoingController($state, $uibModal, warehouseMoveItemService) {
        var ctrl = this;
        ctrl.$uibModal = $uibModal;
        ctrl.$state = $state;
        ctrl.dropItemArray = [];
        ctrl.dropItem = [];
        var date;

        ctrl.init = function() {

            var d = new Date();
            var month = (d.getMonth() + 1);
            var day = d.getDate();
            var year = d.getFullYear();

            if (month < 10) {
                date = "0" + month + "." + day + "." + year;
            } else {
                date = month + "." + day + "." + year;
            }
            ctrl.todayDate = date;
            ctrl.selectedDate(date);

        };

        ctrl.viewItems = function(item) {
            angular.bind(ctrl, viewItemPopup, item)();
        };

        ctrl.viewDriverDetail = function(driverDetail) {
            angular.bind(ctrl, driverInfoPopup, driverDetail)();
        }

        ctrl.selectRow = function(rowIndex) {
            ctrl.selectedRow = rowIndex;
        };

        ctrl.selectedDate = function(date) {

            ctrl.loader = true;

            warehouseMoveItemService.outgoingItems(date)
                .then(function(response) {
                    
                    if (response == undefined || response == null) {
                        ctrl.message = true;
                        ctrl.loader = false;
                    }
                    if (angular.isArray(response.data)) {
                        ctrl.message = false;
                        ctrl.outgoingItems = response.data;
                        ctrl.loader = false;

                    } else {
                        ctrl.message = true;
                        ctrl.loader = false;
                    }
                })
                .catch(function(err) {
                    console.log('Error getting outgoing item details:');
                    console.log(err);
                });
        };
        ctrl.viewOutboundItems = function() {
            angular.bind(ctrl, outboundItemPopUp, null)();
        };

        ctrl.init();
    }

    angular.module('truckItemOutgoingWarehouseDetails')
        .component('truckItemOutgoingWarehouseDetails', {
            templateUrl: 'warehouse/outgoing/truckItem-outgoing-details/truckItem-outgoing-details.template.html',
            controller: ['$state', '$uibModal', 'warehouseMoveItemService', TruckItemOutgoingController]
        });
})(window.angular);
