package bgu.spl.mics;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.java.swing.plaf.motif.resources.motif_zh_CN;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    // instance is created when the class is loaded into memory
    private static final MessageBusImpl INSTANCE = new MessageBusImpl();

    // Mapping of MicroServices to their message queues
    private final Map<MicroService, BlockingQueue<Message>> messageQueues;

    // Event subscriptions
    private final Map<Class<? extends Event>, List<MicroService>> eventSubscribers;

    // Broadcast subscriptions
    private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers;

    private MessageBusImpl() {
        messageQueues = new ConcurrentHashMap<>();
        eventSubscribers = new ConcurrentHashMap<>();
        broadcastSubscribers = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() { // No need for sync because Guaranteed by the JVM during class loading
        return INSTANCE;
    }

    @Override
    public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        // add new event if needed, and than add m to the matched event
        eventSubscribers.putIfAbsent(type, new CopyOnWriteArrayList<MicroService>());// Thread-safe variant of arrayList
        eventSubscribers.get(type).add(m);
    }

    @Override
    public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        // add new Broadcast if needed, and than add m to the matched Broadcast
        broadcastSubscribers.putIfAbsent(type, new CopyOnWriteArrayList<>());
        broadcastSubscribers.get(type).add(m);

    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        /*
         * List<MicroService> subscribers = eventSubscribers.getOrDefault(e.getClass(),
         * new CopyOnWriteArrayList<MicroService>());
         * if (subscribers.isEmpty()) {
         * return null;
         * }
         */
        return null;
    }

    @Override
    public synchronized void register(MicroService m) {

        messageQueues.putIfAbsent(m, new LinkedBlockingQueue<>());

    }

    @Override
    public void unregister(MicroService m) {
        // TODO Auto-generated method stub

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        return messageQueues.get(m).take();
        // since blockingQueue is a thread safe class, we don't need "wait()" method.
        // in addition, BlockingQueue throws interupts.
    }
}
