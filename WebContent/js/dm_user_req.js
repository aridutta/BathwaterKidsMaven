/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('deliveryManagementApp', []);
app.controller('deliveryManagementController', function ($scope, $http) {
    $http({
        url: "rest/getAllDrivers",
        method: "GET",
        headers: {
            'Authorization': "Basic YWRtaW46YWRtaW4="
        }
    })
            .then(function (response) {
                $scope.drivers = response.data;
            });
    $http({
        url: "rest/getUserTS",
        headers: {
            'Authorization': "Basic YWRtaW46YWRtaW4=",
            'Content-Type':'application/json'
        },
        method: "GET"
    }).then(function (response) {
        $scope.timeslots = response.data;
    });

    $scope.assignDriver = function (reqId, driverID) {
        $http({
            url: 'rest/assignDriverToUserRequest/' + driverID + '?userReqID=' + reqId,
            headers: {
                'Content-Type': 'text/plain',
                'Authorization': "Basic YWRtaW46YWRtaW4="
            },
            method: "GET"
        }).then(function () {
            window.location.reload();
        });
    };
});