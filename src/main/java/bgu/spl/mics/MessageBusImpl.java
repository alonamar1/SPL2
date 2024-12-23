package bgu.spl.mics;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
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
    private final Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers;
    // Broadcast subscriptions
    private final Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers;
    // Map to store futures for each event
    private final ConcurrentHashMap<Event<?>, Future<?>> eventFutures;

    private MessageBusImpl() {
        messageQueues = new ConcurrentHashMap<>();
        eventSubscribers = new ConcurrentHashMap<>();
        broadcastSubscribers = new ConcurrentHashMap<>();
        eventFutures = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() { // No need for sync because Guaranteed by the JVM during class loading
        return INSTANCE;
    }

    @Override
    public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        // add new event if needed, and than add m to the matched event
        eventSubscribers.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());// Thread-safe variant of arrayList
        if (!eventSubscribers.get(type).contains(m)) {
            eventSubscribers.get(type).add(m);
        }
    }

    @Override
    public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        // add new Broadcast if needed, and than add m to the matched Broadcast
        broadcastSubscribers.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());
        if (!broadcastSubscribers.get(type).contains(m)) {
            broadcastSubscribers.get(type).add(m);
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        // Resolve the future associated with the event
        Future<T> future = (Future<T>) eventFutures.get(e);
        if (future != null) {
            future.resolve(result);
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        // Send the broadcast message to all subscribed MicroServices
        LinkedBlockingQueue<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
        if (subscribers != null) {
            for (MicroService m : subscribers) {
                try {
                    messageQueues.get(m).put(b);
                } catch (InterruptedException e) {
                    System.out.println("Send Brodcast Interrupt: " + Thread.currentThread().getName());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        LinkedBlockingQueue<MicroService> queueFromEvent = eventSubscribers.get(e.getClass());
        if (queueFromEvent == null || queueFromEvent.isEmpty()) {
            return null;
        }
        MicroService m = queueFromEvent.poll();
        messageQueues.get(m).add(e);
        try {
            queueFromEvent.put(m);
        } catch (InterruptedException error) {
            System.out.println("Send Event Interrupt: " + Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
        Future<T> result = new Future<>();
        eventFutures.put(e, result);
        return result;
    }

    @Override
    public synchronized void register(MicroService m) {
        messageQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) { // לבדוק האם צריך sync
        BlockingQueue<Message> bq = messageQueues.get(m);
        messageQueues.get(m).remove();
        while (!bq.isEmpty()) { //מעבירים את ההודעות לניתוב מחדש. לבדוק אם יש צורך 
            Message msg = bq.poll(); //לחשוב האם צריך פול או טייק
            if (msg instanceof Event) {
                sendEvent((Event) msg); 
            }else {
                sendBroadcast((Broadcast) msg);
            }
        }
        eventSubscribers.values().forEach(queue -> queue.remove(m)); // Remove from event subscriptions

        broadcastSubscribers.values().forEach(queue -> queue.remove(m)); // Remove from broadcast subscriptions
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        return messageQueues.get(m).take();
        // since blockingQueue is a thread safe class, we don't need "wait()" method.
        // in addition, BlockingQueue throws interupts.
    }
}
