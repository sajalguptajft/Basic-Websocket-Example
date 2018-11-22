<html>
<head>
	<meta name="layout" content="main">
	<title>WebSocket Example</title>
	<style type="text/css">
        #chatroom {
            padding: 10px;
        }
        #newMessage {
            width: 400px;
            padding: 10px;
        }
        #messageList {
            width: 400px;
            height: 400px;
            border: 1px solid #ddd;
            overflow: auto;
            padding: 10px;
        }
        .right {
            text-align: right;
            width: 100%;
        }
        .left {
            text-align: left;
            width: 100%;
        }
        li {
            display: block !important;
        }
	</style>
</head>
<body>
<div id="chatroom">
    <div id="messageList">
        <g:each in="${previousChats}">
            <g:if test="${it.user?.id==1}">
                <li class="right">${it.user?.username} : ${it.message}</li>
            </g:if>
            <g:else>
                <li class="left">${it.user?.username} : ${it.message}</li>
            </g:else>
        </g:each>
    </div><br>
    <input type="text" id="newMessage" placeholder="Type your message here"><br>
    <button id="sendMessage">Send</button>
</div>
<script type="text/javascript">
    $(document).ready(function () {

        let log = $("#messageList"),
            message = $("#newMessage"),
            sendMessageButton = $("#sendMessage"),
            webSocketUrl = "${createLink(uri: '/chat/1', absolute: true).replaceFirst(/http/, /ws/)}",
            socket = new WebSocket(webSocketUrl),
            moreLoading = false;

        socket.onopen = function () {
            $("#messageList").scrollTop($('#messageList')[0].scrollHeight);
        }

        socket.onmessage = function (message) {
            log.append("<li class='right'>Sajal : " + message.data + "</li>");
            $("#messageList").scrollTop($('#messageList')[0].scrollHeight);
        };

        sendMessageButton.on('click', function () {
            let text = message.val();
            if ($.trim(text) !== '') {
                socket.send(text);
                message.val("");
            }
            message.focus();
        });

        $("#newMessage").keyup(function(event){
            if(event.keyCode==13) {
                sendMessageButton.click();
            }
        });

        $('#messageList').scroll(function() {
            var pos = $('#messageList').scrollTop();
            if (pos < 200 && $('#messageList')[0].scrollHeight > 400 && moreLoading == false) {
                alert('withen 200px of top.');
                loadMorePreviousChats();
                moreLoading = true;
            }
        });

        function loadMorePreviousChats () {
            setTimeout(function () {
                moreLoading = false;
            }, 3000)
        }
    });
</script>
</body>
</html>