package apple.mint.agent.core.service;

import apple.mint.agent.core.channel.SendChannelWrapper;
import pep.per.mint.common.data.basic.ComMessage;

public abstract class RequestService extends Service {

    public RequestService(String cd, String name, long delay, SendChannelWrapper sendQueueWrapper) {
        super(cd, name, delay, sendQueueWrapper);
    }

    /**
     * This method will be done when the response is comming.
     */
    public abstract void receive(ComMessage<?, ?> response) throws Exception;
}
