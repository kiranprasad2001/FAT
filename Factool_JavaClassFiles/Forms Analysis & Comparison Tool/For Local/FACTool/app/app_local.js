"use strict";
var myApp = angular.module('myApp', []);

var isChrome = !!window.chrome && !!window.chrome.webstore;
	if (isChrome)
	{
	    alert("Please use Internet Explorer Browser, This application is not supported in Chrome");
	}
	
	$( '#list' ).on( 'click', '.video_trigger', function () {
		$( this ).siblings( 'video' ).show();
	});

myApp.controller('mainController', ['$scope',  function ($scope) {

	$scope.homeFlag 		= true;
	$scope.formCompFlag 	= false;
	$scope.formAnarepFlag 	= false;
	$scope.sectionFlag 		= false;
	$scope.grpFlag 			= false;
	$scope.imgFlag 			= false;
	$scope.xddFlag 			= false;
	$scope.helpFlag 		= false;
	$scope.videoFlag 		= false;

	$scope.showDiv = function (value) {
		$scope.homeFlag 	= (value=='homeFlag');
		$scope.formCompFlag 	= (value=='formCompFlag');
		$scope.formAnarepFlag 	= (value=='formAnarepFlag');
		$scope.sectionFlag 		= (value=='sectionFlag');
		$scope.grpFlag 			= (value=='grpFlag');
		$scope.imgFlag 			= (value=='imgFlag');
		$scope.xddFlag 			= (value=='xddFlag');
		$scope.helpFlag 		= (value=='helpFlag');
		$scope.videoFlag 		= (value=='videoFlag');
	};


    // change the batch path here eg. 'D:/Tools/Forms Analysis & Comparison Tool/Batch File/'
    // $scope.batchpath = 'D:\Learning\CCM KT\Oracle Documaker\Forms Analysis & Comparison Tool';
	$scope.batchpath = 'D:/Development_Avecto/FACTool/';
    //change the working directory here eg. 'D:/Tools/Forms Analysis & Comparison Tool/'
	$scope.workingdir = 'D:/Forms Analysis & Comparison Tool/';
    $scope.execute = function(name) {
		window.open('file:///' + $scope.batchpath +'Batch File/'+ name);
    };
  	
}]);









