package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will eventually
 * be resolved to hold a result of some operation. The class allows Retrieving
 * the result once it is available.
 *
 * Only private methods may be added to this class. No public constructor is
 * allowed except for the empty constructor.
 */
public class Future<T> {

    private boolean available;
    private T result;

    /**
     * This should be the the only public constructor in this class.
     */
    public Future() {
        this.result = null;
        this.available = false;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until
     * it is available.
     *
     */
    public synchronized T get() {

        while (!available) {
            try {
                this.wait();
            } catch (InterruptedException e) {
				System.out.println("get Future Interrupt: " + Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            }
        }
        return this.result;

    }

    /**
     * Resolves the result of this Future object.
     */
    public synchronized void resolve(T result) {
        this.result = result;
        available = true;
        this.notifyAll();
    }

    /**
     * @return true if this object has been resolved, false otherwise
     */
    public synchronized boolean isDone() {
        return this.available;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout the maximal amount of time units to wait for the result.
     * @param unit	the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, wait for
     * {@code timeout} TimeUnits {@code unit}. If time has elapsed, return null.
     */
    public synchronized T get(long timeout, TimeUnit unit) { //try catch?? לחשוב על הכל שוב

        long toWait = unit.toMillis(timeout); //convert the time to wait to lont in miliseconds 
        long startTime = System.currentTimeMillis(); //checks the global time
        long remainTime = toWait;
        while (remainTime > 0) //while time hasn't finish
        {
            if (available) {
                return result;
            }
            try {
                this.wait(remainTime);
            } catch (InterruptedException e) {
				System.out.println("Get Future limit time Interrupt: " + Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            }
            long passedTime = System.currentTimeMillis() - startTime; //checks how huch time 
            remainTime = toWait - passedTime;

        }
        return null;
    }
}
