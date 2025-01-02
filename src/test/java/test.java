import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import bgu.spl.mics.application.objects.Pose;
import org.junit.jupiter.api.BeforeEach;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.ReadyToProcessPair;
import bgu.spl.mics.application.objects.TrackedObject;

public class test {
/* 
private FusionSlam fusionSlam;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance();

    }

public void testProcessReadyToProcessPair() {
    // Create a pose

    Pose[] poses = new Pose[10];
    poses[0] = new Pose(1,1,45,1);
    poses[1] = new Pose(1.3f, 2.6f, 145, 2);
    poses[2] = new Pose(-1f, -1.16f, 99, 3);
    poses[3] = new Pose(0f, 0f, 100, 4);
    poses[4] = new Pose(3.1f, -3.1f, 0, 5);
    poses[5] = new Pose(-1.21f, 2.14f, 360, 6);
    poses[6] = new Pose(0f, 2.5f, 712, 7);
    poses[7] = new Pose(0f, -1.12f, 266, 8);
    poses[8] = new Pose(3.6f, 0f, 77, 9);
    poses[9] = new Pose(-2.91f, 0f, 333, 10);

    
    



    // Create cloud points for the tracked object
    CloudPoint cp1 = new CloudPoint(1.0, 1.0);
    CloudPoint cp2 = new CloudPoint(2.0, 2.0);
    List<CloudPoint> cloudPoints = Arrays.asList(cp1, cp2);

    // Create a tracked object
    TrackedObject trackedObject = new TrackedObject("1", 1, "Test Object", cloudPoints);

    // Create a tracked object event
    TrackedObjectEvent trackedObjectEvent = new TrackedObjectEvent("1", 1);
    trackedObjectEvent.addTrackedObject(trackedObject);

    // Add the pose and tracked object event to FusionSlam
    fusionSlam.getPoses().add(pose);
    fusionSlam.getTrackedObjectsReciv().add(trackedObjectEvent);

    // Create a ReadyToProcessPair
    ReadyToProcessPair<Pose, TrackedObjectEvent> pair = new ReadyToProcessPair<>(pose, trackedObjectEvent);

    // Process the pair
    LandMark result = fusionSlam.ProcessReadyToProcessPair(pair);

    // Verify the result
    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("Test Object", result.getDescription());
    assertEquals(2, result.getCoordinates().size());
} */
}