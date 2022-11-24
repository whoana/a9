package apple.mint.agent.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import apple.mint.agent.core.channel.SendChannelWrapper;
import apple.mint.agent.core.service.PushService;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.Extension;
import pep.per.mint.common.msg.handler.ServiceCodeConstant;
import pep.per.mint.common.util.Util;

/**
 * example service
 */
public class BarPushService extends PushService {
    
    String agentCd;

    public BarPushService(String cd, String name, long delay, String agentCd, SendChannelWrapper sendQueueWrapper) {
        super(cd, name, delay, sendQueueWrapper);
        this.agentCd = agentCd;
    }

    @Override
    public void run() {

        logger.info(name.concat(" are started."));
 
        while (thread != null && Thread.currentThread().equals(thread)) {
            try {
                 
                long current = System.currentTimeMillis();
                if (delay > (current - lastRequested)) continue;
                ComMessage<?, ?> msg = getPushMessage();
                send(msg);
                lastRequested = current;

            } catch (Exception e) {
                logger.error("Exception", e);
            } finally {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        logger.info(name.concat(" are finished."));
    }

    private ComMessage<?, ?> getPushMessage() throws Exception {

        ComMessage<List<Map<String, String>>, ?> msg = new ComMessage();
        msg.setId(UUID.randomUUID().toString());
        msg.setStartTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
        msg.setUserId(agentCd);
        msg.setCheckSession(false);
        msg.setRequestObject(new ArrayList<Map<String, String>>());

        Extension ext = new Extension();
        ext.setMsgType(Extension.MSG_TYPE_PUSH);
        ext.setServiceCd(ServiceCodeConstant.WS0043);
        msg.setExtension(ext);

        return msg;
    }

   

}
