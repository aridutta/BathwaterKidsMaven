(function(angular) {

    'use strict'

    function openPopUpDelete(details){

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component:'deleteAdminModal',
            windowClass: 'app-modal-window-small',
            keyboard: false,
            resolve: {
                details: function(){
                    return (details || {});
            }
                
            },
            backdrop: 'static'

        });

        modalInstance.result.then(function(data) {
            // if(data && data.action == "delete") {
            //     var index = data.details;
            //     this.adminList.splice(index, 1);
            // }

        }), function(err){
                    console.log('Error in manage-admin Modal');
                    console.log(err);        }
    };

    function openPopUpAdmin(details) {

        var popUpCtrl = this;
        var modalInstance = popUpCtrl.$uibModal.open({
            component: 'addAdminModal',
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
            if (data && data.action == "update") popUpCtrl.init();
            
        }), function(err) {
            console.log('Error in manage-admin Modal');
            console.log(err);
        }
    }

    function manageAdminController($state, $uibModal, AdminManagerService) {

        var ctrl = this;
        ctrl.$uibModal = $uibModal;
        ctrl.$state = $state;
        ctrl.adminList = [];

        //API /admin/login
        ctrl.init = function() {
            ctrl.isSuperAdmin = true;
            getAdminList();
          };

        ctrl.continue = function(){
            $state.go('index');
        };

        ctrl.edit = function(adminrights) {
            angular.bind(ctrl, openPopUpAdmin, angular.copy(adminrights))();
        };

        ctrl.delete = function(selectedEmail) {

             angular.bind(ctrl,openPopUpDelete,selectedEmail)();
           
        };
        ctrl.selectRow = function(rowIndex){
         ctrl.selectedRow = rowIndex;
        };

        ctrl.addadmin = function() {
            angular.bind(ctrl, openPopUpAdmin, null)();
        };

        function getAdminList(){

           AdminManagerService.listofAdmin()
                .then(function(response) {
                    if (response && response.data) {
                        ctrl.adminList = response.data;
                   }
                })
                .catch(function(err) {
                    console.log('Error getting Admin lists:');
                    console.log(err);
                })
        };
        
        ctrl.init();
    }

    angular.module('manageAdmin')
        .component('manageAdmin', {
            templateUrl: 'manage-admin/manage-admin-details/manage-admin-details.template.html',
            controller: ['$state', '$uibModal','AdminManagerService', manageAdminController]
        });
})(window.angular);