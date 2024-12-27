package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int TickTime; // The duration of each tick in seconds.
    private int Duration; // The total number of ticks before the service terminates.
    private int currentTick; // The current tick number.

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
        // לבדוק האם צריך להפסיק אותו במידה וfusion סיים את עבודתו
        while (Duration > 0) { // Continue broadcasting ticks until the duration is reached.
            try {
                sendBroadcast(new TickBroadcast(this.currentTick)); // Broadcast the current tick.
                Duration--;
                this.currentTick++;
                StatisticalFolder.getInstance().incrementRuntime(); // Increment the runtime in the statistical folder.
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
