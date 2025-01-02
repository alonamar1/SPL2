import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
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


    public List<StampedDetectedObjects> camera1Test()
    { 
    List<StampedDetectedObjects> camera1 = new LinkedList<>();
    String id1="wall";
    String id2= "door";
    String id3="chair";
    String id4 = "sofa";
    String id5= "TV";
    String id6 = "carpet";
    String id7 = "bin";
    String id8 = "ERROR";
    camera1.add(new StampedDetectedObjects(1));
    camera1.get(0).addDetectedObject(new DetectedObject(id5 + "1", id5));

    for (int i=2; i<=10; i++)
    {
        camera1.add(new StampedDetectedObjects(i));
        if (i%2==0) {camera1.get(i-1).addDetectedObject(new DetectedObject(id1 + "1", id1));}
        if (i%3==0) {
            camera1.get(i-1).addDetectedObject(new DetectedObject(id2 + "2", id2));
            camera1.get(i-1).addDetectedObject(new DetectedObject(id3 + i, id3)); }
        if (i%4==0) { 
            camera1.get(i-1).addDetectedObject(new DetectedObject(id4 + "1", id4));
            camera1.get(i-1).addDetectedObject(new DetectedObject(id5 + "1", id5));}
        if (i%5==0) { 
            camera1.get(i-1).addDetectedObject(new DetectedObject(id5 + "2", id5));
            camera1.get(i-1).addDetectedObject(new DetectedObject(id6 + "2", id6));
            camera1.get(i-1).addDetectedObject(new DetectedObject(id7 + "2", id7));}  
        if (i%6==0) { 
            camera1.get(i-1).addDetectedObject(new DetectedObject(id1 + "2", id1));
            camera1.get(i-1).addDetectedObject(new DetectedObject(id2 + "3", id2));}
        if (i%7==0) { 
                camera1.get(i-1).addDetectedObject(new DetectedObject(id7 + "1", id7));
                camera1.get(i-1).addDetectedObject(new DetectedObject(id5 + "1", id5));}
        if (i%8==0) { 
                camera1.get(i-1).addDetectedObject(new DetectedObject(id2 + "2", id2));
                camera1.get(i-1).addDetectedObject(new DetectedObject(id7 + "2", id7));
                camera1.get(i-1).addDetectedObject(new DetectedObject(id8, id8)); }
        if (i%9==0){
            camera1.get(i-1).addDetectedObject(new DetectedObject(id4 + "2", id4));
            camera1.get(i-1).addDetectedObject(new DetectedObject(id6 + "1", id6));}
    }
    /* 
    for (int i=0; i<10; i++)
    {
        System.out.println(camera1.get(i).getTime());
        for (DetectedObject d: camera1.get(i).getDetectedObject())
        {
            System.out.println(d.getID());
            System.out.println(d.getDescreption());
        }
        System.out.println();
        */

        return camera1;

    }


    @Test
    public void setUp() {

        CountDownLatch latch = new CountDownLatch(0); 
        System.out.println("test has started");
        //create a new object to call the relavant methods
        //creating cameras for the test
        Camera camera = new Camera(1, 1, camera1Test());
        //Camera camera2 = new Camera(1, 2, r.readCameraData(allData, "camera2"));
        //initialize ints for tests
        int statisticalfolder1 = 0, statisticalfolder2 = 0;
        //create lists to check thats the DetectedObjectEvent got the excpacted data
        List<StampedDetectedObjects> detectedObjectsList1 = camera.getDetectedObjectsList();
        //List<StampedDetectedObjects> detectedObjectsList2 = camera2.getDetectedObjectsList();
        
        CameraService cam1 = new CameraService(camera, latch);
        //CameraService cam2 = new CameraService(camera2, latch);

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
        System.out.println("yes");
/* 

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
          */       
        

        System.out.print("fff");
    }

    }


