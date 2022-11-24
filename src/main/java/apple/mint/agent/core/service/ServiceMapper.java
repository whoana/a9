package apple.mint.agent.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import apple.mint.agent.impl.service.LoginService;
import apple.mint.agent.impl.service.LogoutService;

public class ServiceMapper {

    LoginService loginService;

    LogoutService logoutService;

    List<RequestService> requestServices = new ArrayList<RequestService>();

    List<PushService> pushServices = new ArrayList<PushService>();

    List<ResponseService> responseServices = new ArrayList<ResponseService>();

    Map<String, Service> map = new LinkedHashMap<String, Service>();

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
        map.put(loginService.getCd(), loginService);
    }

    public void setLogoutService(LogoutService logoutService) {
        this.logoutService = logoutService;
        map.put(logoutService.getCd(), logoutService);
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public LogoutService getLogoutService() {
        return logoutService;
    }

    public void addService(Service service) {
        if (service instanceof RequestService) {
            requestServices.add((RequestService) service);
        } else if (service instanceof PushService) {
            pushServices.add((PushService) service);
        } else if (service instanceof ResponseService) {
            responseServices.add((ResponseService) service);
        }

        map.put(service.getCd(), service);
    }

    public Service getService(String cd) {
        return map.get(cd);
    }

    public List<RequestService> getRequestServices() {
        return this.requestServices;
    }

    public List<PushService> getPushServices() {
        return this.pushServices;
    }

    public List<ResponseService> getResponseServices() {
        return this.responseServices;
    }

    
}
