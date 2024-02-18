const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        authorization: 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXJ0ZW0yMDEzQHlhbmRleC5ieSIsImF1ZCI6Ii9hcGkiLCJzZXJpYWwiOiI3OTcyYzNjOS1mOTE3LTQ1NWYtYWMzMC01N2EwYjY3NjI2MGIiLCJzY29wZSI6InJlYWQgd3JpdGUgbW9kZXJhdGlvbiIsImlzcyI6Ii9hdXRoL2xvZ2luIiwibmlja25hbWUiOiJib2IyMjgiLCJleHAiOjE3MDgyNTU1OTEsImlhdCI6MTcwODI1MTk5MSwiZGV2aWNlSWQiOiIwMDAwIiwidXNlcklkIjo5OTl9.j3sMY37Y7va5KuytOc7W1r8l3jyk5XGPQ2L07VlH6xRZatZn7xisOH2fnHYk--KfPflfgLDvpyxpCamIH9BklJKNdS9v5QpZFZwwVDEmPQjdtE70FYDozyHqA0F4JzshHPdR-DRM0s3wJh3TUvocF74R3MHCVs-0ldOHWcaNc-I4A00mjDpuIeGbkQDlMS85POmncfJArMTiWhdc6eRzqVPMHKJzdzIPP9SbN5gM8lKBTbarJkjWG9nnctpC-SA-cy3P5RHSSxWP3xsYr4s06dMBtfxViNksMRG_pOiueQpD_D6YRA3mKPSPJI_Fk3BELO6imnZHQvCCAaY77Qus4g',
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

