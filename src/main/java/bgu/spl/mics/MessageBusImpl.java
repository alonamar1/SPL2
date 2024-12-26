package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
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
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        // add new event if needed, and than add m to the matched event
        eventSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>());
        if (!eventSubscribers.get(type).contains(m)) {
            eventSubscribers.get(type).add(m);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        // add new Broadcast if needed, and than add m to the matched Broadcast
        broadcastSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>());
        if (!broadcastSubscribers.get(type).contains(m)) {
            broadcastSubscribers.get(type).add(m);
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        // Resolve the future associated with the event
        @SuppressWarnings("unchecked")
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
        synchronized (queueFromEvent) {
            MicroService m = queueFromEvent.poll();
            if (m != null) {
                try {
                    messageQueues.get(m).put(e); // Add the event to the MicroService queue
                    queueFromEvent.put(m); // Return the MicroService to the queue
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.println("Send Event Problem " + Thread.currentThread().getName());
                    return null;
                }
                Future<T> result = new Future<>();
                eventFutures.put(e, result);
                return result;
            }
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        messageQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) { // לבדוק האם צריך sync
        BlockingQueue<Message> bq;
        synchronized (messageQueues) {
            bq = messageQueues.remove(m);
        }
        if (bq != null) {
            while (!bq.isEmpty()) {
                Message msg = bq.poll(); //לחשוב האם צריך פול או טייק
                eventFutures.get(msg).resolve(null); // return to every future resolved with null
                eventFutures.remove(msg); // remove events from the list future event האם צריך את זה???
            }
        }
        synchronized (eventSubscribers) {
            eventSubscribers.values().forEach(queue -> queue.remove(m));
        }
        synchronized (broadcastSubscribers) {
            broadcastSubscribers.values().forEach(queue -> queue.remove(m));
        }
        }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (messageQueues.get(m) == null) {
            return null;
        }

        return messageQueues.get(m).take();

        // since blockingQueue is a thread safe class, we don't need "wait()" method.
        // in addition, BlockingQueue throws interupts.
    }

    public Map<MicroService, BlockingQueue<Message>> getMessageQueues()
    {
        return this.messageQueues;
    }

    public Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> getBroadcastSubscribers()
    {
        return this.broadcastSubscribers;
    }

    public Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> getEventSubscribers()
    {
        return this.eventSubscribers;
    }
}
