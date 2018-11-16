package com.lg.eventbus;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventBus {

    private static class LazyHolder {
        public static final EventBus INSTANCE = new EventBus();
    }

    private final String BC_IP = "230.0.0.1"; // 组播地址
    private final int BC_PORT = 9999; // 组播端口
    private final int PACK_SIZE = 4096;

    private Flowable<String> udpFlowable;
    private MulticastSocket sock;
    private InetAddress bcAddr;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private EventBus() {
        try {
            sock = new MulticastSocket(BC_PORT);
            bcAddr = InetAddress.getByName(BC_IP);
            sock.joinGroup(bcAddr);
            sock.setLoopbackMode(false);
            udpFlowable = Flowable.create(new FlowableOnSubscribe<String>() {

                @Override
                public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                    while (true) {
                        DatagramPacket inpack = new DatagramPacket(new byte[PACK_SIZE], PACK_SIZE);
                        sock.receive(inpack);
                        String msg = new String(inpack.getData(), 0, inpack.getLength());
                        emitter.onNext(msg);
                    }
                }
            }, BackpressureStrategy.BUFFER)
                    .observeOn(Schedulers.io())
                    .share()
                    .subscribeOn(Schedulers.io());
        } catch (Exception err) {
            logger.error("加入组播网络失败", err);
            throw new RuntimeException(err);
        }

    }

    public static EventBus getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void post(Event event) {
        byte[] payload = event.toString().getBytes(Charset.forName("utf8"));
        DatagramPacket packet = new DatagramPacket(payload, 0, payload.length, bcAddr, BC_PORT);
        try {
            sock.send(packet);
        } catch (IOException err) {
            logger.error("发布事件失败", err);
        }
    }

    public void senderr() {
        byte[] payload = "hello word".getBytes(Charset.forName("utf8"));
        DatagramPacket packet = new DatagramPacket(payload, 0, payload.length, bcAddr, BC_PORT);
        try {
            sock.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Flowable<String> subscribe(String type) {
        Pattern pattern = Pattern.compile("^" + type + "\\:(.*)");
        return udpFlowable.map(message -> {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                try {
                    return matcher.group(1);
                } catch (Exception err) {
                    logger.error("解析事件失败:" + message, err);
                }
            }

            return "";
        })
                .filter(message -> !message.isEmpty());
    }

    public void subscribe(String type, Consumer<String> consumer) {
        subscribe(type)
                .subscribe(consumer::accept);
    }

}
