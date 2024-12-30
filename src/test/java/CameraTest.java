import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.ReadCameraData;
import bgu.spl.mics.application.CameraDataPair;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;

public class CameraTest {

    private static class TestMicroService extends CameraService {

        private final CountDownLatch latch;
        private Camera cam;

        public TestMicroService(Camera cam, CountDownLatch latch) {
            super(cam);
            this.latch = latch;
        }

        public Camera getCamera() {
            return cam;
        }

        @Override
        protected void initialize() {
            super.initialize();
            latch.countDown();
        }
    }

    @Test
    public void setUp() {
        System.out.print("test has started");
        ReadCameraData r = new ReadCameraData();
        List<CameraDataPair> allData = r.cameraDataJsonToList("C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json");
        Camera camera1 = new Camera(1, 1, r.readCameraData(allData, "camera1"));
        Camera camera2 = new Camera(1, 2, r.readCameraData(allData, "camera2"));
        int statisticalfolder1 = 0; 
        StatisticalFolder.getInstance().getNumDetectedObjects();
        int statisticalfolder2 = 0;
        List<StampedDetectedObjects> detectedObjectsList1 = camera1.getDetectedObjectsList();
        List<StampedDetectedObjects> detectedObjectsList2 = camera2.getDetectedObjectsList();
        CameraService cam1 = new CameraService(camera1);
        CameraService cam2 = new CameraService(camera2);
        for (int i = 1; i <= detectedObjectsList1.size(); i++) {
            DetectedObjectsEvent event1 = cam1.getcamera().handleTick(i);
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            assertTrue(statisticalfolder1 - statisticalfolder2 == event1.getDetectedObject().size());
            statisticalfolder2 = statisticalfolder1;
            List<DetectedObject> tempList = detectedObjectsList1.get(i-1).getDetectedObject();
            List<DetectedObject> d = event1.getDetectedObject();
            boolean b = (tempList.size() == event1.getDetectedObject().size());
            assertTrue(b);
            if (b) {
                for (int j = 0; j < tempList.size(); j++) {
                    assertTrue(tempList.get(j).getDescreption().equals(event1.getDetectedObject().get(j).getDescreption()));
                    if (tempList.get(j).getID().equals("ERROR"))
                        assertTrue(camera1.getStatus() == STATUS.ERROR);
                }
            }
        }
        for (int i = 1; i <= detectedObjectsList2.size(); i++) {
            DetectedObjectsEvent event2 = cam2.getcamera().handleTick(i);
            statisticalfolder1 = StatisticalFolder.getInstance().getNumDetectedObjects();
            assertTrue(statisticalfolder1 - statisticalfolder2 == event2.getDetectedObject().size());
            statisticalfolder2 = statisticalfolder1;
            List<DetectedObject> tempList = detectedObjectsList2.get(i-1).getDetectedObject();
            boolean b = (tempList.size() == event2.getDetectedObject().size());
            assertTrue(b);
            if (b) {
                for (int j = 0; j < tempList.size(); j++) {
                    assertTrue(tempList.get(j).getDescreption().equals(event2.getDetectedObject().get(j).getDescreption()));
                    if (tempList.get(j).getDescreption().equals("ERROR"))
                        assertTrue(camera2.getStatus() == STATUS.ERROR);
                }
            }
        }
        System.out.print("finito");
    }

    public static void main(String[] args) {

    }
}

