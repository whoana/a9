package apple.mint.agent.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.cloud.context.restart.RestartEndpoint;

import apple.mint.agent.A9;
import apple.mint.agent.core.service.ServiceManager;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.util.Util;

@RestController
public class ServiceController {
    Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    ServiceManager serviceManager;

    @Autowired
    private RestartEndpoint restartEndpoint;

    @RequestMapping(value = "/agent/v4/services/start/all", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> startAllService(
            @RequestBody ComMessage<?, ?> comMessage) {
        serviceManager.startServiceGroupAll();
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd("0");
        return comMessage;
    }

    @RequestMapping(value = "/agent/v4/services/stop/all", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> stopAllService(
            @RequestBody ComMessage<?, ?> comMessage) {
        serviceManager.stopServiceGroupAll();
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd("0");
        return comMessage;
    }

    @RequestMapping(value = "/agent/v4/services/start/{serviceCd}", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> startService(
            @RequestBody ComMessage<?, ?> comMessage,
            @PathVariable("serviceCd") String serviceCd) throws Exception {
        ComMessage<?, ?> res = serviceManager.executeService(serviceCd);
        if (res != null)
            return res;
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorMsg("haveNoData!");
        comMessage.setErrorCd("9");
        return comMessage;
    }

    // @Autowired
    // @Qualifier("myClassLoader")
    // URLClassLoader myClassLoader;

    // @RequestMapping(value = "/agent/managers/services/reload", params =
    // "method=GET", method = RequestMethod.POST)
    // public @ResponseBody ComMessage<?, ?> reloadService(
    // @RequestBody ComMessage<?, ?> comMessage)
    // throws MalformedURLException, IllegalAccessException,
    // IllegalArgumentException, InvocationTargetException,
    // NoSuchMethodException, SecurityException, ClassNotFoundException,
    // InstantiationException {

    // try {
    // Class serviceClass = myClassLoader.loadClass("com.example.App");
    // Object service = serviceClass.newInstance();
    // Class[] paramTypes = {};
    // Method method = serviceClass.getDeclaredMethod("hello", paramTypes);
    // Object[] paramValues = {};
    // String msg = (String) method.invoke(service, paramValues);
    // logger.debug("msg:".concat(msg));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
    // comMessage.setErrorCd("0");
    // return comMessage;
    // }

    @PostMapping("/agent/v4/restart")
    public void restart() {
        serviceManager.stopServiceGroupAll();
        // A9.restart();
        restartEndpoint.restart();
    }

}
