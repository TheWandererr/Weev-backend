const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiJjYjlhZjZhYy0xNDVmLTRiYzAtOWQyYi05NDE1M2VhY2VjMDEiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDg1NDI4ODUsImlhdCI6MTcwODUzOTI4NSwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.eyt3xUmTA1bX_nXiznRQ_Ew-KWBTJoP2YZEFIm6T9gvTPWaNHrPJfBBV_AT1PIdvjziYICg6VTW5DzGmSxhtSTjTKDS-Jmf-iTPC4bROe4gi5QqDDnUNLcF9x4zWzr9z188QwejKIFmYpDcSq7muYc_WBHfauyZZCa1mbmdqA6dxnWsDCogSQP9muaXmxnOM1uWMghLFnqT-pTI-EAf9X7OUBRpYI3_Xyq8j7o6pzbGZ8_6B4xh-hzcKuliTxEsGkVF6YtL1-zZK_g5PyatZOGi152bWtsNI79nkoAs9BioJ9ME-11UxZ7th0DaCFwyU7v1Ii-Cku0LR3fudQIgQTQ',
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

