'use strict';

function GoogleSignInController($state,$interval, GAuth, AdminManagerService, AdminRightsService){

	var ctrl = this;
    ctrl.isSuperAdmin = false;
    ctrl.profile ={};
    var rights = {
         Admin: false,
         Pickup: false,
         Customers: false,
         Inventory: false,
         Warehouse: false
    };


	ctrl.init = function(){
		ctrl.login();
	};

	ctrl.login = function(){
		var CLIENT = angular.config.clientID;
	    GAuth.setClient(CLIENT);

	    var intervalId = $interval(function(){
		    GAuth.checkAuth().then(
		            function (profile) {
		            	$interval.cancel(intervalId);
                        ctrl.loginAdmin(profile);
	         });
		    	console && console.clear ? console.clear() : null;
			},1000);
		};

    ctrl.loginAdmin = function(profile){

            ctrl.profile = profile;

            AdminManagerService.loginAdmin(ctrl.profile.email , ctrl.profile.id)
                .then(function(response) {
                    if (response && response.data) {
                        ctrl.profile.role = response.data.role;
                        ctrl.profile.key = response.data.key;

                        switch(profile.role){
                            case "0":
                                rights.Pickup = true;
                                ctrl.AssignAdmin();
                                break;
                            case "1":
                                rights.Customers = true;
                                ctrl.AssignAdmin();
                                break;
                            case "2":
                                rights.Inventory = true;
                                ctrl.AssignAdmin();
                                break;
                            case "3":
                                rights.Warehouse = true;
                                ctrl.AssignAdmin();
                                break;
                            case "4":
                                rights.Admin = true;
                                ctrl.AssignAdmin();
                                break;
                            case "10":
                                ctrl.AssignSuperadmin();
                                break;
                        }
                    }
                })
                .catch(function(err) {
                    console.log('Error logging as Admin');
                    console.log(err);
                });
    };


    ctrl.AssignSuperadmin = function(){
                            ctrl.isSuperAdmin = true;
                            rights.Customers = true;
                            rights.Inventory = true;
                            rights.Warehouse = true;
                            rights.Admin = true;
                            AdminRightsService.saveProfile(ctrl.profile);
                            AdminRightsService.addRights(rights);
                            $state.go('manageAdmin');
                    };

     ctrl.AssignAdmin = function(){
                            AdminRightsService.saveProfile(ctrl.profile);
                            AdminRightsService.addRights(rights);
                            $state.go('index');
                    };
     ctrl.init();

        }

angular.module('googleSignIn')
.component('gSign',{
	templateUrl: 'google-sign-in/google-sign-in.template.html',
	controller: ['$state','$interval','GAuth','AdminManagerService','AdminRightsService', GoogleSignInController]
});