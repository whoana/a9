package apple.mint.agent.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.cloud.context.restart.RestartEndpoint;

import apple.mint.agent.service.FileService;
import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.service.ServiceManager;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.util.Util;

@RestController
public class ServiceController {
    Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    ClientChannel channel;

    @Autowired
    ServiceManager serviceManager;

    @Autowired
    RestartEndpoint restartEndpoint;

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

    @RequestMapping(value = "/agent/v4/services/restart/{groupId}", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> restartServiceGroup(
            @RequestBody ComMessage<?, ?> comMessage,
            @PathVariable("groupId") String groupId) {

        serviceManager.stopServiceGroup(groupId);
        serviceManager.startServiceGroup(groupId);
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd("0");
        return comMessage;
    }

    @RequestMapping(value = "/agent/v4/services/start/{serviceCd}", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> startService(
            @RequestBody ComMessage<?, ?> comMessage,
            @PathVariable("serviceCd") String serviceCd) throws Exception {
        ComMessage<?, ?> res = serviceManager.executeService(serviceCd, comMessage);
        if (res != null)
            return res;
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorMsg("haveNoData!");
        comMessage.setErrorCd("9");
        return comMessage;
    }

    @RequestMapping(value = "/agent/v4/services/reset/{serviceCd}", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> resetService(
            @RequestBody ComMessage<?, ?> comMessage,
            @PathVariable("serviceCd") String serviceCd) throws Exception {
        try {
            serviceManager.resetService(serviceCd);
            comMessage.setErrorMsg("The service[" + serviceCd + "] resetting done!");
            comMessage.setErrorCd("0");
        } catch (Exception e) {
            comMessage.setErrorMsg(e.getLocalizedMessage());
            comMessage.setErrorCd("9");
        } finally {
            comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        }
        return comMessage;
    }

    @Autowired
    FileService fileService;

    /**
     * <pre>
     *  고용노동부 파일 인터페이스 디렉토리 체크
     *  결과 코드 : 메시지  
     *      0 : 초기등록
     *      1 : 확인완료
     *      2 : 디렉토리없음
     *      3 : 디렉토리아님
     *      4 : 읽기권한없음
     *      9 : 기타확인실패
     * </pre>
     * 
     * @param comMessage
     * @return
     * @throws Exception
     */
    /*
     * @RequestMapping(value = "/agent/v4/services/moel/check/dirs", params =
     * "method=GET", method = RequestMethod.POST)
     * public @ResponseBody ComMessage<?, ?> checkMoelInterfaceDir(
     * 
     * @RequestBody ComMessage<Map<String, String>, Map<String, String>> comMessage)
     * throws Exception {
     * 
     * Map<String, String> params = comMessage.getRequestObject();
     * Map<String, String> res = new HashMap<String, String>();
     * // String integrationId = params.get("integrationId");
     * String directory = params.get("directory");
     * if (!Util.isEmpty(directory)) {
     * try {
     * res = fileService.checkDir(directory);
     * comMessage.setErrorCd("0");
     * comMessage.setErrorMsg("ok");
     * } catch (Exception e) {
     * res.put("confirmCd", "9");
     * res.put("confirmMsg", "기타확인실패");
     * comMessage.setErrorCd("9");
     * comMessage.setErrorMsg(e.getMessage());
     * }
     * } else {
     * comMessage.setErrorCd("9");
     * comMessage.setErrorMsg("체크할 디렉토리값이 null 입니다.");
     * }
     * comMessage.setResponseObject(res);
     * comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
     * return comMessage;
     * }
     */

    @RequestMapping(value = "/agent/v4/services/moel/check/dirs", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<List<Map<String, String>>, List<Map<String, String>>> checkMoelInterfaceDirs(
            @RequestBody ComMessage<List<Map<String, String>>, List<Map<String, String>>> comMessage) throws Exception {

        List<Map<String, String>> params = comMessage.getRequestObject();
        if (!Util.isEmpty(params)) {
            for (Map<String, String> map : params) {
                String directory = map.get("directory");
                if (!Util.isEmpty(directory)) {
                    try {
                        Map<String, String> res = fileService.checkDir(directory);
                        map.put("confirmCd", res.get("cd"));
                        map.put("confirmMsg", res.get("msg"));
                    } catch (Exception e) {
                        map.put("confirmCd", "9");
                        map.put("confirmMsg", "기타확인실패:" + e.getMessage());
                    }
                }

                String errorDirectory = map.get("errDirectory");
                if (!Util.isEmpty(errorDirectory)) {
                    try {
                        Map<String, String> res = fileService.checkDir(errorDirectory);
                        map.put("confirmErrCd", res.get("cd"));
                        map.put("confirmErrMsg", res.get("msg"));
                    } catch (Exception e) {
                        map.put("confirmErrCd", "9");
                        map.put("confirmErrMsg", "기타확인실패:" + e.getMessage());
                    }
                }
            }
            comMessage.setRequestObject(null);
            comMessage.setResponseObject(params);
            comMessage.setErrorCd("0");
            comMessage.setErrorMsg("OK");
        } else {
            comMessage.setErrorCd("9");
            comMessage.setErrorMsg("체크할 디렉토리 리스트가 존재하지 않습니다.");
        }

        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        return comMessage;
    }

    @PostMapping("/agent/v4/restart")
    public void restart() {
        serviceManager.stopServiceGroupAll();
        // A9.restart();
        channel.stop();
        restartEndpoint.restart();
    }

    @GetMapping("/agent/v4/alive")
    public String iamalive() {
        Date date = new Date();
        return "I am alive! it's ".concat(date.toString());
    }

}
