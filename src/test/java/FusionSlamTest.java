import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.ReadyToProcessPair;
import bgu.spl.mics.application.objects.TrackedObject;

public class FusionSlamTest {

    @BeforeEach
    public void clearFusionSlamInstance() {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        fusionSlam.getLandmarks().clear();
        fusionSlam.getPoses().clear();
        fusionSlam.getTrackedObjectsReciv().clear();
        fusionSlam.setRunning(true);
    }

    /**
     * The test checks the process of a single ReadyToProcessPair.
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
        List<LandMark> result = fusionSlam.ProcessReadyToProcessPair(pair);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        LandMark landmark = result.get(0);
        assertEquals("1", landmark.getId());
        assertEquals("Test Object", landmark.getDescription());
        assertEquals(2, landmark.getCoordinates().size());
        // Adjust expected coordinates based on the calculations in FusionSlam
        assertEquals(1.0 * Math.cos(Math.toRadians(45.0)) - 1.0 * Math.sin(Math.toRadians(45.0)) + 1.0,
                landmark.getCoordinates().get(0).getX(), 0.01);
        assertEquals(1.0 * Math.sin(Math.toRadians(45.0)) + 1.0 * Math.cos(Math.toRadians(45.0)) + 2.0,
                landmark.getCoordinates().get(0).getY(), 0.01);
        assertEquals(2.0 * Math.cos(Math.toRadians(45.0)) - 2.0 * Math.sin(Math.toRadians(45.0)) + 1.0,
                landmark.getCoordinates().get(1).getX(), 0.01);
        assertEquals(2.0 * Math.sin(Math.toRadians(45.0)) + 2.0 * Math.cos(Math.toRadians(45.0)) + 2.0,
                landmark.getCoordinates().get(1).getY(), 0.01);
    }

    /**
     * The test checks the process of multiple ReadyToProcessPairs.
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
        List<LandMark> result1 = fusionSlam.ProcessReadyToProcessPair(pair1);
        List<LandMark> result2 = fusionSlam.ProcessReadyToProcessPair(pair2);

        // Assert
        assertNotNull(result1);
        assertEquals(1, result1.size());
        LandMark landmark1 = result1.get(0);
        assertEquals("1", landmark1.getId());
        assertEquals("Test Object 1", landmark1.getDescription());
        assertEquals(2, landmark1.getCoordinates().size());
        // Adjust expected coordinates based on the calculations in FusionSlam
        assertEquals(1.0 * Math.cos(Math.toRadians(45.0)) - 1.0 * Math.sin(Math.toRadians(45.0)) + 1.0,
                landmark1.getCoordinates().get(0).getX(), 0.01);
        assertEquals(1.0 * Math.sin(Math.toRadians(45.0)) + 1.0 * Math.cos(Math.toRadians(45.0)) + 2.0,
                landmark1.getCoordinates().get(0).getY(), 0.01);
        assertEquals(2.0 * Math.cos(Math.toRadians(45.0)) - 2.0 * Math.sin(Math.toRadians(45.0)) + 1.0,
                landmark1.getCoordinates().get(1).getX(), 0.01);
        assertEquals(2.0 * Math.sin(Math.toRadians(45.0)) + 2.0 * Math.cos(Math.toRadians(45.0)) + 2.0,
                landmark1.getCoordinates().get(1).getY(), 0.01);

        assertNotNull(result2);
        assertEquals(1, result2.size());
        LandMark landmark2 = result2.get(0);
        assertEquals("2", landmark2.getId());
        assertEquals("Test Object 2", landmark2.getDescription());
        assertEquals(2, landmark2.getCoordinates().size());
        // Adjust expected coordinates based on the calculations in FusionSlam
        assertEquals(3.0 * Math.cos(Math.toRadians(90.0)) - 3.0 * Math.sin(Math.toRadians(90.0)) + 2.0,
                landmark2.getCoordinates().get(0).getX(), 0.01);
        assertEquals(3.0 * Math.sin(Math.toRadians(90.0)) + 3.0 * Math.cos(Math.toRadians(90.0)) + 3.0,
                landmark2.getCoordinates().get(0).getY(), 0.01);
        assertEquals(4.0 * Math.cos(Math.toRadians(90.0)) - 4.0 * Math.sin(Math.toRadians(90.0)) + 2.0,
                landmark2.getCoordinates().get(1).getX(), 0.01);
        assertEquals(4.0 * Math.sin(Math.toRadians(90.0)) + 4.0 * Math.cos(Math.toRadians(90.0)) + 3.0,
                landmark2.getCoordinates().get(1).getY(), 0.01);
    }

    /**
     * The test checks the process of a single ReadyToProcessPair with no landmarks.
     */
    @Test
    public void testProcessReadyToProcessPairNoLandmarks() {
        // Arrange
        FusionSlam fusionSlam = FusionSlam.getInstance();
        Pose pose = new Pose(1.0f, 2.0f, 45.0f, 1);
        List<CloudPoint> cloudPoints = new LinkedList<>();
        TrackedObject trackedObject = new TrackedObject("3", 1, "Test Object", cloudPoints);
        TrackedObjectEvent trackedObjectEvent = new TrackedObjectEvent("3", 3);
        trackedObjectEvent.addTrackedObject(trackedObject);
        ReadyToProcessPair<Pose, TrackedObjectEvent> pair = new ReadyToProcessPair<>(pose, trackedObjectEvent);

        // Act
        List<LandMark> result = fusionSlam.ProcessReadyToProcessPair(pair);

        // Assert
        assertNull(result);
        assertTrue(fusionSlam.getLandmarks().isEmpty());
    }

