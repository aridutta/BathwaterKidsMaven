(function (angular) {
function AdminRightsService() {
	var rights={};
  var profile = {};

  function addRights(rightsList) {
      rights = angular.copy(rightsList);
   };

  function getRights(){
  	return rights;
  };

  function saveProfile(profileInfo){
    profile = angular.copy(profileInfo);
  };

  function getProfile(){
    return profile;
  };

  return {
  	addRights: addRights,
  	getRights: getRights,
    saveProfile: saveProfile,
    getProfile: getProfile
  }
}

angular.module('bathwaterApp.common').factory('AdminRightsService', [AdminRightsService]);
})(window.angular);