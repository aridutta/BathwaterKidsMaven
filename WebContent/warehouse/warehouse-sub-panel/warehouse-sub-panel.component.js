'use strict';

function WarehouseController($state) {
	var ctrl = this;
}

angular.module('warehouseSubPanel')
.component('warehouseSubPanel',{
	templateUrl: 'warehouse/warehouse-sub-panel/warehouse-sub-panel.template.html',
	controller:['$state', WarehouseController]
});