package apple.mint.agent.config;

import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.channel.SendChannelWrapper;

import apple.mint.agent.core.config.Config;
import apple.mint.agent.core.config.ConfigManager;

import apple.mint.agent.core.config.ServiceConfig;
import apple.mint.agent.core.config.ServiceGroupConfig;
import apple.mint.agent.core.config.ServiceMapperConfig;
import apple.mint.agent.core.service.ServiceMapper;
import apple.mint.agent.core.service.LoginService;
import apple.mint.agent.core.service.LogoutService;

import apple.mint.agent.core.service.ServiceContext;
import apple.mint.agent.core.service.ServiceManager;
import pep.per.mint.common.msg.handler.ServiceCodeConstant;

@Configuration
public class A9Config {

	Logger logger = LoggerFactory.getLogger(A9Config.class);

	@Bean("configManager")
	public ConfigManager getConfigManager() throws Exception {
		ConfigManager cm = new ConfigManager();
		cm.prepare();
		return cm;
	}

	@Bean("sendChannelWrapper")
	public SendChannelWrapper sendChannel(@Autowired @Qualifier("configManager") ConfigManager configManager) {
		int maxQueueSize = configManager.getConfig().getChannelWrapperConfig().getMaxQueueSize();
		SendChannelWrapper channel = new SendChannelWrapper(maxQueueSize);
		return channel;
	}

	@Bean
	public ServiceContext getServiceContext() {
		return new ServiceContext();
	}

	@Bean("implClassLoader")
	public URLClassLoader getImplClassLoader(@Autowired @Qualifier("configManager") ConfigManager configManager)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, MalformedURLException, ClassNotFoundException {

		URLClassLoader classLoader = new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());

		String[] uriList = configManager.getConfig().getClassLoaderConfig().getUriList();

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

		// Class.forName("apple.mint.agent.impl.service.push.HelloWorld", true,
		// classLoader);

		return classLoader;
	}

	@Bean("serviceMapper")
	public ServiceMapper getServiceMapper(
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("configManager") ConfigManager configManager,
			@Autowired ServiceContext serviceContext,
			@Autowired @Qualifier("implClassLoader") URLClassLoader implClassLoader)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		Config config = configManager.getConfig();
		String agentCd = config.getAgentId();
		String password = config.getPassword();
		ServiceMapperConfig smc = config.getServiceMapperConfig();
		boolean exceptionSkipMode = smc.isExceptionSkipMode();
		ServiceConfig[] serviceConfigs = smc.getServiceConfigs();
		ServiceMapper mapper = new ServiceMapper();

		System.out.println("111");

		mapper.setLoginService(
				new LoginService("LoginService", ServiceCodeConstant.WS0025, agentCd, password, serviceContext,
						sendChannelWrapper, false));
		mapper.setLogoutService(
				new LogoutService("LogoutService", ServiceCodeConstant.WS0026, agentCd, serviceContext,
						sendChannelWrapper, false));

		// logger.info("1234567890-getClassLoader():" +
		// this.getClass().getClassLoader());

		// logger.info("1234567890-SpringApplication.class.getClassLoader():" +
		// SpringApplication.class.getClassLoader());

		mapper.addService(
				configManager.getConfig().getClassLoaderConfig().getUriList(),
				serviceConfigs,
				serviceContext,
				sendChannelWrapper,
				exceptionSkipMode,
				implClassLoader);

		return mapper;
	}

	@Bean(initMethod = "startServiceGroupAll")
	public ServiceManager getServiceManager(
			@Autowired @Qualifier("configManager") ConfigManager configManager,
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("implClassLoader") URLClassLoader implClassLoader) {
		ServiceGroupConfig[] serviceGroupConfigs = configManager.getConfig().getServiceMapperConfig()
				.getServiceGroupConfigs();
		ServiceManager manager = new ServiceManager(serviceGroupConfigs, serviceMapper, sendChannelWrapper,
				implClassLoader);
		return manager;
	}

	@Bean
	public ClientChannel getClientChannel(
			@Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper,
			@Autowired @Qualifier("sendChannelWrapper") SendChannelWrapper sendChannelWrapper,
			@Autowired @Qualifier("configManager") ConfigManager configManager) throws Exception {

		String uri = configManager.getConfig().getChannelConfig().getUri();
		String[] params = configManager.getConfig().getChannelConfig().getUriParameters();

		ClientChannel channel = new ClientChannel(
				serviceMapper,
				sendChannelWrapper,
				uri,
				params);
		channel.start();
		return channel;
	}

	// @Bean(initMethod = "startAllService")
	// public ServiceManager getServiceManager(
	// @Autowired @Qualifier("serviceMapper") ServiceMapper serviceMapper) throws
	// Exception {

	// ServiceManager manager = new ServiceManager(serviceMapper);

	// return manager;
	// }

}
