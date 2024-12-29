
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;

public class CameraTest {

    private static class TestMicroService extends CameraService {

        private final CountDownLatch lanch;
        private Camera cam;

        public TestMicroService(Camera cam, CountDownLatch lanch) {
            super(cam);
            this.lanch = lanch;
        }

        public Camera getCamera() {
            return cam;
        }

        @Override
        protected void initialize() {
            super.initialize();
            lanch.countDown();
        }
    }

    @Test
    public void setUp() {
        System.out.print("test has started");
        /* 
        CountDownLatch lanch = new CountDownLatch(2); // 2 microservices
        Camera camera = new Camera(1, 2, "C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json");
        TestMicroService camSer1 = new TestMicroService(camera, lanch);
        TestMicroService camSer2 = new TestMicroService(camera, lanch);

        //initialize threads
        Thread thread1 = new Thread(camSer1); 
        Thread thread2 = new Thread(camSer2);

        //start threads and run the microservices
        thread1.start();
        thread2.start();

        //waits for the microservices to initialize
        try {
            lanch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */

        Camera camera1 = new Camera(1, 1, "./camera_data.json");
        Camera camera2 = new Camera(1, 2, "./camera_data.json");
        int statisticalfolder1 = 0; StatisticalFolder.getInstance().getNumDetectedObjects();
        int statisticalfolder2=0;
        List<StampedDetectedObjects> detectedObjectsList1 = camera1.getDetectedObjectsList();
        List<StampedDetectedObjects> detectedObjectsList2 = camera1.getDetectedObjectsList();
        CameraService cam1 = new CameraService(camera1);
        CameraService cam2 = new CameraService(camera2);
        for (int i=0; i<detectedObjectsList1.size(); i++)
        {
            DetectedObjectsEvent event1 = cam1.getcamera().handleTick(1);
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            assertTrue(statisticalfolder1 - statisticalfolder2 == event1.getDetectedObject().size());
            statisticalfolder2 = statisticalfolder1;
            List<DetectedObject> tempList = detectedObjectsList1.get(i).getDetectedObject();
            boolean b = (tempList.size()==event1.getDetectedObject().size());
            assertTrue(b);
            if (b)
            {
                for (int j=0; j<tempList.size(); j++)
                {
                    assertTrue(tempList.get(j).getDescreption().equals(event1.getDetectedObject().get(j).getDescreption()));
                    if (tempList.get(j).getDescreption().equals("ERROR"))
                        assertTrue(camera1.getStatus()==STATUS.ERROR);
                }  
            }
            

        }
        for (int i=0; i<detectedObjectsList2.size(); i++)
        {
            DetectedObjectsEvent event2 = cam2.getcamera().handleTick(1);
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            assertTrue(statisticalfolder1 - statisticalfolder2 == event2.getDetectedObject().size());
            statisticalfolder2 = statisticalfolder1;
            List<DetectedObject> tempList = detectedObjectsList2.get(i).getDetectedObject();
            boolean b = (tempList.size()==event2.getDetectedObject().size());
            assertTrue(b);
            if (b)
            {
                for (int j=0; j<tempList.size(); j++)
                {
                    assertTrue(tempList.get(j).getDescreption().equals(event2.getDetectedObject().get(j).getDescreption()));
                    if (tempList.get(j).getDescreption().equals("ERROR"))
                        assertTrue(camera2.getStatus()==STATUS.ERROR);
                }  
            }

        }

        
    }

        /* 
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */
    }

