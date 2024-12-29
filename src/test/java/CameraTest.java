import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.services.CameraService;



public class CameraTest {

    private static class TestMicroService extends CameraService {
        private final CountDownLatch lanch;
        private Camera cam;

        public TestMicroService(Camera cam, CountDownLatch lanch) {
            super(cam);
            this.lanch = lanch;
        }

        public Camera getCamera()
        {
            return cam;
        }

        @Override
        protected void initialize() {
            super.initialize();
            lanch.countDown();
        }
    }

    @Test
    public void setUp()
    {
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

        Camera camera = new Camera(1, 2, "C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json");
        CameraService cam1= new CameraService(camera);
        DetectedObjectsEvent event = cam1.getcamera().handleTick(1);
        for (DetectedObject d : event.getDetectedObject())
        {
            System.out.println("id: " + d.getID() +", description: " + d.getDescreption());
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
}
