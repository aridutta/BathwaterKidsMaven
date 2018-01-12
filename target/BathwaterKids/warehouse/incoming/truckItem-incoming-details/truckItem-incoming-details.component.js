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
            component: 'viewTruckItemModal',
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

                if (data && data.action == 'update') popUpCtrl.selectedDate(data.date);
            }),
            function(err) {
                console.log('Error while viewing incoming item modal');
                console.log(err);
            }

    }

    function receiveIncomingProductPopup(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'receiveincomingProductModal',
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
                console.log('Error while receiving incoming item modal');
                console.log(err);
            }

    }

    function storedIncomingProductPopup(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'storedProductModal',
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
                console.log('Error while storing incoming item modal');
                console.log(err);
            }

    }


    function TruckItemIncomingController($state, $uibModal, warehouseMoveItemService) {
        var ctrl = this;
        ctrl.$uibModal = $uibModal;
        ctrl.$state = $state;
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

        ctrl.viewReceivedItems = function() {
            angular.bind(ctrl, receiveIncomingProductPopup, null)();
        };

        ctrl.viewStoredItems = function() {
            angular.bind(ctrl, storedIncomingProductPopup, null)();
        };

        ctrl.viewItems = function(item) {
            angular.bind(ctrl, viewItemPopup, item)();
        };

        ctrl.viewDriverDetail = function(driverInfo) {
            angular.bind(ctrl, driverInfoPopup, driverInfo)();
        }

        ctrl.selectRow = function(rowIndex) {
            ctrl.selectedRow = rowIndex;
        };

        ctrl.selectedDate = function(date) {

            ctrl.loader = true;

            warehouseMoveItemService.incomingItems(date)
                .then(function(response) {
                    if (response == undefined || response == null) {
                        ctrl.message = true;
                        ctrl.loader = false;
                    }
                    if (angular.isArray(response.data)) {
                        ctrl.message = false;
                        ctrl.incomingItems = response.data;
                        ctrl.loader = false;
                    } else {
                        ctrl.message = true;
                        ctrl.loader = false;
                    }

                })
                .catch(function(err) {
                    
                    console.log('Error getting incoming item details:');
                    console.log(err);
                });
        }

        ctrl.init();
    }

    angular.module('truckItemIncomingWarehouseDetails')
        .component('truckItemIncomingWarehouseDetails', {
            templateUrl: 'warehouse/incoming/truckItem-incoming-details/truckItem-incoming-details.template.html',
            controller: ['$state', '$uibModal', 'warehouseMoveItemService', TruckItemIncomingController]
        });
})(window.angular);
