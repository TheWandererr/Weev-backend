const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiIzZGEzZDljZi0yNDQ3LTQ2MjctYTljNy1hYWFiZjllNDlmYWEiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDg0NTUzMzIsImlhdCI6MTcwODQ1MTczMiwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.dNaz98jfNZ5_x2pm5rI3Ch3HenucrdY1RB-PXNWMezhMo2N6nufDApPwBEkbpN3X6K94nVv09sh5teKe96Ugk1sq4m9NKe__9sOIAM5OSU7v73fQBEVOue58G7e8xR9aUPnsZsGm-0tHamUDleOBH1sHDUdJFQI6SxSz9W6g_P1N8_ymb0EPCKzHF3PtOCz9xQZRC7GHxxjxYKsIAMDywk50uPl-b2HFYqscq6ulHNZLHXmuKr1D1MvaIGPDeO19LRuxv98lqM3cfH5ds4rtgtIM5W-GeexPcA5Krqyz-wcqizR6sFhpB6vGX2YqXtYg-g6DJ8z-dQgqWhHlEYQsag',
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
    stompClient.subscribe('/topic/chats.group$26', (hello) => {
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
        destination: "/app/chats.group$26.message",
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

