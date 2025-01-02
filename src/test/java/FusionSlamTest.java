import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.Configurations.CameraDataPair;
import bgu.spl.mics.application.Configurations.PoseData;
import bgu.spl.mics.application.Configurations.ReadCameraData;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.ReadyToProcessPair;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;

public class FusionSlamTest {

    private List<LandMark> landmarks;
    private List<Pose> poses;
    private List<TrackedObjectEvent> trackedObjectsReciv; // List of TrackedObjects received from the LiDAR workers.
    private List<ReadyToProcessPair<Pose, TrackedObjectEvent>> readyToProcessPairs;

    /*
    private static class TestFusionSlam extends FusionSlamService {

        private FusionSlam fusion;

        public TestFusionSlam(FusionSlam fusionSlam) {
            super(fusionSlam, 3, 4, );
        }

        protected void initialize() {
            super.initialize();
        }
    }

    @Test
    public void setUp() {
        System.out.print("test has started");
        //אופציה א
        
        PoseData posedata = new PoseData();
        posedata.loadData("C:\\Users\\alona\\SPL2\\example_input_2\\pose_data.json");
        List<Pose> poseList = posedata.getPoses();
        LiDarWorkerTracker lidartracker = new LiDarWorkerTracker(id, frequency, trackedObjects);
        //Pose[] poses = new Pose[poseList.]
        ReadCameraData read = new ReadCameraData();
        List<CameraDataPair> allData = read.cameraDataJsonToList("C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json");
        int numberOfCameras = allData.size();
        String[] camerasKeys = new String[numberOfCameras];
        for (int i=0; i<numberOfCameras; i++)
        {
            camerasKeys[i] = allData.get(i).getCameraKey();
        } 
        for (int i=0; i<numberOfCameras; i++)
        {
            List<StampedDetectedObjects> currList = read.readCameraData(allData, camerasKeys[i]);
            List<TrackedObject> trackedObjectsList = new LinkedList<>();
            for (StampedDetectedObjects s : currList)
            {
                List<DetectedObject> tempList = s.getDetectedObject();
                for (DetectedObject d : tempList)
                {
                    trackedObjectsList.add()
                }
            }


        }
        TrackedObject[] objects = {};
        List<LandMark> expacLandMarks = null;
        FusionSlam fusion = FusionSlam.getInstance();
        List<ReadyToProcessPair<Pose, TrackedObjectEvent>> readyToProcessPairs = new LinkedList<>();
        for (int i = 0; i<poses.length; i++)
        {
            for (int j = 0; j<objects.length; j++)
            {
                if (objects[j].getTime() == poses[i].getTime())
                    readyToProcessPairs.add(new ReadyToProcessPair(poses[i], objects[j]));
            }
        }
        List<LandMark> landMarks = new LinkedList<>();
        for (int i=0; i<readyToProcessPairs.size(); i++)
        {
            landmarks.add(fusion.ProcessReadyToProcessPair(readyToProcessPairs.get(i)));
        }
        assertTrue(StatisticalFolder.getInstance().getNumDetectedObjects() == readyToProcessPairs.size());
        assertTrue(expacLandMarks.size() == landMarks.size());

        while (!expacLandMarks.isEmpty())
        {
            assertTrue(landmarks.contains(expacLandMarks.get(0)));
            expacLandMarks.remove(0);
            //לבדוק גם קלאוד פוינטס, איידי, תיאור
        }
        //להוסיף בדיקות על ארורים
         

        //אופציה ב
        Pose[] poses = {}; //להשלים מהדאטה שתהיה לי
        TrackedObject[] objects = {};
        FusionSlam fusion = FusionSlam.getInstance();
        List<ReadyToProcessPair<Pose, TrackedObjectEvent>> readyToProcessPairs = new LinkedList<>();
        for (int i = 0; i < poses.length; i++) {
            for (int j = 0; j < objects.length; j++) {
                if (objects[j].getTime() == poses[i].getTime()) {
                    readyToProcessPairs.add(new ReadyToProcessPair(poses[i], objects[j]));
                }
            }
        }
        List<LandMark> landMarks = new LinkedList<>();
        for (int i = 0; i < readyToProcessPairs.size(); i++) {
            landmarks.add(fusion.ProcessReadyToProcessPair(readyToProcessPairs.get(i)));
        }

        assertTrue(StatisticalFolder.getInstance().getNumDetectedObjects() == readyToProcessPairs.size());
        
        for (int i = 0; i<objects.length; i++)
        {
            for (int j = 0; j<landmarks.size(); j++)
            {
                if (objects[i].getId().equals(landmarks.get(j).getId()))
                {
                    if (objects[i].getDescription().equals(landmarks.get(j).getDescription()))
                    {
                        for (int k=0; k<landMarks.get(j).getCoordinates().size(); k++)
                        {
                            assertTrue(landMarks.get(j).getCoordinates().get(k).getX()==objects[i].getCoordinates().get(k).getX());
                            assertTrue(landMarks.get(j).getCoordinates().get(k).getY()==objects[i].getCoordinates().get(k).getY());
                        }
                    }
                }
            }
        }

    } 
        */
    /**
     * @pre pose and trackedObjectEvent are not null
     * @post result is not null and contains the correct id, description, and coordinates
     * @inv landmarks list size is incremented by 1 if a new landmark is added
     */
    @Test
    public void testProcessReadyToProcessPair() {
        // Arrange
        FusionSlam fusionSlam = FusionSlam.getInstance();
        Pose pose = new Pose(1.0f, 2.0f, 45.0f, 1);
        List<CloudPoint> cloudPoints = new LinkedList<>();
        cloudPoints.add(new CloudPoint(1.0, 1.0));
        cloudPoints.add(new CloudPoint(2.0, 2.0));
        TrackedObject trackedObject = new TrackedObject("1", 1, "Test Object", cloudPoints);
        List<TrackedObject> trackedObjects = new LinkedList<>();
        trackedObjects.add(trackedObject);
        TrackedObjectEvent trackedObjectEvent = new TrackedObjectEvent("1", 1);
        trackedObjectEvent.addTrackedObject(trackedObject);
        ReadyToProcessPair<Pose, TrackedObjectEvent> pair = new ReadyToProcessPair<>(pose, trackedObjectEvent);

        // Act
        LandMark result = fusionSlam.ProcessReadyToProcessPair(pair);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Object", result.getDescription());
        assertEquals(2, result.getCoordinates().size());
        // Adjust expected coordinates based on the calculations in FusionSlam
        assertEquals(1.0 * Math.cos(Math.toRadians(45.0)) - 1.0 * Math.sin(Math.toRadians(45.0)) + 1.0, result.getCoordinates().get(0).getX(), 0.01);
        assertEquals(1.0 * Math.sin(Math.toRadians(45.0)) + 1.0 * Math.cos(Math.toRadians(45.0)) + 2.0, result.getCoordinates().get(0).getY(), 0.01);
        assertEquals(2.0 * Math.cos(Math.toRadians(45.0)) - 2.0 * Math.sin(Math.toRadians(45.0)) + 1.0, result.getCoordinates().get(1).getX(), 0.01);
        assertEquals(2.0 * Math.sin(Math.toRadians(45.0)) + 2.0 * Math.cos(Math.toRadians(45.0)) + 2.0, result.getCoordinates().get(1).getY(), 0.01);
    }

