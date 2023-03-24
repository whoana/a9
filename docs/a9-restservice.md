[A9 REST Service List]

* 아래는 A9 에이전트로 요청할 수 있는 서비스 목록입니다. 
    - alive 체크 :
        http://{AGENT_HOST}:{PORT}/agent/v4/alive
        Response: String "I am alive! it's {current date}"

    - 에이전트 재시작 : 
        http://{AGENT_HOST}:{PORT}/agent/v4/restart

    - 모든 서비스 시작 : 
        http://{AGENT_HOST}:{PORT}/agent/v4/services/start/all?method=GET

    - 모든 서비스 종료 : 
        http://{AGENT_HOST}:{PORT}/agent/v4/services/stop/all?method=GET

    - 서비스 초기화 : 
        http://{AGENT_HOST}:{PORT}/agent/v4/services/reset/WS0049?method=GET

    - 프로세스체크 서비스 : 
        http://{AGENT_HOST}:9091/agent/v4/services/start/WS0030?method=GET
        Response: ComMessage (기존 웹소켓 서비스 WS0030 PUSH 메시지)   

    - 파일 인터페이스 체크 서비스 : 
        http://{AGENT_HOST}:{PORT}/agent/v4/services/start/WS0049?method=GET
        Response: ComMessage (기존 웹소켓 서비스 WS0049 PUSH 메시지)   

    - 고용노동부 인터페이스 디렉토리 유효성 체크 : 
        http://{AGENT_HOST}:9091/agent/v4/services/moel/check/dirs?method=GET        
        Request: ComMessage
        {
            "objectType": "ComMessage",
            "requestObject": [{
                    "interfaceId":"F@111111111",
                    "directory" : "/home/iip/moel/interfaces/INTF014",
                    "errDirectory" : "/home/iip/moel/interfaces/INTF014/err1"
            }],
            "startTime": "20230316105001001",
            "endTime": null,
            "errorCd":"0",
            "errorMsg":"",
            "userId": "iip",
            "appId": "admin",
            "checkSession":false
        }

        Response: ComMessage
        {
            "objectType": "ComMessage",
            "requestObject": null,
            "responseObject": [{
                    "interfaceId":"F@111111111",
                    "directory" : "/home/iip/moel/interfaces/INTF014",
                    "errDirectory" : "/home/iip/moel/interfaces/INTF014/err1",
                    "cd" : "0",
                    "msg": "ok"                    
            }],
            "startTime": "20230316105001001",
            "endTime": "20230316105002001",
            "errorCd":"0",
            "errorMsg":"",
            "userId": "iip",
            "appId": "admin",
            "checkSession":false
        }