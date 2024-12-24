package bgu.spl.mics.application.services;

import java.util.List;
import java.util.concurrent.Future;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker liDarTracker;
    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use to
     *                     process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LiDar");
        this.liDarTracker = liDarTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectsEvent.class, (DetectedObjectsEvent detectedObjectsEvent) -> {
            // TODO Implement this
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            // TODO Implement this
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // TODO: Handle the case where the service was terminated.
            // terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            //
            terminate();
        });
    }
}
