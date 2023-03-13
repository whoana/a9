package apple.mint.agent.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import pep.per.mint.common.util.Util;
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
import apple.mint.agent.core.service.ServiceContext;
import apple.mint.agent.core.service.ServiceManager;

@Configuration
public class A9Config {

	Logger logger = LoggerFactory.getLogger(A9Config.class);

	static final String AGT_ERR_MSG_SYS_001 = "[AESYS001] 서버가 실행 중이 아니거나 네트워크 연결에 문제가 있어 에이전트 실행을 진행할 수 없습니다. 서버가 실행중인지 확인 후 다시 진행해주세요.";
	static final String AGT_ERR_MSG_SYS_002 = "[AESYS002] 환경설정에 문제가 있어 에이전트 실행을 진행할 수 없습니다.";

	@Bean("configManager")
	public ConfigManager getConfigManager() throws Exception {

		int tryConfigCount = 1;
		ConfigManager cm = new ConfigManager();
		Config config = cm.getConfig();
		int maxTryConfigCount = config.getMaxConfigCount();
		long tryConfigDelay = config.getTryConfigDelay();

		while (true) {
			try {
				cm.prepare();
				return cm;
			} catch (ConnectException e) {
				logger.error("try getConfigManager: " + tryConfigCount, e);
				if (tryConfigCount >= maxTryConfigCount)
					throw new SystemException(AGT_ERR_MSG_SYS_001, e);
			} catch (Exception e) {
				logger.error("try getConfigManager: " + tryConfigCount, e);
				if (tryConfigCount >= maxTryConfigCount)
					throw new SystemException(AGT_ERR_MSG_SYS_002, e);
			}
			tryConfigCount++;
			Thread.sleep(tryConfigDelay);
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

		Config config = configManager.getConfig();

		String address = config.getServerAddress();
		String port = config.getServerPort();
		String serverAddress = "http://" + address + ":" + (Util.isEmpty(port) ? "80" : port);

		for (int i = 0; i < uriList.length; i++) {
			method.invoke(classLoader, new URL(serverAddress + uriList[i]));
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
		serviceContext.setServerAddress(config.getServerAddress());
		serviceContext.setServerPort(config.getServerPort());
		String agentNm = config.getAgentNm();
		String password = config.getPassword();

		Settings settings = configManager.getSettings();
		ServiceMapperConfig smc = settings.getServiceMapperConfig();
		boolean exceptionSkipMode = smc.isExceptionSkipMode();
		ServiceConfig[] serviceConfigs = smc.getServiceConfigs();
		ServiceMapper mapper = new ServiceMapper();

		mapper.setLoginService(
				new LoginService("LoginService", "WS0025", agentNm, password, serviceContext,
						sendChannelWrapper, false));
		mapper.setLogoutService(
				new LogoutService("LogoutService", "WS0026", agentNm, serviceContext,
						sendChannelWrapper, false));

		try {
			mapper.addService(
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

	@Bean("serviceManager")
	public ServiceManager getServiceManager(
			@Autowired @Qualifier("configManager") ConfigManager configManager,
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("implClassLoader") URLClassLoader implClassLoader) {
		ServiceGroupConfig[] serviceGroupConfigs = configManager.getSettings().getServiceMapperConfig().getServiceGroupConfigs();
		ServiceManager serviceManager = new ServiceManager(serviceGroupConfigs, serviceMapper, sendChannelWrapper, implClassLoader);
		return serviceManager;
	}

	@Bean
	public ClientChannel getClientChannel(
			@Autowired @Qualifier("serviceManager") ServiceManager serviceManager,
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("configManager") ConfigManager configManager,
			@Autowired @Qualifier("implClassLoader") URLClassLoader implClassLoader) throws Exception {
		String uri = configManager.getSettings().getChannelConfig().getUri();
		String address = configManager.getConfig().getServerAddress();
		String port = configManager.getConfig().getServerPort();
		port = Util.isEmpty(port) ? "80" : port;
		uri = "ws://" + address + ":" + port + uri;
		String[] params = configManager.getSettings().getChannelConfig().getUriParameters();
		ClientChannel channel = new ClientChannel(
				serviceMapper,
				sendChannelWrapper,
				uri,
				params);
		channel.start();
		serviceManager.startServiceGroupAll();
		return channel;
	}

}
