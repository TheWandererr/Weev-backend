const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiI3Y2FmZGM5Ny04YTdkLTQ3MmYtOGRmOS0wMTBhOTAyNDBjMjciLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDgzNjk1MDQsImlhdCI6MTcwODM2NTkwNCwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.XQGOvPYuawnTR3--njVAZ3MbFe4Lxeo5JpDQGUHTgwlqVAwEo5UuMh_bOnxWL0HM1gNkw4mioltf9sLEKltgOYDHqh_JzHLkKukXk-k3vK9zX1LGrWVTwngE5_A5jJSPPqGNg8RRLp9TxY746UltWklUm4mA1hQhO4BBiSnG8iAu1v0FWQ5JWEWBCUHdIWQRcC5HvLl7_v6ALU0f3YAz1YRlOVl_xHkG2BF8a_TJZsGaiQ8KmzPl-yKWoT_rMTaYNj-eAil9CR1DDLLa96xuUPLSuq_h7QDiRe2f338BMQE4Yme-w2wVlxAt0rW40EdDaDUkzVyjg4VOvU2bAbk7Qw',
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
        destination: "/app/chats.group$26",
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

