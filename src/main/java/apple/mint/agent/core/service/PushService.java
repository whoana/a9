package apple.mint.agent.core.service;
 
import apple.mint.agent.core.channel.SendChannelWrapper;
 

public abstract class PushService extends Service {

    public PushService(String cd, String name, long delay, SendChannelWrapper sendQueueWrapper) {
        super(cd, name, delay, sendQueueWrapper);
    } 

}
