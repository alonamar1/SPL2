
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
/*
    private List<LandMark> landmarks;
    private List<Pose> poses;
    private List<TrackedObjectEvent> trackedObjectsReciv; // List of TrackedObjects received from the LiDAR workers.
    private List<ReadyToProcessPair<Pose, TrackedObjectEvent>> readyToProcessPairs;

    private static class TestFusionSlam extends FusionSlamService {

        private FusionSlam fusion;

        public TestFusionSlam(FusionSlam fusionSlam) {
            super(fusionSlam, 3, 4);
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

    } */

}
