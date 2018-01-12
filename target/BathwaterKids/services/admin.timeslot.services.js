(function(angular) {
	"use strict";
	
	function TimeslotServiceHandler($http){

		var getTimeslotsForTheWeek = function(){
			return $http({
						    url: '/rest/getTimeslotsForTheWeek',
						    method: "GET",
						    headers:{
						    	"Authorization": 'Basic YWRtaW46YWRtaW4='
						    }
						});
					};

		var createTimeSlotsRange = function(timeslot){
			
			return $http({
						url: '/rest/createTimeSlotsRange',
			            method: "POST",
			            data: timeslot,
			            transformRequest: function(obj) {
					        var str = [];

					        //EXTRA CODE TO MANAGE EXISTING APIS WORK
					        //Add days
					        for(var day in obj.days){
					        	obj.days[day] ? str.push(encodeURIComponent("days") + "=" + encodeURIComponent(day)) : null;
					        }
					        delete obj.days;

					        //Add timeslots
					        for(var timeslot in obj.timeslots){
					        	obj.timeslots[timeslot] ? str.push(encodeURIComponent("timeslots") + "=" + encodeURIComponent(timeslot[timeslot.length - 1])) : null;
					        }
					        delete obj.timeslots;

					        //Add availables
					        for(var index =1; index <=6; index++){
					        	if(obj.availables["s"+index]){
					        		str.push(encodeURIComponent("available") + "=" + encodeURIComponent(obj.availables["s"+index]));
						        }
						        else{
						        	str.push(encodeURIComponent("available") + "=");
						        }
					        }
					        delete obj.availables;

					        for(var p in obj){
					        	str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
					        }
					        return str.join("&");
					    },
			            headers: {
			                "Authorization": "Basic YWRtaW46YWRtaW4=",
			                "Content-Type": "application/x-www-form-urlencoded"
			            }
				});
			};

		var getTimeslots = function(){
			return $http({
					        url: '/rest/getTimeslots',
					        method: "GET",
					        headers: {
					            'Authorization': "Basic YWRtaW46YWRtaW4="
					        }
    					});
					}
		//EXPORTED Object
		return {
			getTimeslotsForTheWeek,
			createTimeSlotsRange,
			getTimeslots
		}
	}

	angular.module('bathwaterApp.services')
		.factory('TimeslotService',['$http', TimeslotServiceHandler]);	

})(window.angular);