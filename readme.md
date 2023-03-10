[A9 에이전트 적용을 위한 웹소켓 준비 사항]

    1 IIP 시스템 환경설정에 아래 값을 꼭 등록 해야함. 그렇지 않으면 웹소켓 접속 자체가 안됨.
    	설명 : 웹소켓 컨테이너 제품유형(Tomcat,WebLogic,GlassFish,WebSphere,Undertow,Jeus) 등록 필요.
    	환경 변수 값 : "system.websocket.container"

    	현재 사내 개발 DB 값 :
    		SELECT * FROM TSU0302 WHERE ATTRIBUTE_ID = 'websocket.container';
    		------------------------------------------------------------------------------------------------------------------------------------
    		-- PACKAGE	ATTRIBUTE_ID			IDX		ATTRIBUTE_NM			ATTRIBUTE_VALUE		COMMENTS							DEL_YN
    		------------------------------------------------------------------------------------------------------------------------------------
    		-- system	websocket.container		1		websocket.container		Tomcat				웹소켓컨테이너제품유형(웹소켓사용시 필수값)			N

    2 IIP서버 -> 에이전트 PUSH 명령 서비스 비활성화
    	설명 : A9 에이전트에서는 사용되지 않는 서비스

    	IIP서버 -> 에이전트 PUSH 서비스 리스트 조회 :
    		SELECT * FROM TSU0205 WHERE service_type = '3';
    	미사용으로 업데이트 :
    		UPDATE TSU0205 SET USE_YN = 'N' WHERE service_type = '3';

    3 리눅스 포트 오픈
    	sudo firewall-cmd --permanent --zone=public --add-port=9090/tcp
    	sudo firewall-cmd --reload
