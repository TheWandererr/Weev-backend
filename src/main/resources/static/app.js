const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiIxYmY2OWQ2ZC1lMGYyLTQ1OGUtYTZmOS0zZTNmZDEzNGNmMTgiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDgyMTU1MDIsImlhdCI6MTcwODIxMTkwMiwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.FrNr_E3QP_Mbip7cxrutMGMOEh7W3uA4v4G1M1co6IAKH51OVgqL_5QBw2m8Xay7bizH2_eLpg-IwoThopNIqYRoFKER4CNv9PrPj-hm1kfKpMbZsTsjiphjEpklzI-Dl5Jz8p9bV0y6rGOyMktEKzAf84Ceh73rhoIz1QSIudrRG7kASWkMr5lTyBWDG0cPGRpR-EDuSZ1dYAz-lf2s2p8LXBX-HlSO4Rq_kgaSpRZhMGfGNJwISEO1Wdg6RGvkizZJPJhAI2XEsXs7U9ovhIVjRiwaUr90LebZ-961yDHhCuzeDfNhZ16Ap74t_09QTDeG2x_1XcWQYZE96NQxWQ',
        deviceId : '0000'
    }
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/user/topic/updates', (greeting) => {
        console.log(greeting);
        showGreeting(JSON.stringify(greeting.body));
    });
    stompClient.subscribe('/topic/chats.26', (hello) => {
        console.log(hello);
        showGreeting(JSON.stringify(hello.body));
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/chats.26",
        body: JSON.stringify({'text': $("#name").val()})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});

