package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private FusionSlam fusionSlam; // The FusionSLAM object responsible for managing the global map.
    private int cameraAmount; // represent the number of active cameras
    private int LidarWorkerAmount; // represent the number of active lidar worker
    private boolean poseServiceOn;
    private boolean timeServiceOn;

    private final CountDownLatch lanch;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam, int cameraAmount, int LidarWorkerAmount, CountDownLatch lanch) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.LidarWorkerAmount = LidarWorkerAmount;
        this.cameraAmount = cameraAmount;
        this.poseServiceOn = true;
        this.timeServiceOn = true;
        this.lanch = lanch;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        // TickEvent - TrackedObjectEvent
        subscribeEvent(TrackedObjectEvent.class, (TrackedObjectEvent TrackedObjectEvent) -> {
            ReadyToProcessPair<Pose, TrackedObjectEvent> toProcess = this.fusionSlam
                    .checkReadyToProcess(TrackedObjectEvent); // Check if the tracked object is ready to be processed.
            if (toProcess != null) {
                // Process the pair of pose and every tracked object.
                this.fusionSlam.ProcessReadyToProcessPair(toProcess);
            } else {
                // Add the new tracked object event to the list.
                this.fusionSlam.getTrackedObjectsReciv().add(TrackedObjectEvent);
            }
        });

        // TickEvent - PoseEvent
        subscribeEvent(PoseEvent.class, (PoseEvent PoseEvent) -> {
            List<ReadyToProcessPair<Pose, TrackedObjectEvent>> toProcess = this.fusionSlam
                    .checkReadyToProcess(PoseEvent.getPose()); // Check if the pose is ready to be processed.
            if (!toProcess.isEmpty()) {
                for (ReadyToProcessPair<Pose, TrackedObjectEvent> pair : toProcess) {
                    this.fusionSlam.ProcessReadyToProcessPair(pair);
                }
            } else {
                this.fusionSlam.getPoses().add(PoseEvent.getPose()); // Add the new pose to the list.
            }
        });

        // TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            // TODO: בודק מתי צריך לסיים את כל התוכנית
            // if need to finish
            //this.checkIfRunning(); // צריך לבדוק גם פה ??
            // if (!this.fusionSlam.getRunning()) {
            //     // sendBroadcast(new TerminatedBroadcast("FusionSlam"));
            // }
        });

        // TerminatedBroadCast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            if (terminated.getSenderId().equals("Camera")) {
                this.cameraAmount--;
            } else if (terminated.getSenderId().equals("LiDarWorker")) {
                this.LidarWorkerAmount--;
            } else if (terminated.getSenderId().equals("PoseService")) {
                this.poseServiceOn = false;
            } else if (terminated.getSenderId().equals("TimeService")) {
                this.timeServiceOn = false;
            }
            this.checkIfRunning();
            if (!this.fusionSlam.getRunning()) {
                this.MakeOutputFileRegularState();
                terminate();
            }
        });

        // CrashedBroadCast
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            this.fusionSlam.setRunning(false);
            this.MakeOutputFileERRORState(crashed.getreasonForCrash(), crashed.getSenderId());
            terminate();
        });

        lanch.countDown();
    }

    /**
     * Check if the fusionSlam need to finish the program
     */
    public void checkIfRunning() {
        // All the sensor finishs, finish simulation
        if (cameraAmount == 0 && LidarWorkerAmount == 0 && !this.poseServiceOn)
            this.fusionSlam.setRunning(false);
        // TimeService stops, the program need to terminated
        else if (!this.timeServiceOn)
            this.fusionSlam.setRunning(false);
    }

    /**
     * Generates a JSON file representing the regular state of the system and saves
     * it to a directory.
     * The output includes statistical data gathered from the system.
     */
    public void MakeOutputFileRegularState() {
        // Get an instance of the StatisticalFolder
        StatisticalFolder stats = StatisticalFolder.getInstance();

        // Create an OutputData object to store statistical data
        OutputData outputData = new OutputData(
                stats.getRuntime(),
                stats.getNumDetectedObjects(),
                stats.getNumTrackedObjects(),
                stats.getNumLandmarks(),
                this.fusionSlam.getLandmarks());

        // Initialize Gson for pretty-printing the JSON output
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            // Define the directory for saving the output file
            File directory = new File("C:\\Users\\meire\\Documents\\Third Semster\\SPL2\\Skeleton\\example_input");

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Define the output file path and write the JSON data to the file
            try (FileWriter writer = new FileWriter(
                    "C:\\Users\\meire\\Documents\\Third Semster\\SPL2\\Skeleton\\example_input\\output_file_Test.json")) {
                gson.toJson(outputData, writer);
            }
        } catch (IOException e) {
            // Print stack trace if an I/O error occurs
            e.printStackTrace();
        }
    }

    /**
     * Creates an error state output file in JSON format and returns an
     * `OutputDataErrorState` object.
     * 
     * @param error      The error message to be included in the output.
     * @param sensorName The name of the sensor that encountered the error.
     */
    public void MakeOutputFileERRORState(String error, String sensorName) {
        // Get an instance of the StatisticalFolder
        StatisticalFolder stats = StatisticalFolder.getInstance();

        // Create an OutputData object
        OutputData statistics = new OutputData(
                stats.getRuntime(),
                stats.getNumDetectedObjects(),
                stats.getNumTrackedObjects(),
                stats.getNumLandmarks(),
                this.fusionSlam.getLandmarks());

        // Get an instance of the SaveStateFolder
        SaveStateFolder saveStatus = SaveStateFolder.getInstance();
        // Create an OutputDataErrorState object containing error details and
        // statistical data
        OutputDataErrorState output = new OutputDataErrorState(
                error,
                sensorName,
                saveStatus.getLastCameraObjects(),
                saveStatus.getLastLiDarEvent(),
                saveStatus.getPrevPoses(),
                statistics);

        // Initialize Gson for pretty-printing the JSON output
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            // TODO: change to protable dir
            // Define the directory for saving the output file
            File directory = new File("C:\\Users\\meire\\Documents\\Third Semster\\SPL2\\Skeleton\\example_input");

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Define the output file path and write the JSON data to the file
            try (FileWriter writer = new FileWriter(
                    "C:\\Users\\meire\\Documents\\Third Semster\\SPL2\\Skeleton\\example_input\\output_Test_ERROR.json")) {
                gson.toJson(output, writer);
            }
        } catch (IOException e) {
            // Print stack trace if an I/O error occurs
            e.printStackTrace();
        }
    }


    // -----------------------Inner Classes-----------------------
    /**
     * This class is used to make the output file in regular state.
     */
    private class OutputData {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private List<LandMark> landMarks;

        public OutputData(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks,
                List<LandMark> landMarks) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.landMarks = landMarks;
        }
    }

    /**
     * This class is used to make the output file in ERROR state.
     */
    private class OutputDataErrorState {
        private String error;
        private String faultySensor;
        private List<DetectedObjectsEvent> lastCamerasFrame;
        private List<TrackedObjectEvent> lastLiDarWorkerTrackersFrame;
        private List<Pose> poses;
        private OutputData statistics;

        public OutputDataErrorState(String error, String faultySens, List<DetectedObjectsEvent> lastCamerasFrame,
                List<TrackedObjectEvent> lastLiDarWorkerTrackersFrame, List<Pose> poses, OutputData statistics) {
            this.error = error;
            this.faultySensor = faultySens;
            this.lastCamerasFrame = lastCamerasFrame;
            this.lastLiDarWorkerTrackersFrame = lastLiDarWorkerTrackersFrame;
            this.poses = poses;
            this.statistics = statistics;
        }
    }

    // -----------------------Tests------------------------------
