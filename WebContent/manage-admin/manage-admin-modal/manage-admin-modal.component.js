(function(angular) {
    'use strict';

    function addAdminModalModalController($state, AdminManagerService) {
        var ctrl = this;
        ctrl.admin = (ctrl.resolve && ctrl.resolve.details) || {};
        ctrl.isEdited = Object.keys(ctrl.admin).length > 0;
        ctrl.role = "";
        ctrl.params = {};

        ctrl.init = function(){
            
            switch(ctrl.admin.role){
            case "0":
                ctrl.admin.Pickup = true;
                break;
            case "1":
                ctrl.admin.Customers = true;
                break;
            case "2":
                ctrl.admin.Inventory = true;
                break;
            case "3":
                ctrl.admin.Warehouse = true;
                break;
            case "4":
                ctrl.admin.Admin = true;
                break;
            case "10":
                ctrl.admin.SuperAdmin = true;
                break;
        };

        };
        
       // Add Admin
        ctrl.saveAdmin = function() {  

            ctrl.params = ctrl.roleAndrights(); 

            AdminManagerService.addAdmin(ctrl.params)
                .then(function(response) {
                    if (response && response.data) {
                        ctrl.modalInstance.close({action: 'update'});
                    }
                })
                .catch(function(err) {
                    console.log('Error getting Admin lists:');
                    console.log(err);
                })
        };

        ctrl.cancel = function() {
            ctrl.modalInstance.close();
        };

                
        ctrl.updateAdmin = function() {
            //Edit Admin
            ctrl.params = ctrl.roleAndrights(); 

            AdminManagerService.editAdmin(ctrl.params)
                .then(function(response) {
                    if (response && response.data) {
                        ctrl.modalInstance.close({action: 'update'});
                    }
                })
                .catch(function(err) {
                    console.log('Error getting Admin lists:');
                    console.log(err);
                })

        };

         ctrl.roleAndrights = function(){

                    if(ctrl.admin.SuperAdmin)
                        ctrl.role = "10";
                    else if(ctrl.admin.Admin)
                        ctrl.role = "4";
                    else if(ctrl.admin.Warehouse)
                        ctrl.role = "3";
                    else if(ctrl.admin.Inventory)
                        ctrl.role = "2";
                    else if(ctrl.admin.Customers)
                        ctrl.role = "1";
                    else if(ctrl.admin.Pickup)
                        ctrl.role = "0";
                    else if(ctrl.admin.Owner)
                        ctrl.role = "0";
                    
                    ctrl.params = JSON.stringify({
                                  email: ctrl.admin.email,
                                  role: ctrl.role
                              });

                    return ctrl.params;
            }; 
        ctrl.init();   
    }

    angular.module('addAdminModal')
        .component('addAdminModal', {
            templateUrl: 'manage-admin/manage-admin-modal/manage-admin-modal.template.html',
            controller: ['$state','AdminManagerService', addAdminModalModalController],
            bindings: {
                modalInstance: '<',
                resolve: '<'
            }
        });

})(window.angular);