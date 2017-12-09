/**
 * See http://jmesnil.net/stomp-websocket/doc/
 */
IGNITION_FRONT_APP.service('webSocketService', ['$rootScope', '$timeout', '$q', 'eventsService', function ($rootScope, $timeout, $q, eventsService) {
    var srv = this;

    var RECONNECT_TIMEOUT_MS = _.random(5 * 1000, 100 * 1000);
    var DISCONNECT_ACKNOWLEDGE_TIMEOUT_MS = 5 * 1000;

    var SUBSCRIBE_TOPIC = '/mktWsApp/session/setup';

    srv.WebSocketDestination = {
        SERVER_AVAILABILITY_EVT: "SERVER_AVAILABILITY_EVT",
        WebSocketTopics: {
            COMPANY_AVAILABILITY_TOPIC: '/topic/companyAvailability'
        },
        WebSocketQueues: {
            MESSAGING_THREAD_UPDATE_QUEUE: '/mktWsUser/queue/threadUpdated'
        },
        WebSocketMessage: {
            USER_TYPING_MESSAGE: '/mktWsApp/userTypingWebSockMsg'
        }
    };

    var stompClient = null;
    var isStompConnected = false;
    var sessionUserName = null;
    var sessionCompanyId = null;

    srv.initializeWebSocketConnection = function () {
        stompConnect();
    };

    srv.addWebSocketTopicListener = function (topicName, callback) {
        if (!topicName || !callback) {
            throw "Unable to subscribe WebSocket: not enough data."
        }
        eventsService.addEventListener(topicName, callback);
        console.info("addWebSocketTopicListener: subscribed for: " + topicName);
    };

    srv.removeWebSocketTopicListener = function (topicName, callback) {
        if (!topicName || !callback) {
            throw "Unable to unSubscribe WebSocket: not enough data."
        }
        eventsService.removeEventListener(topicName, callback);
        console.info("removeWebSocketTopicListener: unSubscribed for: " + topicName);
    };

    srv.publishWebSocketMessage = function (destination, dto) {
        stompClient.send(destination, {}, JSON.stringify(dto));
    };

    $(window).bind('beforeunload', function (e) {
        disconnectStompClient();
    });

    //
    //
    //

    function stompConnect() {
        var socket = new SockJS("/SockJS/stomp-endpoint");
        stompClient = Stomp.over(socket);
        stompClient.debug = function (msg) {
            // Uncomment for debugging
            // console.info(msg);
        };

        stompClient.heartbeat.outgoing = 0; // client will send heartbeats every 20000ms
        stompClient.heartbeat.incoming = 0; // Set 0 if client does not want to receive heartbeats from the server

        stompClient.connect({}, connectCallback, errorCallback);
    }

    function connectCallback(frame) {
        isStompConnected = true;
        var mappedFrame = mapConnectionFrame(frame);
        sessionUserName = mappedFrame.wsConnectedAsUserName;

        _.forOwn(srv.WebSocketDestination.WebSocketTopics, function (value, key) {
            subscribeForNotifications(value, function (webSockMessage) {
                var payloadBodyDto = JSON.parse(webSockMessage['body']);
                fireWebSocketEvent(value, payloadBodyDto)
            });
        });

        _.forOwn(srv.WebSocketDestination.WebSocketQueues, function (value, key) {
            subscribeForNotifications(value, function (webSockMessage) {
                var payloadBodyDto = JSON.parse(webSockMessage['body']);
                fireWebSocketEvent(value, payloadBodyDto)
            });
        });

        notifyConnected();
        console.info("spaWebSocketService: connected as user name: " + sessionUserName);

        if (!sessionUserName) {
            console.info("spaWebSocketService: no WS subscriptions for anon users. Disconnecting.");
            disconnectStompClient();
            return;
        }

        setupWsConnectionSession();
    }

    function disconnectStompClient() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    function setupWsConnectionSession() {
        var sessionSetupSubs = stompClient.subscribe(SUBSCRIBE_TOPIC, function (payload) {
            var SetupWebSocketSessionSettingsDto = JSON.parse(payload.body);
            var sessionCompanyId = SetupWebSocketSessionSettingsDto['webSocketSessionCompanyIdIfAny'];

            console.info("spaWebSocketService: sessionSetup for companyId: " + sessionCompanyId);
        });

        //console.info("spaWebSocketService: session setup subscription: " + sessionSetupSubs['id']);
    }

    function subscribeForNotifications(topicName, handlerFn) {
        stompClient.subscribe(topicName, handlerFn);
    }

    function errorCallback(error) {
        console.warn("spaWebSocketService: communication error: " + error);
        var origStompConnected = isStompConnected;
        isStompConnected = false;
        sessionUserName = null;
        sessionCompanyId = null;

        // If went from connected to disconnected
        if (origStompConnected && !isStompConnected) {
            notifyDisconnected();
        }

        $timeout(function () {
            console.info("spaWebSocketService: attempting to reconnect... ");
            stompConnect();
        }, RECONNECT_TIMEOUT_MS);
    }

    function mapConnectionFrame(frame) {
        return {
            wsConnectedAsUserName: frame['headers']['user-name']
        };
    }

    function notifyConnected() {
        fireWebSocketEvent(srv.WebSocketDestination.SERVER_AVAILABILITY_EVT, {
            serverAvailEvtIsWentOnline: true
        });
    }

    function notifyDisconnected() {

        // Notify subscribers about communications problems after timeout as disconnection may be temporary
        $timeout(function () {
            if (!isStompConnected) {
                fireWebSocketEvent(srv.WebSocketDestination.SERVER_AVAILABILITY_EVT, {
                    serverAvailEvtIsWentOnline: false
                });
            }
        }, DISCONNECT_ACKNOWLEDGE_TIMEOUT_MS);
    }

    function fireWebSocketEvent(topicName, payload) {

        eventsService.fireEventName(topicName, payload);
        $timeout(function () {
            $rootScope.$apply();
        });
    }

}]);
