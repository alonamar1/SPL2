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
     * Updates the coordinates of a landmark from a given list of cloud points.
     * 
     * @param prevlandmark
     * @param newLandmarkCloudPoints
     */
    public static void updateCoordiLandmark(LandMark prevlandmark, List<CloudPoint> newLandmarkCloudPoints) {
        int minSize = Math.min(prevlandmark.getCoordinates().size(), newLandmarkCloudPoints.size());
        // Update the coordinates of the landmark
        for (int i = 0; i < minSize; i++) {
            CloudPoint prevPoint = prevlandmark.getCoordinates().get(i);
            CloudPoint newPoint = newLandmarkCloudPoints.get(i);
            prevPoint.setX((prevPoint.getX() + newPoint.getX()) / 2);
            prevPoint.setY((prevPoint.getY() + newPoint.getY()) / 2);
        }
        // Add the remaining points from the longer list to the landmark
        if (newLandmarkCloudPoints.size() > minSize) {
            for (int i = minSize; i < newLandmarkCloudPoints.size(); i++) {
                prevlandmark.getCoordinates().add(newLandmarkCloudPoints.get(i));
            }
        } else if (prevlandmark.getCoordinates().size() > minSize) {
            for (int i = minSize; i < prevlandmark.getCoordinates().size(); i++) {
                newLandmarkCloudPoints.add(prevlandmark.getCoordinates().get(i));
            }
        }
    }
}
