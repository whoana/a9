package apple.mint.agent.core.service;

import java.util.List;

public class ServiceManager {

    private ServiceMapper serviceMapper;

    public ServiceManager(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    public void startAllService() {
        startPushService();
        startRequestService();
        startResponseService();
    }

    public void stopAllService() {
        stopPushService();
        stopRequestService();
        stopResponseService();
    }

    public void startPushService() {
        List<PushService> services = serviceMapper.getPushServices();
        for (PushService service : services) {
            service.start();
        }
    }

    public void stopPushService() {
        List<PushService> services = serviceMapper.getPushServices();
        for (PushService service : services) {
            service.stop();
        }
    }

    public void startRequestService() {
        List<RequestService> services = serviceMapper.getRequestServices();
        for (RequestService service : services) {
            service.start();
        }
    }

    public void stopRequestService() {
        List<RequestService> services = serviceMapper.getRequestServices();
        for (RequestService service : services) {
            service.stop();
        }
    }

    public void startResponseService() {
        List<ResponseService> services = serviceMapper.getResponseServices();
        for (ResponseService service : services) {
            service.start();
        }
    }

    public void stopResponseService() {
        List<ResponseService> services = serviceMapper.getResponseServices();
        for (ResponseService service : services) {
            service.stop();
        }
    }
}
