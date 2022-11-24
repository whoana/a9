package apple.mint.agent.core.service;

import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.channel.SendChannelWrapper;
import pep.per.mint.common.data.basic.ComMessage;

public abstract class ResponseService extends Service {

    public ResponseService(String cd, String name, long delay, SendChannelWrapper sendQueueWrapper) {
        super(cd, name, delay, sendQueueWrapper);
    }

    /**
     * response method
     */
    public abstract void receive(ComMessage<?, ?> response) throws Exception;

}
