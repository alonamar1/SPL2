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

    @Test
    public void setUp() {

        CountDownLatch latch = new CountDownLatch(0); 
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
        
        CameraService cam1 = new CameraService(camera1, latch);
        CameraService cam2 = new CameraService(camera2, latch);

        for (int i = 1; i <= detectedObjectsList1.size(); i++) {
            //create event for the relevant tick
            DetectedObjectsEvent event1 = cam1.getcamera().handleTick(i);
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            //save the list from the relevan tick from the data base
            List<DetectedObject> tempList = detectedObjectsList1.get(i-1).getDetectedObject();
            if (event1 != null)
            {
                //checks there is indeed an error
                if (event1.getDetectedObject().get(0).getID().equals("ERROR"))
                    {
                        boolean findError = false;
                        for (DetectedObject d : event1.getDetectedObject())
                        {
                            if (d.getID().equals("ERROR"))
                                findError = true;
                        }
                        assertTrue(findError);
                        break;
                    }
                else{
                    //checks that the StatisticalFolder updated the relevant count
                    assertTrue(statisticalfolder1 - statisticalfolder2 == event1.getDetectedObject().size());
                    statisticalfolder2 = statisticalfolder1; 
                    //checks first the sizes are equal 
                    assertTrue(tempList.size() == event1.getDetectedObject().size());
                    for (int j = 0; j < tempList.size(); j++) {
                        //checks the strings are matched
                        assertTrue(tempList.get(j).getDescreption().equals(event1.getDetectedObject().get(j).getDescreption()));
                    }
                } 
            }
            //the only case event1 is null is when templist is empty
            else{
                assertTrue(tempList.isEmpty());

            }
        }


        for (int i = 1; i <= detectedObjectsList2.size(); i++) {
            //create event for the relevant tick
            DetectedObjectsEvent event2 = cam2.getcamera().handleTick(i);
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            //save the list from the relevan tick from the data base
            List<DetectedObject> tempList = detectedObjectsList2.get(i-1).getDetectedObject();
            if (event2 != null)
            {
                //checks there is indeed an error
                if (event2.getDetectedObject().get(0).getID().equals("ERROR"))
                    {
                        boolean findError = false;
                        for (DetectedObject d : event2.getDetectedObject())
                        {
                            if (d.getID().equals("ERROR"))
                                findError = true;
                        }
                        assertTrue(findError);
                        break;
                    }
                else{
                    //checks that the StatisticalFolder updated the relevant count
                    assertTrue(statisticalfolder1 - statisticalfolder2 == event2.getDetectedObject().size());
                    statisticalfolder2 = statisticalfolder1; 
                    //checks first the sizes are equal 
                    assertTrue(tempList.size() == event2.getDetectedObject().size());
                    for (int j = 0; j < tempList.size(); j++) {
                        //checks the strings are matched
                        assertTrue(tempList.get(j).getDescreption().equals(event2.getDetectedObject().get(j).getDescreption()));
                    }
                } 
            }
            //the only case event1 is null is when templist is empty
            else{
                assertTrue(tempList.isEmpty());

            }
        }
                 
        

        System.out.print("fff");
    } 

}

