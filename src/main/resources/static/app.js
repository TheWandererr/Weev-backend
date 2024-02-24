const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJib2IyMjkiLCJhdWQiOiIvYXBpIiwic2VyaWFsIjoiYmZkYzM3NDctNjc2Mi00ZDZhLThkNmItMzRhNDM5OTVhMGMwIiwic2NvcGUiOiJyZWFkIHdyaXRlIiwiaXNzIjoiL2F1dGgvbG9naW4iLCJuaWNrbmFtZSI6ImJvYjIyOSIsImV4cCI6MTcwODc2OTIxNCwiaWF0IjoxNzA4NzY1NjE0LCJkZXZpY2VJZCI6IjAwMDAiLCJ1c2VySWQiOjR9.al0eDDmuiN-5pkqM8LLzyHeJPhs6I70BzG1kK-096N77XX71ZXTIVI8J4fnQITJEVqDJgoj0o9WuptxB-Z54Aqf4IjhrjEx41xDe829pFKbqBTkX5PKHmoIf5XRMyCRKiYv59K5ffojJWI-pby2rWJJCdVIZC88uZo8yYClr0s0b9Rn0cT_pqK9SCq8I1rY3TNiC9k6kTLVlML5urQ-8fQpsR4VlSqCYOUCUM9qYyhdgo7MxDTZF6ESBKk87hoRRuox2usiFfq23uP88kYNzuWPwEfeNYAGid30bIm49eIUh5PgmglHKMVrr4SqoIBWSUB24Pq_Q240C9k-mTIq1mw',
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
    stompClient.subscribe('/topic/chats.group$999', (hello) => {
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
        destination: "/app/chats.group$999.message",
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

