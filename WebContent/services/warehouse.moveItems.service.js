(function(angular) {

    "use strict";

    function WarehouseMoveItemService($http) {

        var itemArray = [];

        var moveItems = function(items) {
            itemArray = itemArray.concat(items);
        };

        var getMovedSavedItems = function() {
            return itemArray;
        };

        var getItemsByStatus = function(status){
            return $http({
                    url: '/rest/admin/getItemsByStatus?status='+ status,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var updateItemInWarehouse = function(storedItemID, location, status){
            return $http({
                    url: '/rest/admin/updateItemInWarehouse?storedItemID='+ storedItemID + '&status='+ status + '&location=' + location,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var updateDropItemStatus = function(storedItemID, location, status, subitemCode){
            return $http({
                    url: '/rest/admin/updateDropItemStatus?storedItemID='+ storedItemID + '&status='+ status + '&subitemCode=' +subitemCode+ '&location=' + location,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var outgoingItems = function(date){
            return $http({
                    url: '/rest/admin/outgoingItems?date='+ date,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var incomingItems = function(date){
            return $http({
                    url: '/rest/admin/incomingItems?date='+ date,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var checkoutItem = function(){
            var data = {
                itemCode: "BWG41107295",
                userRequestID: "246915e5-a7c4-49e9-a099-5704f3274670"
            }

            return $http({
                    url: '/rest/driver/checkoutItem',
                    method: "POST",
                    data: JSON.stringify(data),
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var checkInStoredItem = function(storedID, status, location, credit){
           // status = "RECEIVED";
            return $http({
                    url: '/rest/admin/checkInStoredItem?storedID='+ storedID + '&credits=' + credit + '&status='+ status + '&location=' + location,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        var updateDropItemStatus = function(storedID, status){
           // status = "RECEIVED";
            return $http({
                    url: '/rest/admin/updateDropItemStatus?storedItemID='+ storedID + '&status='+ status,
                    method: "GET",
                    headers:{
                        "Authorization": 'Basic YWRtaW46YWRtaW4='
                }
            });
        };

        


        return {

            moveItems,
            getMovedSavedItems,
            getItemsByStatus,
            updateItemInWarehouse,
            updateDropItemStatus,
            outgoingItems,
            incomingItems,
            checkoutItem,
            checkInStoredItem,
            updateDropItemStatus

        }

    }

    angular.module('bathwaterApp.services')
        .factory("warehouseMoveItemService", ['$http',WarehouseMoveItemService]);

})(window.angular);
