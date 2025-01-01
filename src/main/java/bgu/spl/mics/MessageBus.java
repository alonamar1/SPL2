package bgu.spl.mics;

/**
 * The message-bus is a shared object used for communication between
 * micro-services.
 * It should be implemented as a thread-safe singleton.
 * The message-bus implementation must be thread-safe as
 * it is shared between all the micro-services in the system.
 * You must not alter any of the given methods of this interface.
 * You cannot add methods to this interface.
 */
public interface MessageBus {

    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * 
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     * @pre type != null
     * @pre m != null
     * @post m is now subscribed to receive Event of type {@code type}
     * @post if no micro-service has subscribed to {@code type} events, a new list is created.
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m);

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * 
     * @param type The type to subscribe to.
     * @param m    The subscribing micro-service.
     * @pre type != null
     * @pre m != null
     * @post m is now subscribed to receive Broadcasts of type {@code type}
     * @post if no micro-service has subscribed to {@code type} broadcasts, a new list is created.
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m);

    /**
     * Notifies the MessageBus that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * <p>
     * 
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @pre e != null
     * @pre The event {@code e} was taken from the queue of {@code m}.
     * @post If {@code e} was taken from the queue of {@code m}, then the future
     *       object associated with {@code e} is resolved with the result
     *       {@code result}.
     */
    <T> void complete(Event<T> e, T result);

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the
     * micro-services subscribed to {@code b.getClass()}.
     * <p>
     * 
     * @param b The message to added to the queues.
     * @pre b != null
     * @pre The message-bus is running.
     * @post The broadcast message {@code b} is added to the message queues of all
     *       the micro-services subscribed to {@code b.getClass()}.
     * @post If no micro-service has subscribed to {@code b.getClass()} the
     *       broadcast is not added to any queue.
     * @post The message is added to the queues of the micro-services in a
     *       round-robin fashion.
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the
     * micro-services subscribed to {@code e.getClass()} in a round-robin
     * fashion. This method should be non-blocking.
     * <p>
     * 
     * @param <T> The type of the result expected by the event and its corresponding
     *            future object.
     * @param e   The event to add to the queue.
     * @return {@link Future<T>} object to be resolved once the processing is
     *         complete,
     *         null in case no micro-service has subscribed to {@code e.getClass()}.
     * @pre e != null
     * @pre The message-bus is running.
     * @post if there is a micro-service subscribed to {@code e.getClass()} then the
     *       event {@code e} is sent to one of them.
     * @post if no micro-service has subscribed to {@code e.getClass()} then no
     *       message is sent.
     * @post The event {@code e} is added to the queue of a micro-service subscribed
     *       to {@code e.getClass()}.
     * @post If there is no micro-service subscribed to {@code e.getClass()} the
     *       method returns null.
     * @post If there is a micro-service subscribed to {@code e.getClass()} then the
     *       method returns a non-null {@link Future<T>} object.
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * 
     * @param m the micro-service to create a queue for.
     * @pre m != null
     * @pre !isRegistered(m)
     * @post isRegistered(m)
     * @post getQueue(m) != null
     * @post getQueue(m).size() == 0
     */
    void register(MicroService m);

    /**
     * Removes the message queue allocated to {@code m} via the call to
     * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
     * related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * 
     * @param m the micro-service to unregister.
     * @pre {@code m} is not null.
     * @pre isRegistered(m)
     * @post !isRegistered(m)
     * @post All the message queues allocated to {@code m} also deleted.
     * @post All references related to {@code m} are deleted from the message-bus.
     */
    void unregister(MicroService m);

    /**
     * Using this method, a <b>registered</b> micro-service can take message
     * from its allocated queue.
     * This method is blocking meaning that if no messages
     * are available in the micro-service queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code m} was never registered.
     * <p>
     * 
     * @param m The micro-service requesting to take a message from its message
     *          queue.
     * @return The next message in the {@code m}'s queue (blocking).
     * @throws InterruptedException  if interrupted while waiting for a message
     *                               to became available.'
     * @throws IllegalStateException if {@code m} was never registered.
     * 
     * @pre {@code m} is a registered micro-service.
     * @pre The message-bus is running.
     * @post The message is no longer in the queue.
     * @post If no messages are available, the method waits until a message becomes
     *       available.
     */
    Message awaitMessage(MicroService m) throws InterruptedException;

}
