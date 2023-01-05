package apple.mint.agent.config;

import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.context.annotation.Bean;

import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.channel.SendChannelWrapper;

import apple.mint.agent.core.config.Config;
import apple.mint.agent.core.config.ConfigManager;

import apple.mint.agent.core.config.ServiceConfig;
import apple.mint.agent.core.config.ServiceGroupConfig;
import apple.mint.agent.core.config.ServiceMapperConfig;
import apple.mint.agent.core.config.Settings;
import apple.mint.agent.core.service.ServiceMapper;
import apple.mint.agent.exception.SystemException;
import apple.mint.agent.core.service.LoginService;
import apple.mint.agent.core.service.LogoutService;
import apple.mint.agent.core.service.RestartAgentService;
import apple.mint.agent.core.service.ServiceContext;
import apple.mint.agent.core.service.ServiceManager;
import pep.per.mint.common.msg.handler.ServiceCodeConstant;

@Configuration
public class A9Config {

	Logger logger = LoggerFactory.getLogger(A9Config.class);
	
	static final String AGT_ERR_MSG_SYS_001 = "[AESYS001] 서버가 실행 중이 아니거나 네트워크 연결에 문제가 있어 에이전트 실행을 진행할 수 없습니다. 서버가 실행중인지 확인 후 다시 진행해주세요.";

	@Bean("configManager")
	public ConfigManager getConfigManager() throws Exception {
		try{
			ConfigManager cm = new ConfigManager();
			cm.prepare();
			return cm;
		}catch(ConnectException e){
			throw new SystemException(AGT_ERR_MSG_SYS_001, e);
		}
	}

	@Bean("sendChannelWrapper")
	public SendChannelWrapper sendChannel(@Autowired @Qualifier("configManager") ConfigManager configManager) {
		int maxQueueSize = configManager.getSettings().getChannelWrapperConfig().getMaxQueueSize();
		SendChannelWrapper channel = new SendChannelWrapper(maxQueueSize);
		return channel;
	}

	@Bean
	public ServiceContext getServiceContext() {
		ServiceContext sc = new ServiceContext();
		return sc;
	}

	@Bean("implClassLoader")
	public URLClassLoader getImplClassLoader(@Autowired @Qualifier("configManager") ConfigManager configManager)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, MalformedURLException, ClassNotFoundException {

		URLClassLoader classLoader = new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());

		String[] uriList = configManager.getSettings().getClassLoaderConfig().getUriList();

		if (uriList == null || uriList.length == 0) {
			throw new IllegalArgumentException("class uriList must be not null.");
		}

		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}

		for (int i = 0; i < uriList.length; i++) {
			method.invoke(classLoader, new URL(uriList[i]));
		}

		return classLoader;
	}

	@Bean("serviceMapper")
	public ServiceMapper getServiceMapper(
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("configManager") ConfigManager configManager,
			@Autowired ServiceContext serviceContext,
			@Autowired @Qualifier("implClassLoader") URLClassLoader implClassLoader)
			throws Exception {
		Config config = configManager.getConfig();
		String agentCd = config.getAgentId();
		String password = config.getPassword();

		Settings settings = configManager.getSettings();
		ServiceMapperConfig smc = settings.getServiceMapperConfig();
		boolean exceptionSkipMode = smc.isExceptionSkipMode();
		ServiceConfig[] serviceConfigs = smc.getServiceConfigs();
		ServiceMapper mapper = new ServiceMapper();

		mapper.setLoginService(
				new LoginService("LoginService", ServiceCodeConstant.WS0025, agentCd, password, serviceContext,
						sendChannelWrapper, false));
		mapper.setLogoutService(
				new LogoutService("LogoutService", ServiceCodeConstant.WS0026, agentCd, serviceContext,
						sendChannelWrapper, false));

		try {
			mapper.addService(
					configManager.getSettings().getClassLoaderConfig().getUriList(),
					serviceConfigs,
					serviceContext,
					sendChannelWrapper,
					exceptionSkipMode,
					implClassLoader);
		} catch (Exception e) {
			throw new SystemException(AGT_ERR_MSG_SYS_001, e);
		}
		 
		return mapper;
	}

	@Bean(initMethod = "startServiceGroupAll")
	public ServiceManager getServiceManager(
			@Autowired @Qualifier("configManager") ConfigManager configManager,
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired ServiceContext serviceContext,
			@Autowired RestartEndpoint restartEndpoint,
			@Autowired @Qualifier("implClassLoader") URLClassLoader implClassLoader) {
		ServiceGroupConfig[] serviceGroupConfigs = configManager.getSettings().getServiceMapperConfig()
				.getServiceGroupConfigs();
		ServiceManager manager = new ServiceManager(serviceGroupConfigs, serviceMapper, sendChannelWrapper,
				implClassLoader);

		serviceContext.setRestartAgentService(
				new RestartAgentService() {
					@Override
					public void restart() throws Exception {
						manager.stopServiceGroupAll();
						restartEndpoint.restart();
					}
				});

		return manager;
	}

	@Bean
	public ClientChannel getClientChannel(
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("configManager") ConfigManager configManager) throws Exception {

		String uri = configManager.getSettings().getChannelConfig().getUri();
		String[] params = configManager.getSettings().getChannelConfig().getUriParameters();

		ClientChannel channel = new ClientChannel(
				serviceMapper,
				sendChannelWrapper,
				uri,
				params);
		channel.start();
		return channel;
	}
 

}
