/**
 *
 */
IGNITION_FRONT_APP.controller('ignitionPlayCtrl', ['$scope', '$timeout', 'ignitionFrontDas', 'ignitionPlayConfig', 'ignitionAvailRecsSrv', function ($scope, $timeout, ignitionFrontDas, ignitionPlayConfig, ignitionAvailRecsSrv) {
    var vm = this;

    vm.ignitionPlayPMA = {
        isPlayingAll: false
    };

    var currentlyPlayingRec = {
        currentRecordingIdx: 0,
        currentRecordingFragIdx: -1
    };

    var CURRENT_PLAYING_AUDIO;

    $scope.$watch(function () {
            return ignitionPlayConfig;
        },
        function (newVal, oldVal) {
            $timeout(function () {
                if (CURRENT_PLAYING_AUDIO) {
                    configureVolume(CURRENT_PLAYING_AUDIO);
                }
            });

        }, true);

    vm.onPlayAllAvailableSongs = function () {
        vm.ignitionPlayPMA.isPlayingAll = true;
        findAndPlayNextRecording();
    };

    vm.onPausePlayingAllAvailableSongs = function () {
        vm.ignitionPlayPMA.isPlayingAll = false;
        if (CURRENT_PLAYING_AUDIO) {
            CURRENT_PLAYING_AUDIO.pause();
        }
    };

    vm.onSkipPlayingCurrentSong = function () {

        var currRecIdx = currentlyPlayingRec.currentRecordingIdx;
        var nextRecIdx = (currRecIdx + 1) < ignitionAvailRecsSrv.availableMappedSongsList.length ? currRecIdx + 1 : 0;

        currentlyPlayingRec = {
            currentRecordingIdx: nextRecIdx,
            currentRecordingFragIdx: -1
        };

        CURRENT_PLAYING_AUDIO.isSkipped = true;

        findAndPlayNextRecording();
    };


    ignitionAvailRecsSrv.initAvailRecsSrv();

    //
    //
    //

    function findAndPlayNextRecording() {

        findNextPlayingRec();

        var songByIdx = ignitionAvailRecsSrv.availableMappedSongsList[currentlyPlayingRec.currentRecordingIdx];
        var fragIdx = currentlyPlayingRec.currentRecordingFragIdx;

        var audio = playSongFragment(songByIdx.ignitionAvailSongId, fragIdx);

        audio.addAppEventListener('playing', function () {

            if (CURRENT_PLAYING_AUDIO) {
                CURRENT_PLAYING_AUDIO.pause();
            }

            CURRENT_PLAYING_AUDIO = audio;

            setTimeout(function () {
                if (vm.ignitionPlayPMA.isPlayingAll && !audio.isSkipped) {
                    findAndPlayNextRecording();
                }
            }, ignitionPlayConfig.getIgnitionCfgSnippetLengthMs());

        }, true);

        /*audio.addAppEventListener('ended', function () {
            vm.findAndPlayNextRecording();
        }, true);*/
    }

    function findNextPlayingRec() {

        var currRecIdx = currentlyPlayingRec.currentRecordingIdx;
        var currFragIdx = currentlyPlayingRec.currentRecordingFragIdx;

        var nextRecIdx;
        var nextFragIdx;

        if (currFragIdx >= (ignitionPlayConfig.getIgnitionCfgSnippetsNum() - 1)) {
            nextRecIdx = (currRecIdx + 1) < ignitionAvailRecsSrv.availableMappedSongsList.length ? currRecIdx + 1 : 0;
            nextFragIdx = 0;
        } else {
            nextRecIdx = currRecIdx;
            nextFragIdx = currFragIdx + 1;
        }

        currentlyPlayingRec.currentRecordingIdx = nextRecIdx;
        currentlyPlayingRec.currentRecordingFragIdx = nextFragIdx;
    }

    function playSongFragment(songId, fragId) {

        var uri = '/ignition/rest/play/' + songId + "/" + fragId;

        var audio = new Audio();
        configureVolume(audio);
        audio.loop = false;
        audio.src = uri;
        audio.play();

        console.info("Playing recording: " + songId);

        return audio;
    }

    function configureVolume(audio) {
        audio.volume = ignitionPlayConfig.getIgnitionCfgPlaybackVolume();
    }

}]);