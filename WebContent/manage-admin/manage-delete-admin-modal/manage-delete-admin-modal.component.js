(function(angular) {
    'use strict';

    function deleteAdminModalModalController($state, AdminManagerService) {
    var ctrl = this;
    ctrl.admin = (ctrl.resolve && ctrl.resolve.details) || {};

    ctrl.cancel = function(){
        ctrl.modalInstance.close();    
    };

    ctrl.deleteAdmin = function(){
                var params = JSON.stringify({
                          email: ctrl.admin
                      });   

                AdminManagerService.deleteAdmin(params)
                        .then(function(response) {
                            //TODO
                        })
                        .catch(function(err) {
                            console.log('Error in deleting Admin from admin list lists:');
                            console.log(err);
                        })
        }
       
    }

    angular.module('deleteAdminModal')
        .component('deleteAdminModal', {
            templateUrl: 'manage-admin/manage-delete-admin-modal/manage-delete-admin-modal.template.html',
            controller: ['$state','AdminManagerService', deleteAdminModalModalController],
            bindings: {
                modalInstance: '<',
                resolve: '<'
            }
        });

})(window.angular);