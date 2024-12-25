package bgu.spl.mics.application.objects;
import java.util.List;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {


    private List<StampedCloudPoints> cloudPoints;
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */


    public static LiDarDataBase getInstance(String filePath) {
        // TODO: Implement this
        return null;
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }
}