    /**
     * The test checks the update of coordinates of an existing landmark.
     */
    @Test
    public void testUpdateExistingLandmarkCoordinates() {
        // Arrange
        FusionSlam fusionSlam = FusionSlam.getInstance();
        Pose pose = new Pose(1.0f, 2.0f, 45.0f, 1);
        List<CloudPoint> initialCloudPoints = new LinkedList<>();
        double initialX1 = (1.0 * Math.cos(Math.toRadians(45.0)) - 1.0 * Math.sin(Math.toRadians(45.0)) + 1.0);
        double initialY1 = (1.0 * Math.sin(Math.toRadians(45.0)) + 1.0 * Math.cos(Math.toRadians(45.0)) + 2.0);
        double initialX2 = (2.0 * Math.cos(Math.toRadians(45.0)) - 2.0 * Math.sin(Math.toRadians(45.0)) + 1.0);
        double initialY2 = (2.0 * Math.sin(Math.toRadians(45.0)) + 2.0 * Math.cos(Math.toRadians(45.0)) + 2.0);
        initialCloudPoints.add(new CloudPoint(initialX1, initialY1));
        initialCloudPoints.add(new CloudPoint(initialX2, initialY2));
        LandMark existingLandmark = new LandMark("1", "Test Object", initialCloudPoints);
        fusionSlam.getLandmarks().add(existingLandmark);

        List<CloudPoint> newCloudPoints = new LinkedList<>();
        newCloudPoints.add(new CloudPoint(3.0, 3.0));
        newCloudPoints.add(new CloudPoint(4.0, 4.0));
        TrackedObject trackedObject = new TrackedObject("1", 1, "Test Object", newCloudPoints);
        List<TrackedObject> trackedObjects = new LinkedList<>();
        trackedObjects.add(trackedObject);
        TrackedObjectEvent trackedObjectEvent = new TrackedObjectEvent("1", 1);
        trackedObjectEvent.addTrackedObject(trackedObject);
        ReadyToProcessPair<Pose, TrackedObjectEvent> pair = new ReadyToProcessPair<>(pose, trackedObjectEvent);

        // Act
        fusionSlam.ProcessReadyToProcessPair(pair);
        
        // Assert
        LandMark updatedLandmark = fusionSlam.getLandmarks().get(0);
        assertEquals("1", updatedLandmark.getId());
        assertEquals("Test Object", updatedLandmark.getDescription());
        assertEquals(2, updatedLandmark.getCoordinates().size());
        // Check if the coordinates are the average of the old and new ones
        double expectedX1 = ((1.0 * Math.cos(Math.toRadians(45.0)) - 1.0 * Math.sin(Math.toRadians(45.0)) + 1.0)
                + (3.0 * Math.cos(Math.toRadians(45.0)) - 3.0 * Math.sin(Math.toRadians(45.0)) + 1.0)) / 2;
        double expectedY1 = ((1.0 * Math.sin(Math.toRadians(45.0)) + 1.0 * Math.cos(Math.toRadians(45.0)) + 2.0)
                + (3.0 * Math.sin(Math.toRadians(45.0)) + 3.0 * Math.cos(Math.toRadians(45.0)) + 2.0)) / 2;
        double expectedX2 = ((2.0 * Math.cos(Math.toRadians(45.0)) - 2.0 * Math.sin(Math.toRadians(45.0)) + 1.0)
                + (4.0 * Math.cos(Math.toRadians(45.0)) - 4.0 * Math.sin(Math.toRadians(45.0)) + 1.0)) / 2;
        double expectedY2 = ((2.0 * Math.sin(Math.toRadians(45.0)) + 2.0 * Math.cos(Math.toRadians(45.0)) + 2.0)
                + (4.0 * Math.sin(Math.toRadians(45.0)) + 4.0 * Math.cos(Math.toRadians(45.0)) + 2.0)) / 2;
        assertEquals(expectedX1, updatedLandmark.getCoordinates().get(0).getX(), 0.01);
        assertEquals(expectedY1, updatedLandmark.getCoordinates().get(0).getY(), 0.01);
        assertEquals(expectedX2, updatedLandmark.getCoordinates().get(1).getX(), 0.01);
        assertEquals(expectedY2, updatedLandmark.getCoordinates().get(1).getY(), 0.01);
    }

}
