(function(angular) {
    'use strict';

    function moreDetailsModalController($state, Lightbox) {
        var ctrl = this;

        ctrl.itemDetail = (ctrl.resolve && ctrl.resolve.details) || {};
        ctrl.isDisabled = Object.keys(ctrl.itemDetail).length > 0;

        ctrl.openLightboxModal = function(images) {
            //LightBox Library used as Image Viewer.
            Lightbox.openModal(images, 0);
        };

        ctrl.init = function() {
        	
            if (ctrl.itemDetail.subItems.length > 0) {

                for (var i = 0; i < ctrl.itemDetail.subItems.length; i++) {
                    for (var j = 0; j <= i; j++) {
                        if (typeof ctrl.itemDetail.subItems[i].imageURLs[j] == "undefined") {

                            ctrl.itemDetail.subItems[i].imageURLs[0] = "https://www.moh.gov.bh/Content/Upload/Image/636009821114059242-not-available.jpg";

                        }
                    }

                }

            }else{
            	ctrl.itemMessage = "No Sub Items to display";
            }

        };

        ctrl.cancel = function() {
            ctrl.modalInstance.close();
        };

        ctrl.init();
    }

    angular.module('moreDetailsModal')
        .component('moreDetailsModal', {
            templateUrl: 'warehouse/incoming/more-details-modal/more-details-modal.template.html',
            controller: ['$state', 'Lightbox', moreDetailsModalController],
            bindings: {
                modalInstance: '<',
                resolve: '<'
            }
        });
})(window.angular);
