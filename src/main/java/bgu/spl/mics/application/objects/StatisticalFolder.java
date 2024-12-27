package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the systemRuntime of the system,
 * the number of objects detected and tracked, and the number of landmarks
 * identified.
 */
public class StatisticalFolder {

    /**
     * Singleton holder class.
     */
    private static class StatisticalFolderHolder {
        private static StatisticalFolder instance = new StatisticalFolder();
    }

    /**
     * Returns the singleton instance of StatisticalFolder.
     * 
     * @return
     */
    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.instance;
    }

    // Need to be Thread safe
    private AtomicInteger systemRuntime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandmarks;

    /**
     * Constructs a new StatisticalFolder object.
     */
    public StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
    }

    /**
     * Returns the runtime of the system.
     * @return
     */
    public int getRuntime() {
        return systemRuntime.get();
    }

    /**
     * Returns the number of landmarks identified.
     * @return
     */
    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    /**
     * Returns the number of detected objects.
     * @return
     */
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    /**
     * Returns the number of tracked objects.
     * @return
     */
    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    /**
     * Increments the runtime by 1.
     */
    public void incrementRuntime() {
        systemRuntime.incrementAndGet();
    }

    /**
     * Increments the number of landmarks by 1.
     */
    public void incrementNumLandmarks() {
        numLandmarks.incrementAndGet();
    }

    /**
     * Increments the number of detected objects by 1.
     */
    public void incrementNumDetectedObjects() {
        numDetectedObjects.incrementAndGet();
    }

    /**
     * Increments the number of tracked objects by 1.
     */
    public void incrementNumTrackedObjects() {
        numTrackedObjects.incrementAndGet();
    }
}
