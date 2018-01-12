(function(angular) {


'use strict';

function customersSubPanelController($state) {
	var ctrl = this;
}

angular.module('customersSubPanel')
.component('customersSubPanel',{
	templateUrl: 'customers/customer-sub-panel/customer-sub-panel.template.html',
	controller:['$state', customersSubPanelController]
});
})(window.angular);