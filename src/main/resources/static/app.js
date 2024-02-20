const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiIzY2U0M2E1Yi00ZGJmLTRmYzgtOTdjMC1mOWM1ZjcxZWJhNWYiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDg0NjAxNTIsImlhdCI6MTcwODQ1NjU1MiwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.FIsHlAjJKaCHB-Ti-kXGgHwcv4zEfciz4wq4l5gH8nmmdLHZaEnHPeWjlzKS9cTvn80vT40TBtEPNHUDmAm3HHYG0KWeKmZwb8Cl4A9N95ywj8_mOkAUkrwwLGiG_DJCsnVHLB6l-C8Jo8HQ9GdwlQwfPG3Y0DSrVEHailjlU6kOL74HLvZd2zowFceNeRjGKmPEtuOMvS9-MOe1HnXRk4siGVsyyJwIdHl-BxgRBIFdxGrLcI6PR-dS3zubQ0I85oMSGpMlxOkz00jgp_h1xfjg8hldx5J5pod9sBPTDQkg9Am1rs3OjT6-rnbmDzDn2K_OEwp-FmTZ6vfec0s5BQ',
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