    /**
     * @pre pose and trackedObjectEvent are not null
     * @post result is not null and contains the correct id, description, and coordinates
     * @inv landmarks list size is incremented by 1 if a new landmark is added
     */
    @Test
    public void testProcessMultipleReadyToProcessPairs() {
        // Arrange
        FusionSlam fusionSlam = FusionSlam.getInstance();
        Pose pose1 = new Pose(1.0f, 2.0f, 45.0f, 1);
        Pose pose2 = new Pose(2.0f, 3.0f, 90.0f, 2);
        List<CloudPoint> cloudPoints1 = new LinkedList<>();
        cloudPoints1.add(new CloudPoint(1.0, 1.0));
        cloudPoints1.add(new CloudPoint(2.0, 2.0));
        List<CloudPoint> cloudPoints2 = new LinkedList<>();
        cloudPoints2.add(new CloudPoint(3.0, 3.0));
        cloudPoints2.add(new CloudPoint(4.0, 4.0));
        TrackedObject trackedObject1 = new TrackedObject("1", 1, "Test Object 1", cloudPoints1);
        TrackedObject trackedObject2 = new TrackedObject("2", 2, "Test Object 2", cloudPoints2);
        TrackedObjectEvent trackedObjectEvent1 = new TrackedObjectEvent("1", 1);
        TrackedObjectEvent trackedObjectEvent2 = new TrackedObjectEvent("2", 2);
        trackedObjectEvent1.addTrackedObject(trackedObject1);
        trackedObjectEvent2.addTrackedObject(trackedObject2);
        ReadyToProcessPair<Pose, TrackedObjectEvent> pair1 = new ReadyToProcessPair<>(pose1, trackedObjectEvent1);
        ReadyToProcessPair<Pose, TrackedObjectEvent> pair2 = new ReadyToProcessPair<>(pose2, trackedObjectEvent2);

        // Act
        LandMark result1 = fusionSlam.ProcessReadyToProcessPair(pair1);
        LandMark result2 = fusionSlam.ProcessReadyToProcessPair(pair2);

        // Assert
        assertNotNull(result1);
        assertEquals("1", result1.getId());
        assertEquals("Test Object 1", result1.getDescription());
        assertEquals(2, result1.getCoordinates().size());
        // Adjust expected coordinates based on the calculations in FusionSlam
        assertEquals(1.0 * Math.cos(Math.toRadians(45.0)) - 1.0 * Math.sin(Math.toRadians(45.0)) + 1.0, result1.getCoordinates().get(0).getX(), 0.01);
        assertEquals(1.0 * Math.sin(Math.toRadians(45.0)) + 1.0 * Math.cos(Math.toRadians(45.0)) + 2.0, result1.getCoordinates().get(0).getY(), 0.01);
        assertEquals(2.0 * Math.cos(Math.toRadians(45.0)) - 2.0 * Math.sin(Math.toRadians(45.0)) + 1.0, result1.getCoordinates().get(1).getX(), 0.01);
        assertEquals(2.0 * Math.sin(Math.toRadians(45.0)) + 2.0 * Math.cos(Math.toRadians(45.0)) + 2.0, result1.getCoordinates().get(1).getY(), 0.01);

        assertNotNull(result2);
        assertEquals("2", result2.getId());
        assertEquals("Test Object 2", result2.getDescription());
        assertEquals(2, result2.getCoordinates().size());
        // Adjust expected coordinates based on the calculations in FusionSlam
        assertEquals(3.0 * Math.cos(Math.toRadians(90.0)) - 3.0 * Math.sin(Math.toRadians(90.0)) + 2.0, result2.getCoordinates().get(0).getX(), 0.01);
        assertEquals(3.0 * Math.sin(Math.toRadians(90.0)) + 3.0 * Math.cos(Math.toRadians(90.0)) + 3.0, result2.getCoordinates().get(0).getY(), 0.01);
        assertEquals(4.0 * Math.cos(Math.toRadians(90.0)) - 4.0 * Math.sin(Math.toRadians(90.0)) + 2.0, result2.getCoordinates().get(1).getX(), 0.01);
        assertEquals(4.0 * Math.sin(Math.toRadians(90.0)) + 4.0 * Math.cos(Math.toRadians(90.0)) + 3.0, result2.getCoordinates().get(1).getY(), 0.01);
    }

    /**
     * @pre pose and trackedObjectEvent are not null
     * @post result is null if no landmarks are added
     * @inv landmarks list size remains the same if no new landmarks are added
     */
    @Test
    public void testProcessReadyToProcessPairNoLandmarks() {
        // Arrange
        FusionSlam fusionSlam = FusionSlam.getInstance();
        Pose pose = new Pose(1.0f, 2.0f, 45.0f, 1);
        List<CloudPoint> cloudPoints = new LinkedList<>();
        TrackedObject trackedObject = new TrackedObject("1", 1, "Test Object", cloudPoints);
        TrackedObjectEvent trackedObjectEvent = new TrackedObjectEvent("1", 1);
        trackedObjectEvent.addTrackedObject(trackedObject);
        ReadyToProcessPair<Pose, TrackedObjectEvent> pair = new ReadyToProcessPair<>(pose, trackedObjectEvent);

        // Act
        LandMark result = fusionSlam.ProcessReadyToProcessPair(pair);

        // Assert
        assertNull(result);
        assertTrue(fusionSlam.getLandmarks().isEmpty());
    }

}
