/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var driversApp = angular.module('junitApp', []);
driversApp.controller('junitController', function ($scope, $http, $timeout) {
    $scope.testCases = 0;
    $scope.successPercentage = 0;
    $http({
        url: 'rest/startTestCases',
        method: "GET",
        headers: {
            'Authorization': "Basic YWRtaW46YWRtaW4="
        }
    }).then(function (response) {
        $scope.testCases = response.data.testCases;
        $scope.keyName = response.data.keyName;
        $timeout($scope.intevalFunction(), 8000);
    });

    $scope.runTime = 0;

    $scope.move = function () {
        var elem = document.getElementById("myBar");
        elem.style.width = $scope.successPercentage + '%';
    };

    $scope.intevalFunction = function () {
        var urlString = 'rest/getTestResultProgress?keyName=' + $scope.keyName;
        $http({
            url: urlString,
            method: "GET",
            headers: {
                'Authorization': "Basic YWRtaW46YWRtaW4="
            }
        }).then(function (response) {
            $scope.runCount = response.data.runCount;
            $scope.successPercentage = ($scope.runCount / $scope.testCases) * 100;
            $scope.move();
            if (response.data.runTime) {
                $scope.runTime = response.data.runTime;
            } else {
                $timeout($scope.intevalFunction(), 8000);
            }
        });
    };


});