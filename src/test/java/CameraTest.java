import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.Configurations.CameraDataPair;
import bgu.spl.mics.application.Configurations.ReadCameraData;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;

public class CameraTest {
/*
    private static class TestMicroService extends CameraService {

        private Camera cam;
        public TestMicroService(Camera cam, CountDownLatch latch) {
            super(cam, latch);
        }

        public Camera getCamera() {
            return cam;
        }

        @Override
        protected void initialize() {
            super.initialize();
        }
    }

    @Test
    public void setUp() {
        System.out.println("test has started");
        //create a new object to call the relavant methods
        ReadCameraData r = new ReadCameraData();
        //create a list with all the data from the json
        List<CameraDataPair> allData = r.cameraDataJsonToList("C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json");
        //creating cameras for the test
        Camera camera1 = new Camera(1, 1, r.readCameraData(allData, "camera1"));
        Camera camera2 = new Camera(1, 2, r.readCameraData(allData, "camera2"));
        //initialize ints for tests
        int statisticalfolder1 = 0, statisticalfolder2 = 0;
        //create lists to check thats the DetectedObjectEvent got the excpacted data
        List<StampedDetectedObjects> detectedObjectsList1 = camera1.getDetectedObjectsList();
        List<StampedDetectedObjects> detectedObjectsList2 = camera2.getDetectedObjectsList();
        
        CameraService cam1 = new CameraService(camera1);
        CameraService cam2 = new CameraService(camera2);

        for (int i = 1; i <= detectedObjectsList1.size(); i++) {
            //create event for the relevant tick
            DetectedObjectsEvent event1 = cam1.getcamera().handleTick(i);
            //checks that the StatisticalFolder updated the relevant count
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            //save the list from the event
            List<DetectedObject> tempList = detectedObjectsList1.get(i-1).getDetectedObject();
            if (event1==null){
                int error=0;
                for (int j = 0; j < tempList.size(); j++) {
                    //check that an error indeed occured
                    if (tempList.get(j).getID().equals("ERROR")){
                        System.out.println("ERROR");
                        //when error, exit the loop
                        error = 1;
                    }    
                }
                if (error == 1) {break;}
            }
            assertTrue(statisticalfolder1 - statisticalfolder2 == event1.getDetectedObject().size());
            //checks first the sizes are equal
            assertTrue(tempList.size() == event1.getDetectedObject().size());
            statisticalfolder2 = statisticalfolder1; 
            for (int j = 0; j < tempList.size(); j++) {
                //checks the strings are matched
                assertTrue(tempList.get(j).getDescreption().equals(event1.getDetectedObject().get(j).getDescreption()));
            }
        }

        for (int i = 1; i <= detectedObjectsList2.size(); i++) {
            //create event for the relevant tick
            DetectedObjectsEvent event2 = cam2.getcamera().handleTick(i);
            //checks that the StatisticalFolder updated the relevant count
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            //save the list from the event
            List<DetectedObject> tempList = detectedObjectsList2.get(i-1).getDetectedObject();
            if (event2==null){
                int error=0;
                for (int j = 0; j < tempList.size(); j++) {
                    //check that an error indeed occured
                    if (tempList.get(j).getID().equals("ERROR")){
                        System.out.println("ERROR");
                        //when error, exit the loop
                        error = 1;
                    }    
                }
                if (error == 1) {break;}
            }
            assertTrue(statisticalfolder1 - statisticalfolder2 == event2.getDetectedObject().size());
            //checks first the sizes are equal
            assertTrue(tempList.size() == event2.getDetectedObject().size());
            statisticalfolder2 = statisticalfolder1; 
            for (int j = 0; j < tempList.size(); j++) {
                //checks the strings are matched
                assertTrue(tempList.get(j).getDescreption().equals(event2.getDetectedObject().get(j).getDescreption()));

            }
        }
        System.out.print("fff");
    } */

}

