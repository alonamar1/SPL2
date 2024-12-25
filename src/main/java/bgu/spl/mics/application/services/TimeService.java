package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int TickTime;
    private int Duration;
    private int currentTick;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.currentTick = 1; // Initialize the current tick to 1. as the first tick is 1.
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        while (Duration > 0) {
            try {
                sendBroadcast(new TickBroadcast(this.currentTick)); // Broadcast the current tick.
                Duration--;
                this.currentTick++;
                Thread.sleep(TickTime*1000); // Wait for the specified tick time, in milliseconds.
            } catch (InterruptedException e) { // TODO: Handle the case where the thread was interrupted.
                System.out.println("TimeService was interrupted.");
                e.printStackTrace();
            }
        }
        sendBroadcast(new TerminatedBroadcast("TimeService")); // Broadcast a terminated message.
        terminate(); // Terminate the service after the specified duration.
    }
}
