package apple.mint.agent.core.channel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import pep.per.mint.common.data.basic.ComMessage;

public class SendChannelWrapper {

    private Queue<ComMessage<?, ?>> queue = new ConcurrentLinkedQueue<>();

    private int maxQueueSize = 100;

    ChannelState state = ChannelState.closed;

    public SendChannelWrapper(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public void send(ComMessage<?, ?> message) throws Exception {
        if (queue.size() > maxQueueSize)
            throw new Exception("MaxQueueSizeReachedException");
        if (!ChannelState.connected.equals(state)) 
            throw new Exception("AbnormalChannelStateException:".concat(state.toString()));
        queue.offer(message);
    }

    public Queue<ComMessage<?, ?>> getQueue() {
        return queue;
    }

    public ChannelState getState() {
        return state;
    }

    public void setState(ChannelState state) {
        this.state = state;
    }

    

}
