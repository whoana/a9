package apple.mint.agent.core.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apple.mint.agent.core.channel.ClientChannel;
import apple.mint.agent.core.channel.SendChannelWrapper;
import pep.per.mint.common.data.basic.ComMessage;

public abstract class Service implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(Service.class);

    protected String cd;

    protected String name;

    protected long delay = 10 * 1000; // 10 seconds

    protected long lastRequested;

    private SendChannelWrapper sendChannelWrapper;

    protected Thread thread;

    public Service(String cd, String name, long delay, SendChannelWrapper sendChannelWrapper) {
        this.cd = cd;
        this.name = name;
        this.delay = delay;
        this.sendChannelWrapper = sendChannelWrapper;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, name);
        } else {
            stop();
        }
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void send(ComMessage<?, ?> msg) throws Exception {
        sendChannelWrapper.send(msg);
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setLastRequested(long lastRequested) {
        this.lastRequested = lastRequested;
    }

    public long getLastRequested() {
        return this.lastRequested;
    }

    public String getCd() {
        return cd;
    }

    public void setServiceCd(String cd) {
        this.cd = cd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
