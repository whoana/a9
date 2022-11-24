package apple.mint.agent.impl.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.channel.SendChannelWrapper;
import apple.mint.agent.core.service.RequestService;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.Extension;
import pep.per.mint.common.msg.handler.ServiceCodeConstant;
import pep.per.mint.common.util.Util;

public class LoginService extends RequestService {

    Logger logger = LoggerFactory.getLogger(LoginService.class);

    private String agentCd;

    public LoginService(String name, String cd, String agentCd, String password, SendChannelWrapper sendQueueWrapper) {
        super(cd, name, 0, sendQueueWrapper);
        this.agentCd = agentCd;
    }

    public ComMessage<?, ?> request() throws Exception {
        ComMessage<?, ?> msg = new ComMessage();
        msg.setId(UUID.randomUUID().toString());
        msg.setUserId(agentCd);
        msg.setStartTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
        Extension ext = new Extension();
        ext.setMsgType(Extension.MSG_TYPE_REQ);
        ext.setServiceCd(ServiceCodeConstant.WS0025);
        msg.setExtension(ext);
        return msg;
    }

    @Override
    public void receive(ComMessage<?, ?> response) throws Exception {
        logger.debug("msg:".concat(Util.toJSONPrettyString(response)));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
