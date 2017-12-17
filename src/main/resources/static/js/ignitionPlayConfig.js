/**
 *
 */
IGNITION_FRONT_APP.value('ignitionPlayConfig', {
    ignitionCfgSnippetLength: 0,
    ignitionCfgSnippetsNum: 0,
    ignitionCfgVolumePercent: 0,
    getIgnitionCfgSnippetLengthMs: function () {
        return (this.ignitionCfgSnippetLength * 1000);
    },
    getIgnitionCfgSnippetsNum: function () {
        return (this.ignitionCfgSnippetsNum);
    },
    getIgnitionCfgPlaybackVolume: function () {
        return (this.ignitionCfgVolumePercent / 100);
    }
});