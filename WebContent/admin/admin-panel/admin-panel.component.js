(function(angular) {

'use strict';

function AdminPanelController($state) {
	var ctrl = this;
}

angular.module('adminPanel')
.component('adminPanel',{
	templateUrl: 'admin/admin-panel/admin-panel.template.html',
	controller:['$state', AdminPanelController]
});

})(window.angular);