/**
 *
 */
IGNITION_FRONT_APP.service('eventsService', ['$rootScope', function ($rootScope) {
    var srv = this;

    var eventListeners = [];

    srv.addEventListener = function (evtName, callback) {
        eventListeners.push({
            eventName: evtName,
            eventCallback: callback
        });
    };

    srv.removeEventListener = function (evtName, callback) {
        _.remove(eventListeners, function (elem) {
            return elem.eventName === evtName && elem.eventCallback === callback;
        });
    };

    srv.fireEventName = function (evtName, data) {
        _.forEach(eventListeners, function (listener) {
            if (listener.eventName === evtName) {
                listener.eventCallback(data);
            }
        });
    };

}]);
