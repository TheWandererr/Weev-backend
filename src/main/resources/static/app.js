const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiI0NjY0YjI1MC1kMzQ3LTRmNDUtODk4Yi0xNTk4ZjgzYWZlNWUiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDgyNjc0NzYsImlhdCI6MTcwODI2Mzg3NiwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.l2j0-MfwQQ6TI8TsCo5Cc9DOw6039orIC_fGKPhla0cBoknQfWOoLM5gw-3rIzp5-8Va1MpTdjhys3d5FBgbxhwNx7A-NEKFA9dS8930SDgpNzNbX5Z10b2f6kZi3ec4kM4kaLqT5bwEJLoxGvMrqsvh24dvJlVIfk3A-MZZ3q_48uUxNwavHr_UPOg4_uosrpJ4qTgELHY5cP1EH8PyhCEFF0DiOP-bB2IKbIgeWEWDY8WoRG6HdfSbXQHBth3k_g3kaa_XJ8P1xmruclxRsdJnT5KVqTGpZyZulehE8BW2nBcm41QI1K_ZEdBtJBy0rF62HKD8PNZyK-MvXwXUCQ',
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

