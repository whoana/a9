IIPAgent 4.0 웹소켓 접속시 유의사항 .
1.IIP 시스템 환경설정 파일에 아래 값을 꼭 등록 해야함. 그렇지 않으면 웹소켓 접속 자체가 안됨.

    설명 : 웹소켓컨테이너제품유형(Tomcat,WebLogic,GlassFish,WebSphere,Undertow,Jeus)    등록 필요 .
    환경 변수 값 : "system.websocket.container"

    String websocketContainer = commonService.getEnvironmentalValue("system", "websocket.container", null);
    if(Util.isEmpty(websocketContainer)) {
			registry.addHandler(prototypeHandler(),  prototypeUrl).setAllowedOrigins("*").addInterceptors(new HttpSessionHandshakeInterceptor());
			registry.addHandler(frontWebsocketHandler(), frontUrl).setAllowedOrigins("*").addInterceptors(new HttpSessionHandshakeInterceptor());
			registry.addHandler(agentWebsocketHandler(), agentUrl).setAllowedOrigins("*").addInterceptors(new HttpSessionHandshakeInterceptor()).withSockJS();
		}else {
			RequestUpgradeStrategy requestUpgradeStrategy = null;
			if(WEBSOCKET_CONTAINER_TOMCAT.equalsIgnoreCase(websocketContainer)) {
				requestUpgradeStrategy = new TomcatRequestUpgradeStrategy();
			}else if(WEBSOCKET_CONTAINER_WEBLOGIC.equalsIgnoreCase(websocketContainer)) {
				requestUpgradeStrategy = new WebLogicRequestUpgradeStrategy();
			}else if(WEBSOCKET_CONTAINER_GLASSFISH.equalsIgnoreCase(websocketContainer)) {
				requestUpgradeStrategy = new GlassFishRequestUpgradeStrategy();
			}else if(WEBSOCKET_CONTAINER_WEBSPHERE.equalsIgnoreCase(websocketContainer)) {
				requestUpgradeStrategy = new WebSphereRequestUpgradeStrategy();
			}else if(WEBSOCKET_CONTAINER_UNDERTOW.equalsIgnoreCase(websocketContainer)) {
				 requestUpgradeStrategy = new UndertowRequestUpgradeStrategy();
			}else if(WEBSOCKET_CONTAINER_JEUS.equalsIgnoreCase(websocketContainer)) {
				//for jeus
				requestUpgradeStrategy = new jeus.spring.websocket.JeusRequestUpgradeStrategy();
			}else {
				requestUpgradeStrategy = new TomcatRequestUpgradeStrategy();
			}

			registry.addHandler(prototypeHandler(),  prototypeUrl).setAllowedOrigins("*")
			.addInterceptors(new HttpSessionHandshakeInterceptor()).setHandshakeHandler(requestUpgradeStrategy == null ? new DefaultHandshakeHandler() : new DefaultHandshakeHandler(requestUpgradeStrategy));

			registry.addHandler(frontWebsocketHandler(), frontUrl).setAllowedOrigins("*")
			.addInterceptors(new HttpSessionHandshakeInterceptor()).setHandshakeHandler(requestUpgradeStrategy == null ? new DefaultHandshakeHandler() : new DefaultHandshakeHandler(requestUpgradeStrategy));

			registry.addHandler(agentWebsocketHandler(), agentUrl).setAllowedOrigins("*")
			.addInterceptors(new HttpSessionHandshakeInterceptor()).setHandshakeHandler(requestUpgradeStrategy == null ? new DefaultHandshakeHandler() : new DefaultHandshakeHandler(requestUpgradeStrategy));
		}