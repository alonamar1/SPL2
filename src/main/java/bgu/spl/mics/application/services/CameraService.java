package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import java.util.List;
import java.util.concurrent.Future;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    

    private final Camera camera;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera");
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            List<DetectedObjectsEvent> detectedList = camera.handleTick(tick.getTick());
            if(detectedList != null){
                Future<Boolean> future;
                for (DetectedObjectsEvent detectedObjectsEvent : detectedList) {
                    future = (Future<Boolean>) sendEvent(detectedObjectsEvent);
                    try {
                        if (future.get() == false)  {
                            //TODO: Handle the case where the event was not completed successfully.
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // TODO: Handle the case where the future was interrupted.
                        sendBroadcast(new CrashedBroadcast("Camera"));
                    }
                }
            }
        } );
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // TODO: Handle the case where other service was terminated.
            // if the terminated service is TimeService, terminate the CameraService.
            if (terminated.getSenderId().equals("TimeService")) {
                sendBroadcast(new TerminatedBroadcast("Camera"));
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            terminate();
        });

    }
}
