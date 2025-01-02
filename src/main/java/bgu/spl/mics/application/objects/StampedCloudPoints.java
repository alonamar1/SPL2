package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    private List<List<Double>> cloudPoints; // List of cloud points, each represented by a list of 3 doubles (x, y, z)

    public StampedCloudPoints(String ID, int time, List<List<Double>> cloudPoints){
        this.id = ID;
        this.time = time;
        this.cloudPoints = cloudPoints;
    }

    public String getID(){
        return id;
    }

    public int getTime(){
        return time;
    }

    public List<List<Double>> getCloudPoints(){
        return cloudPoints;
    }
}
