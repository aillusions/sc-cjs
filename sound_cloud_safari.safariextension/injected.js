/**
 *
 */

//var IGNITION_SERVER_URL = "https://localhost/ignition/rest/song/new";
var IGNITION_SERVER_URL = "https://192.168.1.104//ignition/rest/song/new";

var SONG_DATA = getEmptySongData();

function sendDataToServerIf() {
    if (SONG_DATA.foundSongUrl != null
        && SONG_DATA.foundSongTitle != null
        && SONG_DATA.foundSongPlaylistUrl != null) {

        sendDataToServer(SONG_DATA);

        SONG_DATA = getEmptySongData();
    }
}

function sendDataToServer(songData) {

    var songTitle = songData.foundSongTitle;

    $.ajax({
        type: 'POST',
        url: IGNITION_SERVER_URL,
        async: true,
        data: JSON.stringify({
            scCjsSongTitle: songData.foundSongTitle,
            scCjsSongUrl: songData.foundSongUrl,
            scCjsSongPlayListUrl: songData.foundSongPlaylistUrl
        }),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        beforeSend: function () {

        },
        success: function (AbstractResponse) {
            logInfo("Sent: " + songTitle);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            logInfo("Sending filed: " + songTitle + " " + thrownError);
        },
        complete: function () {

        },
        contentType: "application/json; charset=utf-8",
        cache: false,
        dataType: 'json',
        timeout: 10 * 1000 // 10 sec
    });
}

function registerRequestInterceptor(callBack) {

    var open = window.XMLHttpRequest.prototype.open,
        send = window.XMLHttpRequest.prototype.send,
        onReadyStateChange;

    function openReplacement(method, url, async, user, password) {
        try {
            callBack(url);
        } catch (err) {
            console.error(err);
        }
        return open.apply(this, arguments);
    }

    function sendReplacement(data) {
        // console.warn('Sending HTTP request data : ', data);

        if (this.onreadystatechange) {
            this._onreadystatechange = this.onreadystatechange;
        }
        this.onreadystatechange = onReadyStateChangeReplacement;

        return send.apply(this, arguments);
    }

    function onReadyStateChangeReplacement() {
        // console.warn('HTTP request ready state changed : ' + this.readyState);
        if (this._onreadystatechange) {
            return this._onreadystatechange.apply(this, arguments);
        }
    }

    window.XMLHttpRequest.prototype.open = openReplacement;
    window.XMLHttpRequest.prototype.send = sendReplacement;
}

function onNewPlayListLoaded(playListUrl) {
    SONG_DATA.foundSongPlaylistUrl = playListUrl;
    sendDataToServerIf();
}

function onSongTitleChanged(songURL, songTitle) {
    SONG_DATA.foundSongUrl = songURL;
    SONG_DATA.foundSongTitle = songTitle;
    sendDataToServerIf();
}

function onXhrRequest(url) {
    if (url.indexOf("m3u") > -1) {
        onNewPlayListLoaded(url);
    }
}

function watchSongTitle() {
    setInterval(function () {

        var songDiv = $('div.playbackSoundBadge__title');
        var songLink = songDiv.find('a');

        var songTitle = songLink.attr('title');
        var songUri = songLink.attr('href');

        var songURL = 'https://soundcloud.com' + songUri;

        if (SONG_DATA.foundSongUrl != songURL) {
            onSongTitleChanged(songURL, songTitle);
        }
    }, 50);
}

function init() {
    logInfo("ignition script init.");
    watchSongTitle();
    registerRequestInterceptor(onXhrRequest);
}

function getEmptySongData() {
    return {
        foundSongUrl: null,
        foundSongTitle: null,
        foundSongPlaylistUrl: null
    };
}

function logInfo(message) {
    console.info("------> " + message);
}

init();