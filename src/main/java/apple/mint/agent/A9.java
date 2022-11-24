package apple.mint.agent;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.channel.SendChannelWrapper;
import apple.mint.agent.core.service.ServiceManager;
import apple.mint.agent.core.service.ServiceMapper;
import apple.mint.agent.impl.service.BarPushService;
import apple.mint.agent.impl.service.LoginService;
import apple.mint.agent.impl.service.LogoutService;  

@SpringBootApplication
public class A9 {
 
	public static void main(String[] args) {
		SpringApplication.run(A9.class, args);
	}

	@Bean("sendChannelWrapper")
	public SendChannelWrapper sendChannel() {
		SendChannelWrapper channel = new SendChannelWrapper(100);
		return channel;
	}

	@Bean("serviceMapper")
	public ServiceMapper getServiceMapper(@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper) {
		ServiceMapper mapper = new ServiceMapper();
		String agentCd = "111";

		mapper.setLoginService(new LoginService("LoginService", "WS0025", agentCd, "", sendChannelWrapper));

		mapper.setLogoutService(new LogoutService("LoginService", "WS0026", agentCd, sendChannelWrapper));

		mapper.addService(new BarPushService("BarPushService", "WS0043", 5 * 1000, agentCd, sendChannelWrapper));

		return mapper;
	}

	@Bean
	public ClientChannel getClientChannel(
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper) throws Exception {
		String[] params = {};
		ClientChannel channel = new ClientChannel(
				serviceMapper,
				sendChannelWrapper,
				"ws://localhost:8080/mint/push/agent",
				params);
		channel.start();
		return channel;
	}

	@Bean(initMethod = "startAllService")
	public ServiceManager getServiceManager(
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper) throws Exception {
		 
		ServiceManager manager = new ServiceManager(serviceMapper);
		return manager;
	}

}
