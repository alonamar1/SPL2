package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for
 * unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {

    // instance is created when the class is loaded into memory
    private static class SingletonHolder {
        private static final MessageBusImpl INSTANCE = new MessageBusImpl();
    }

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
        return SingletonHolder.INSTANCE;
    }

    /* 
    * @pre type and m are not null
    * @post microservice m is subscribed to type
    * @inv if type already has microservice or m is already subscribe
        the method do nothing
    * @param type
    * @param m
    */
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        // add new event if needed, and than add m to the matched event
        eventSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>());
        if (!eventSubscribers.get(type).contains(m)) {
            eventSubscribers.get(type).add(m);
        }
    }

    /* 
    * @pre type and m are not null
    * @post microservice m is subscribed to type
    * @inv if type already has microservice or m is already subscribe
        the method do nothing
    * @param type
    * @param m
    */
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        // add new Broadcast if needed, and than add m to the matched Broadcast
        broadcastSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>());
        if (!broadcastSubscribers.get(type).contains(m)) {
            broadcastSubscribers.get(type).add(m);
        }
    }

    /* 
    * @pre e is not null
    * @post future resolved with result if really complete
    * @inv result is the needed retult to future
    * @param e
    * @param result
    */
    @Override
    public <T> void complete(Event<T> e, T result) {
        // Resolve the future associated with the event
        @SuppressWarnings("unchecked")
        Future<T> future = (Future<T>) eventFutures.get(e);
        if (future != null) {
            future.resolve(result);
        }
    }


    /* 
    * @pre b is not null
    * @post all the microservices that subscribed to the broadcast got the broadcast
    * @inv all the microservices that subsribed to the broadcast are in subsribers queue
        the method do nothing
    * @param b
    */
    @Override
    public void sendBroadcast(Broadcast b) {
        // Send the broadcast message to all subscribed MicroServices
        LinkedBlockingQueue<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
        if (subscribers != null) {
            for (MicroService m : subscribers) {
                try {
                    BlockingQueue<Message> temp = messageQueues.get(m);
                    if (temp != null) {
                        temp.put(b);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Send Brodcast Interrupt: " + Thread.currentThread().getName());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* 
    * @pre e is not null
    * @post one of the microservices that subscribed to the event got it
    * @inv the microservices that subscribed to the event get the event by round-robin loop
    * @param b
    * @return result
    */
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

    /* 
    * @pre m is not null
    * @post m is registered to messageQueues
    * @inv every microservice is registered only once
    * @param m
    */

    @Override
    public void register(MicroService m) {
        messageQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
    }


    /* 
    * @pre m is not null
    * @post m is unregistered and all the messages it has deleted
    * @inv all the messages are only in the queue
    * @param m
    */
    @Override
    public void unregister(MicroService m) {
        BlockingQueue<Message> bq;
        synchronized (messageQueues) {
            bq = messageQueues.remove(m);
        }
        if (bq != null) {
            while (!bq.isEmpty()) {
                Message msg = bq.poll();
                if (msg instanceof Event) {
                    eventFutures.remove(msg); // TODO: do we need to remove events from the list future events
                }
            }
        }
        synchronized (eventSubscribers) {
            eventSubscribers.values().forEach(queue -> queue.remove(m));
        }
        synchronized (broadcastSubscribers) {
            broadcastSubscribers.values().forEach(queue -> queue.remove(m));
        }
    }

    /* 
    * @pre m is not null
    * @post m got the messege from the queue
    * @inv awaitmessage is threadsafe
    * @param m
    * @ mmessageQueues.get(m).take() (the first message in the queue)
    */
    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (messageQueues.get(m) == null) {
            throw new IllegalStateException("MicroService was never registered");
        }
        // since blockingQueue is a thread safe class, we don't need "wait()" and
        // "notify()" methods
        return messageQueues.get(m).take();

    }

    // ------------------Getters for testing------------------
    public Map<MicroService, BlockingQueue<Message>> getMessageQueues() {
        return this.messageQueues;
    }

    public Map<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> getBroadcastSubscribers() {
        return this.broadcastSubscribers;
    }

    public Map<Class<? extends Event>, LinkedBlockingQueue<MicroService>> getEventSubscribers() {
        return this.eventSubscribers;
    }
}
