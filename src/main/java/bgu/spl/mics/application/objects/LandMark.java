package bgu.spl.mics.application.objects;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {

    private String id;
    private String description;
    private List<CloudPoint> coordinates;

    public LandMark(String id, String description, List<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }   

    public String getId() {
        return id;
    }   

    public String getDescription() {
        return description;
    }   

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    /**
     * Updates the coordinates of a landmark. from a given list of cloud points.
     * @param prevlandmark
     * @param newLandmarkCloudPoints
     */
    public static void updateCoordiLandmark(LandMark prevlandmark, List<CloudPoint> newLandmarkCloudPoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCoordiLandmark'");
    }   
    
}
