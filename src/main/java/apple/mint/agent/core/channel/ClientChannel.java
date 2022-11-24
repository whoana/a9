package apple.mint.agent.core.channel;

import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import apple.mint.agent.core.service.RequestService;
import apple.mint.agent.core.service.ResponseService;
import apple.mint.agent.core.service.Service;
import apple.mint.agent.core.service.ServiceMapper;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.msg.handler.MessageHandler;
import pep.per.mint.common.util.Util;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;

public class ClientChannel implements Runnable {

    // define logger
    Logger logger = LoggerFactory.getLogger(ClientChannel.class);

    /**
     * 웺속켓 연결 이후 run 메소드 처리 지연 사간 설정 상수 값
     */
    private static long CONNECTED_DELAY = 1 * 1000;

    /**
     * 웺속켓 종료 이후 run 메소드 처리 지연 사간 설정 상수 값
     */
    private static long CLOSED_DELAY = 10 * 1000;

    // run 메소드 처리 지연 설정 변수
    private long delay = CLOSED_DELAY;

    // 채널명 기본값
    private String channelName = "WebSocketClientChannel";

    // ComMessage serialize, deserialize 핸들러
    private MessageHandler messageHandler = new MessageHandler();

    // sendQueue 메시지 전송 지연처리 시간 설정
    private long sendDelay = 300;

    // 체널의 웹소켓 현재 상태
    private ChannelState state;

    // 웹소켓 연결관리를 위한 매니저 래퍼 오브젝트
    private WebSocketConnectionManager manager;

    // ClientChannel 스레드
    private Thread thread;

    // 연결된 웹소켓 세션 객체
    private WebSocketSession session;

    // 서버로 보낼 매시지 보간 래퍼큐
    private Queue<ComMessage<?, ?>> sendQueue;

    private SendChannelWrapper sendChannelWrapper;

    private int maxQueueSize = 100;

    private ServiceMapper serviceMapper;

    /**
     * @param uri
     * @param uriParameters
     */
    public ClientChannel(
            ServiceMapper serviceMapper,
            SendChannelWrapper sendChannelWrapper,
            String uri, Object[] uriParameters) {
        this.serviceMapper = serviceMapper;
        this.sendChannelWrapper = sendChannelWrapper;                
        this.sendQueue = sendChannelWrapper.getQueue();
        manager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                new TextWebSocketHandlerExtension(),
                uri,
                uriParameters);
        manager.setAutoStartup(false);
        manager.setHeaders(new WebSocketHttpHeaders());
        synchronized (this) {
            state = ChannelState.initialized;
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, channelName);
        }
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            try {
                logout();
            } catch (Exception e) {
                logger.error("logout fail:", e);
            }
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        while (thread != null && Thread.currentThread().equals(thread)) {
            // logger.debug("channel.state:".concat(state.toString()));
            try {
                if (state == ChannelState.initialized || state == ChannelState.closed) {
                    manager.stop();
                    manager.start();
                } else if (state == ChannelState.connected) {
                    if (sendQueue != null) {
                        while (true) {
                            ComMessage<?, ?> cmsg = sendQueue.peek();
                            if (cmsg == null)
                                break;

                            if (session == null) {
                                throw new Exception("Session is null");
                            }
                            TextMessage msg = new TextMessage(messageHandler.serialize(cmsg));
                            session.sendMessage(msg);

                            sendQueue.remove(cmsg);
                            try {
                                Thread.sleep(sendDelay);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                } else {

                }
            } catch (Exception e) {
                logger.error("ClientChannelException", e);
            } finally {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        state = ChannelState.stopped;
    }

    public void send(ComMessage<?, ?> cmsg) throws Exception {
        if (sendQueue.size() > maxQueueSize)
            throw new Exception("MaxQueueSizeReachedException");
        sendQueue.offer(cmsg);
    }

    private void logout() {

        try {
            if (ClientChannel.this.serviceMapper != null) {
                TextMessage request = new TextMessage(
                        messageHandler.serialize(ClientChannel.this.serviceMapper.getLogoutService().request()));
                session.sendMessage(request);
            }
        } catch (Exception e) {
            logger.error("LogoutException", e);
        }
    }

    private final class TextWebSocketHandlerExtension extends TextWebSocketHandler {
        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            try {
                ComMessage<?, ?> cmsg = messageHandler.deserialize(message.getPayload());

                // TODO Exctension null 처리 하기
                if (cmsg.getExtension() == null) {
                    logger.info("have no extension msg:".concat(Util.toJSONPrettyString(cmsg)));
                    return;
                }
                String serviceCd = cmsg.getExtension().getServiceCd();
                Service service = serviceMapper.getService(serviceCd);

                if (service == null)
                    throw new Exception("UnknownServiceExcetpion");
                if (service instanceof RequestService) {
                    ((RequestService) service).receive(cmsg);
                } else if (service instanceof ResponseService) {
                    ((ResponseService) service).receive(cmsg);
                }

            } catch (Exception e) {
                logger.error("ClientChannel Exception:", e);
            }
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            ClientChannel.this.session = session;
            try {
                if (ClientChannel.this.serviceMapper == null) {
                    throw new Exception("ClientChannel.this.loginService == null");
                }
                TextMessage msg = new TextMessage(
                        messageHandler.serialize(ClientChannel.this.serviceMapper.getLoginService().request()));
                session.sendMessage(msg);
            } catch (Exception e) {
                logger.error("Login Fail:", e);
            }

            synchronized (this) {
                state = ChannelState.connected;
                ClientChannel.this.sendChannelWrapper.setState(state);
                delay = CONNECTED_DELAY;
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            ClientChannel.this.session = null;
            synchronized (this) {
                state = ChannelState.closed;
                ClientChannel.this.sendChannelWrapper.setState(state);
                delay = CLOSED_DELAY; // 10 minutes;
            }
        }
    }

}
