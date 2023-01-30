

IIPAgent 4.0 웹소켓 접속시 유의사항
	1.IIP 시스템 환경설정 파일에 아래 값을 꼭 등록 해야함. 그렇지 않으면 웹소켓 접속 자체가 안됨.
    	설명 : 웹소켓컨테이너제품유형(Tomcat,WebLogic,GlassFish,WebSphere,Undertow,Jeus)    등록 필요 .
    	환경 변수 값 : "system.websocket.container"