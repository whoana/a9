package apple.mint.agent.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

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

import apple.mint.agent.core.service.ServiceManager;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.util.Util;

@RestController
public class ServiceController {
    Logger logger = LoggerFactory.getLogger(ServiceController.class);

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
     * @param comMessage
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/agent/v4/services/moel/check/dirs", params = "method=GET", method = RequestMethod.POST)
    public @ResponseBody ComMessage<?, ?> checkMoelInterfaceDir(
            @RequestBody ComMessage<Map<String, String>, Map<String, String>> comMessage) throws Exception {

        Map<String, String> params = comMessage.getRequestObject();
        Map<String, String> res = new HashMap<String, String>();
        // String integrationId = params.get("integrationId");
        String directory = params.get("directory");
        if (!Util.isEmpty(directory)) {
 
            try {
                Path path = Paths.get(directory);
                boolean check = Files.exists(Paths.get(directory));
                if (check) {
                    boolean isDirectory = Files.isDirectory(path);
                    if (!isDirectory) {
                        res.put("confirmCd", "3");
                        res.put("confirmMsg", "디렉토리아님");
                    } else {
                        boolean isReadable = Files.isReadable(path);
                        if (!isReadable) {
                            res.put("confirmCd", "4");
                            res.put("confirmMsg", "읽기권한없음");
                        } else {
                            res.put("confirmCd", "1");
                            res.put("confirmMsg", "확인완료");
                        }
                    }
                } else {
                    res.put("confirmCd", "2");
                    res.put("confirmMsg", "디렉토리없음");
                }
                comMessage.setErrorCd("0");
                comMessage.setErrorMsg("ok");
            } catch (Exception e) {
                res.put("confirmCd", "9");
                res.put("confirmMsg", "기타확인실패");

                comMessage.setErrorCd("9");
                comMessage.setErrorMsg(e.getMessage());
            }
        } else {
            comMessage.setErrorCd("9");
            comMessage.setErrorMsg("체크할 디렉토리값이 null 입니다.");
        }
        comMessage.setResponseObject(res);
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));

        return comMessage;
    }

    @PostMapping("/agent/v4/restart")
    public void restart() {
        serviceManager.stopServiceGroupAll();
        // A9.restart();
        restartEndpoint.restart();
    }

    @GetMapping("/agent/v4/alive")
    public String iamalive() {
        Date date = new Date();
        return "I am alive! it's ".concat(date.toString());
    }

}
