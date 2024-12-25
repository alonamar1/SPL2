package bgu.spl.mics.application.services;

import java.util.concurrent.Future;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.TrackedObject;

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
    private 

    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use
     * to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LiDarService");
        this.liDarTracker = liDarTracker;
    }

    /**
     * Initializes the LiDarService. Registers the service to handle
     * DetectObjectsEvents and TickBroadcasts, and sets up the necessary
     * callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectsEvent.class, (DetectedObjectsEvent detectedObjectsEvent) -> {
            TrackedObjectEvent event = new TrackedObjectEvent();
            for (int i = 0; i < detectedObjectsEvent.getDetectedObject().size(); i++) {
                TrackedObject trackedObject = liDarTracker.getTrackedObject(detectedObjectsEvent.getDetectedObject().get(i));
                if (trackedObject != null) {
                    event.addTrackedObject(trackedObject);
                }
            }
            Future<Boolean> future = (Future<Boolean>) sendEvent(event);
            try {
                if (future.get() == false) {
                    //TODO: Handle the case where the event was not completed successfully.
                }
            } catch (Exception e) {
                e.printStackTrace(); // TODO: Handle the case where the future was interrupted.
                sendBroadcast(new CrashedBroadcast("LiDar"));
            }
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
