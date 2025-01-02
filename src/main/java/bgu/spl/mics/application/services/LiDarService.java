package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;

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
    private List<DetectedObjectsEvent> waitingDetectedObjectsEvents;
    private int currentTick;
    private int cameraAmount;
    private final CountDownLatch lanch;

    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use
     *                     to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker, int cameraAmount, CountDownLatch lanch) {
        super("LiDarService");
        this.liDarTracker = liDarTracker;
        this.waitingDetectedObjectsEvents = new LinkedList<DetectedObjectsEvent>();
        this.currentTick = 0;
        this.cameraAmount = cameraAmount;
        this.lanch = lanch;
    }

     /**
     * Check if need to terminate the LiDar Worker
     */
    public void checkIfFinish() {
        // Assuming the list is sorted by time
        if (cameraAmount == 0 && this.waitingDetectedObjectsEvents.isEmpty()) {
            this.liDarTracker.setStatus(STATUS.DOWN);
        }
    }

    /**
     * End the Thread
     */
    public void endLiDarWorker() {
        sendBroadcast(new TerminatedBroadcast("LiDarWorker"));
        terminate();
    }

    /**
     * Initializes the LiDarService. Registers the service to handle
     * DetectObjectsEvents and TickBroadcasts, and sets up the necessary
     * callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectsEvent.class, (DetectedObjectsEvent detectedObjectsEvent) -> {
            if (this.liDarTracker.getStatus() == STATUS.UP) {
                if (currentTick >= detectedObjectsEvent.getTime() + liDarTracker.getFrequency()) {
                    TrackedObjectEvent event = this.liDarTracker.prepareToSend(detectedObjectsEvent);
                    if (event.getTrackedObject().get(0).getDescription().equals("ERROR")) {
                        sendBroadcast(new CrashedBroadcast("LiDar",
                                "Sensor LidarWorker " + this.liDarTracker.getID() + " disconnected"));
                        terminate();
                    } else {
                        sendEvent(event);
                    }
                } else {
                    waitingDetectedObjectsEvents.add(detectedObjectsEvent);
                }
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            if (this.liDarTracker.getStatus() == STATUS.UP) {
                this.currentTick = tick.getTick();
                // by going from the last link to the first, we make sure that removing funcion
                // doesn't change the order of the next indexes.
                for (int i = waitingDetectedObjectsEvents.size() - 1; i >= 0; i--) {
                    if (tick.getTick() >= waitingDetectedObjectsEvents.get(i).getTime() + liDarTracker.getFrequency()) {
                        TrackedObjectEvent event = this.liDarTracker.prepareToSend(waitingDetectedObjectsEvents.get(i));
                        if (event.getTrackedObject().size() > 0) {
                            if (event.getTrackedObject().get(0).getDescription().equals("ERROR")) {
                                sendBroadcast(new CrashedBroadcast("LiDar",
                                        "Sensor LidarWorker " + this.liDarTracker.getID() + " disconnected"));
                                terminate();
                            } else {
                                sendEvent(event);
                            }
                            waitingDetectedObjectsEvents.remove(i);
                        }
                    }
                    this.checkIfFinish();
                    // If finish end service
                    if (this.liDarTracker.getStatus() == STATUS.DOWN) {
                        this.endLiDarWorker();
                    }
                }
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // if the terminated service is TimeService, terminate the LiDarService.
            if (terminated.getSenderId().equals("TimeService")) {
                this.liDarTracker.setStatus(STATUS.DOWN);
                this.endLiDarWorker();
            }
            // if camera is finished
            if (terminated.getSenderId().equals("Camera")) {
                this.cameraAmount--;
            }
            this.checkIfFinish();
            // If finish end service
            if (this.liDarTracker.getStatus() == STATUS.DOWN) {
                this.endLiDarWorker();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            terminate();
        });

        lanch.countDown();
    }
}
