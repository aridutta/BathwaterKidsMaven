(function(angular) {
    'use strict';

    function openSubItem(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'customerSubItemModal',
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
                console.log('Error in SubItem Modal');
                console.log(err);
            }
    }

    function userReqModalController($state, $uibModal, customerUserService, Lightbox) {
        var ctrl = this;
        ctrl.$uibModal = $uibModal;
        ctrl.$state = $state;

        ctrl.customer = (ctrl.resolve && ctrl.resolve.details) || {};
        ctrl.isDisabled = Object.keys(ctrl.customer).length > 0;
        ctrl.userReq = [];
        ctrl.itemsArray = [];
        ctrl.imageURLs = [];
        //ctrl.itemSelected = false;

        ctrl.UserReqmessage = true;
        ctrl.noItemMessage = true;



        ctrl.init = function() {
            ctrl.noUserReqMessage = true;

            customerUserService.getUserRequest(ctrl.customer.userID)
                .then(function(response) {


                    if (response.data.length > 0) {

                        response.data.forEach(function(data) {
                            data.isChecked = false;
                        });

                        ctrl.userReq = response.data;
                        ctrl.UserReqmessage = false;

                    } else {
                        ctrl.message = true;
                        ctrl.itemsMessage = "Data does not exist";
                    }
                })
                .catch(function(err) {
                    console.log('Error getting user-request details:');
                    console.log(err);
                });
        };


        ctrl.getItems = function(item) {

            if (item.items) {
                if (item.isChecked) {

                    ctrl.noItemMessage = false;
                    ctrl.noUserReqMessage = false;
                    for (var i = 0; i < item.items.length; i++) {
                        for (var j = 0; j <= i; j++) {
                            if (typeof item.items[i].imagesBase64[j] == "undefined") {
                                //ctrl.value = item.items[i].imagesBase64[j];
                                item.items[i].imagesBase64[j] = "img/notAvailable.jpg";


                            }
                        }
                    }
                    item.items.forEach(function(data) { data.userRequestID = item.userRequestID });
                    ctrl.itemsArray = ctrl.itemsArray.concat(item.items);
                    ctrl.selectedRow = item.userRequestID;
                } else {
                    ctrl.itemsArray = ctrl.itemsArray.filter(function(data) {
                        return data.userRequestID != item.userRequestID });
                    if (ctrl.itemsArray.length == 0) {
                        ctrl.noItemMessage = true;
                    }
                }

            } else {
                //No subitems in the array
                if (ctrl.itemsArray.length == 0) {

                    ctrl.noItemMessage = true;
                }
            }

        };
        ctrl.openLightboxModal = function(images) {
            //LightBox Library used as Image Viewer.
            Lightbox.openModal(images, 0);
        };
        ctrl.displayRow = function(index) {

            ctrl.displayRowValue = index;
            ctrl.selectedRow = "";
        };

        ctrl.subItems = function(subitem) {

            angular.bind(ctrl, openSubItem, subitem)();
        };

        ctrl.cancel = function() {
            ctrl.modalInstance.close();
        };

        ctrl.init();
    }

    angular.module('customerUserReqModal')
        .component('customerUserReqModal', {
            templateUrl: 'customers/customer-userRequest-modal/customer-userRequest-modal.template.html',
            controller: ['$state', '$uibModal', 'customerUserService', 'Lightbox', userReqModalController],
            bindings: {
                modalInstance: '<',
                resolve: '<'
            }
        });
})(window.angular);
