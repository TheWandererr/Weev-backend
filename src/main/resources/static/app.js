const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiI5OTEyMmU3Mi00YTY3LTRlMmItOTRhYy1hMWRiODk5ZTk1ZGUiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDgyMDQ2NTAsImlhdCI6MTcwODIwMTA1MCwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.XcgSokrjpnu-ft3HPBafNOSavZQByJUI36ocbSX5-jog5VZayDyke93Qto3xt5y2FoolsO3RkJ4Dk451xod5uEpcWpadxbY9kX4NjTOnpQlFHvTFvIGM2oNOItXuFmGA7dnPsidamsckrF8Ij53KtFNWQqo4Rsg4PY0YQY5460qyIhaqPEpOTOS2bkjGDm09J35jTqujqkD4O_AUw20N8BBLDmc9m5z0VEBr-YyhIZL_OpZybFADCCV4DgcHZRpEwRH2QHhhNKKkDr1IC7LWc7Ei_KFa_SbvrkoRJEfz1hUt1seBo5Ie_u6D9k2B6jtlJoKrCJ3CWGYlthFSYNryiA',
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
        destination: "/app/chats",
        body: JSON.stringify({'id': $("#name").val()})
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

