(function(angular) {
'use strict';

function AdminLayoutController($state) {
	var ctrl = this;
}

angular.module('adminLayout')
.component('adminLayout',{
	templateUrl: 'admin/admin-layout/admin-layout.template.html',
	controller:['$state', AdminLayoutController]
});

})(window.angular);
