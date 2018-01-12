'use strict';

function InventorySubPanelController($state) {
	var ctrl = this;
}

angular.module('inventorySubPanel')
.component('inventorySubPanel',{
	templateUrl: 'inventory/inventory-sub-panel/inventory-sub-panel.template.html',
	controller:['$state', InventorySubPanelController]
});