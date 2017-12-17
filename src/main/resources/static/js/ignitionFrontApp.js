/**
 *
 */
var IGNITION_FRONT_APP = angular.module('ignitionFrontApp', ['pascalprecht.translate']);

/**
 *
 */
IGNITION_FRONT_APP.config(function ($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});

/**
 *
 */
IGNITION_FRONT_APP.config(['$translateProvider', function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'json/resources_',
        suffix: '.json'
    });
    $translateProvider.preferredLanguage('en');
}]);
