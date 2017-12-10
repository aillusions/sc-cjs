/**
 *
 */
IGNITION_FRONT_APP.controller('ignitionFrontMainCtrl', ['$scope', '$timeout', 'ignitionFrontDas', 'ignitionAvailRecsSrv', 'webSocketService', function ($scope, $timeout, ignitionFrontDas, ignitionAvailRecsSrv, webSocketService) {
    var vm = this;


    webSocketService.initializeWebSocketConnection();

    webSocketService.addWebSocketConnectionListener(function (payload) {
        console.info("Server available: " + payload.isServerOnline);

        if (payload.isServerOnline) {

            webSocketService.addWebSocketTopicListener(webSocketService.WebSocketDest.WebSocketTopics.GREETINGS_TOPIC, function (payload) {
                console.info("Greeting ws notification: " + (payload.content || payload.testField));
            });

            $timeout(function () {
                webSocketService.publishWebSocketMessage(webSocketService.WebSocketDest.WebSocketMessage.GREETING_MESSAGE, {
                    name: "Vasia"
                });
            }, 1000);
        }

    });

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
          audio.addAppEventListener('ended', function () {
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