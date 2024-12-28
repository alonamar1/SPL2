import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.messages.*;;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.services.CameraService;


public class CameraTest {
    
    public static class TestEvent1 implements Event<String> {

        private String message;

        public TestEvent1(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "TestEvent1";
        }

    }

    public static class terminate implements Event<String> {

        private String message;

        public terminate(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "terminate";
        }
    }

    public static class TestBroadcast1 implements Broadcast {

        private String message;

        public TestBroadcast1(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class TestEvent2 implements Event<String> {

        private String message;

        public TestEvent2(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return "TestEvent2";
        }
    }

    public static class TestBroadcast2 implements Broadcast {

        private String message;

        public TestBroadcast2(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class TestMicroService extends CameraService {

        private final CountDownLatch lanch;
        private Camera cam;

        public TestMicroService(Camera cam, CountDownLatch lanch) {
            super(cam);
            this.lanch = lanch;
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
        CountDownLatch lanch = new CountDownLatch(2); // 4 microservices
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

        MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast(1));        
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
