package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the systemRuntime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    // TODO: Define fields and methods for statistics tracking.

    // need to be Thread safe
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;

    public StatisticalFolder(){
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numLandmarks = 0;
        this.numTrackedObjects = 0;
    }

    public int getRuntime(){
        return systemRuntime;
    }

    public int getNumLandmarks(){
        return numLandmarks;
    }

    public int getNumDetectedObjects(){
        return numDetectedObjects;
    }

    public int getNumTrackedObjects(){
        return numTrackedObjects;
    }
}
