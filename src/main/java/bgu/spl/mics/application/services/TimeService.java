package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int TickTime; // The duration of each tick in seconds.
    private int Duration; // The total number of ticks before the service terminates.
    private int currentTick; // The current tick number.

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.currentTick = 1; // Initialize the current tick to 1. as the first tick is 1.
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */
    @Override
    protected void initialize() {
        boolean needTofinish = false;
        while (Duration > 0 && !needTofinish) { // Continue broadcasting ticks until the duration is reached.
            try {
                // check if the fusion slam still working
                if (!FusionSlam.getInstance().getRunning()) {
                    needTofinish = true;
                    sendBroadcast(new TerminatedBroadcast("TimeService")); // Broadcast a terminated message.
                    terminate(); // Terminate the service after the specified duration.
                }
                // Broadcast the current tick.
                sendBroadcast(new TickBroadcast(this.currentTick));
                Duration--;
                this.currentTick++;
                // Increment the runtime in the statistical folder.
                StatisticalFolder.getInstance().incrementRuntime();
                // Wait for the specified tick time, in milliseconds.
                Thread.sleep(TickTime * 1000);
            } catch (InterruptedException e) {
                System.out.println("TimeService was interrupted.");
                e.printStackTrace();
            }
        }
        sendBroadcast(new TerminatedBroadcast("TimeService")); // Broadcast a terminated message.
        terminate(); // Terminate the service after the specified duration.
    }
}
