/**
 *
 */
IGNITION_FRONT_APP.controller('ignitionFrontMainCtrl', ['$scope', '$timeout', 'ignitionFrontDas', 'ignitionAvailRecsSrv','webSocketService', function ($scope, $timeout, ignitionFrontDas, ignitionAvailRecsSrv, webSocketService) {
    var vm = this;


    webSocketService.initializeWebSocketConnection();

    /*webSocketService.addWebSocketTopicListener(spaWebSocketService.WebSocketDestination.WebSocketQueues.MESSAGING_THREAD_UPDATE_QUEUE, function (AbstractThreadUpdateRevDto) {

    });*/


    vm.initIgnitionFront = function () {
        init();
    };

   /* vm.onPlaySelectedSong = function (song) {
        var songId = song.ignitionAvailSongId;
        playSongFragment(songId);
    };*/





  /*  function startPlayingSongsFromIndex(idx) {

        vm.currentPlayingRecordingIdx = idx;

        var firstSong = vm.availableMappedSongsList[idx];

        var audio = playSongFragment(firstSong.ignitionAvailSongId);
        audio.addEventListener('ended', function () {
            var nextIdx = (idx + 1) < vm.availableMappedSongsList.length ? idx + 1 : 0;
            startPlayingSongsFromIndex(nextIdx);
        }, true);
    }*/

    function init() {
        ignitionFrontDas.getIgnitionServerVersion().then(function (AboutIgnitionDto) {
            var mappedAboutInfo = ignitionFrontDas.mapAboutIgnitionDto(AboutIgnitionDto);
            console.info("Server version: " + mappedAboutInfo.ignitionVersion);
        });
    }

}]);