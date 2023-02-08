1 서버에 웹소켓옵션이 설정되지 않았을 경우 에이전트 로그에서 확인되는 예외

     Connecting to WebSocket at ws://localhost:8080/mint/push/agent
2023-02-01 10:18:46.866 DEBUG 9216 --- [etClientChannel] o.s.w.s.c.s.StandardWebSocketClient      : Connecting to ws://localhost:8080/mint/push/agent
2023-02-01 10:18:46.895 ERROR 9216 --- [cTaskExecutor-6] o.s.w.s.c.WebSocketConnectionManager     : Failed to connect

javax.websocket.DeploymentException: The HTTP response from the server [200] did not permit the HTTP upgrade to WebSocket
        at org.apache.tomcat.websocket.WsWebSocketContainer.connectToServerRecursive(WsWebSocketContainer.java:400)
        at org.apache.tomcat.websocket.WsWebSocketContainer.connectToServer(WsWebSocketContainer.java:185)
        at org.springframework.web.socket.client.standard.StandardWebSocketClient.lambda$doHandshakeInternal$0(StandardWebSocketClient.java:151)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.lang.Thread.run(Thread.java:748)

        solution:
            IIP 시스템 환경설정 파일에 아래 값을 꼭 등록 해야함. 그렇지 않으면 웹소켓 접속 자체가 안됨.
            설명 : 웹소켓컨테이너제품유형(Tomcat,WebLogic,GlassFish,WebSphere,Undertow,Jeus)    등록 필요 .
            환경 변수 값 : "system.websocket.container"