/* 
    public void testMakeOutputFile() {
        // Simulate some data
        StatisticalFolder stats = StatisticalFolder.getInstance();
        stats.incrementRuntime();
        stats.incrementNumDetectedObjects(5);
        stats.incrementNumTrackedObjects(3);
        stats.incrementNumLandmarks();

        // Add some landmarks to fusionSlam
        List<CloudPoint> coordinates = new LinkedList<>();
        coordinates.add(new CloudPoint(1.0, 2.0));
        coordinates.add(new CloudPoint(3.0, 4.0));
        List<LandMark> landmarks = new LinkedList<>();
        landmarks.add(new LandMark("Wall_1", "Wall", coordinates));
        landmarks.add(new LandMark("Wall_4", "Hi my name is", coordinates));
        this.fusionSlam.getLandmarks().addAll(landmarks);

        // Call the method to create the output file
        MakeOutputFileRegularState();
    }

    public void testMakeOutputFileERRORState() {
        // Simulate some data
        StatisticalFolder stats = StatisticalFolder.getInstance();
        stats.incrementRuntime();
        stats.incrementNumDetectedObjects(5);
        stats.incrementNumTrackedObjects(3);
        stats.incrementNumLandmarks();

        // Add some landmarks to fusionSlam
        List<CloudPoint> coordinates = new LinkedList<>();
        coordinates.add(new CloudPoint(1.0, 2.0));
        coordinates.add(new CloudPoint(3.0, 4.0));
        List<LandMark> landmarks = new LinkedList<>();
        landmarks.add(new LandMark("Wall_1", "Wall", coordinates));
        this.fusionSlam.getLandmarks().addAll(landmarks);

        // Simulate some poses
        List<Pose> poses = new LinkedList<>();
        float a = (float) 0.0755;
        poses.add(new Pose((float) 1.1, (float) 0.2, (float) 3.1, 0));
        poses.add(new Pose((float) 2, (float) -3.2076, (float) 0.0755, 2));
        SaveStateFolder.getInstance().updatePose(new Pose((float) 1.1, (float) 0.2, (float) 3.1, 0));
        SaveStateFolder.getInstance().updatePose(new Pose((float) 2, (float) -3.2076, (float) 0.0755, 2));

        // Simulate some detected objects events
        List<DetectedObject> detectedObjects = new LinkedList<>();
        detectedObjects.add(new DetectedObject("Wall_3", "Wall"));
        DetectedObjectsEvent detectedObjectsEvent = new DetectedObjectsEvent(1, detectedObjects, 12);
        SaveStateFolder.getInstance().updateCameraObjects(detectedObjectsEvent);

        // Simulate some tracked object events
        List<CloudPoint> trackedCoordinates = new LinkedList<>();
        trackedCoordinates.add(new CloudPoint(3.1, -0.4));
        trackedCoordinates.add(new CloudPoint(3.2, -0.2));
        TrackedObject trackedObject = new TrackedObject("Wall_3", 12, "Wall", trackedCoordinates);
        List<TrackedObject> trackedObjects = new LinkedList<>();
        TrackedObjectEvent trackedObjectEvent = new TrackedObjectEvent("LiDarWorkerTracker1", 12);
        trackedObjectEvent.addTrackedObject(trackedObject);
        SaveStateFolder.getInstance().updateLidarWorker(trackedObjectEvent);

        // Call the method to create the error output file
        MakeOutputFileERRORState("Camera disconnected", "Camera1");
    }

    public static void main(String[] args) {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        //FusionSlamService service = new FusionSlamService(fusionSlam, 2, 2);
        // service.testMakeOutputFile();
        //service.testMakeOutputFileERRORState();
    } */
}
