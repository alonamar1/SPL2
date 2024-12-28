package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
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
    private List<DetectedObjectsEvent> globalDetectedObjectsEvents;
    private int currentTick;

    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use
     * to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LiDarService");
        this.liDarTracker = liDarTracker;
        this.globalDetectedObjectsEvents = new LinkedList<DetectedObjectsEvent>();
        this.currentTick = 0;
    }

    /**
     * Initializes the LiDarService. Registers the service to handle
     * DetectObjectsEvents and TickBroadcasts, and sets up the necessary
     * callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectsEvent.class, (DetectedObjectsEvent detectedObjectsEvent) -> {
            globalDetectedObjectsEvents.add(detectedObjectsEvent);

        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            this.currentTick = tick.getTick();
            TrackedObjectEvent event;
            for (int i = 0; i < globalDetectedObjectsEvents.size(); i++) {
                if (tick.getTick() >= globalDetectedObjectsEvents.get(i).getTime() + liDarTracker.getFrequency()) {
                    event = new TrackedObjectEvent(globalDetectedObjectsEvents.get(i).getTime()); // create a new TrackedObjectEvent
                    for (int j = 0; j < globalDetectedObjectsEvents.get(i).getDetectedObject().size(); j++) {
                        TrackedObject trackedObject = liDarTracker.getTrackedObject(globalDetectedObjectsEvents.get(i).getDetectedObject().get(j), globalDetectedObjectsEvents.get(i).getTime());
                        // if the tracked object is not null, add it to the event
                        if (trackedObject != null) {
                            event.addTrackedObject(trackedObject);
                        }
                        else if (liDarTracker.getStatus() == STATUS.ERROR) {
                            // if the status of the LiDarTracker is ERROR, terminate the LiDarService
                            sendBroadcast(new CrashedBroadcast("LiDar"));
                            terminate();
                        }
                    }
                    Future<Boolean> future = (Future<Boolean>) sendEvent(event); //להבין מה הפיוצר רוצה ממני
                    MessageBusImpl.getInstance().complete(globalDetectedObjectsEvents.get(i), true); // complete the camera event
                    try {
                        if (future.get() == false) {
                            //TODO: Handle the case where the event was not completed successfully.
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // TODO: Handle the case where the future was interrupted.
                        //sendBroadcast(new CrashedBroadcast("LiDar"));
                    }
                }
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
                // TODO: Handle the case where other service was terminated.
            // if the terminated service is TimeService, terminate the LiDarService.
            if (terminated.getSenderId().equals("TimeService")) {
                sendBroadcast(new TerminatedBroadcast("LiDar"));
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            terminate();
        });
    }
}
