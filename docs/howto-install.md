[howto install agent]

    본문서는 에이전트 설치 방법을 설명합니다.

    [1.에이전트 실행 구조]
        에이전트는 작업을 수행할 로컬서버에 설치되어 실행되는 자바프로세스입니다.
        에이전트 실행 시 수행할 서비스모듈의 구현체 및 설정파일은 네트워크를 통해 IIP 서버로 부터 로딩하게 됩니다.
        서비스 구현체의 원격 위치는 아래와 같습니다.

            [에이전트 서비스 모듈 원격 위치 예]
                {HTTP IIP SERVICE ADDRESS} : "http://localhost:8080/mint/agent/v4/"
                
                [settings 파일]
                    {HTTP IIP SERVICE ADDRESS}/MOEL.settings.json // 에이전트 서비스 설정

                [libs 파일들]
                    {HTTP IIP SERVICE ADDRESS}/libs/
                        a9-impl-1.0.jar                 // 에이전트 서비스 구현 라이브러리 
                        jPowerShell-1.9.jar             // 서비스에서 참조하는 dependency 라리브러리 
                        jProcesses-1.6.4.jar            // 서비스에서 참조하는 dependency 라리브러리 
                        mint-agent-common-3.0.0.jar     // 서비스에서 참조하는 dependency 라리브러리 
                        WMI4Java-1.6.1.jar              // 서비스에서 참조하는 dependency 라리브러리 
        
        에이전트를 문제없이 실행하기 위해서는 서비스 설정 파일 및 구현체 라이브러리는 IIP서버 해당 위치에 반드시 존재해야 합니다.

