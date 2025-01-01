package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class LiDarDataBaseHolder {
        private static LiDarDataBase instance = new LiDarDataBase();
    }

    private List<StampedCloudPoints> cloudPoints; // List of cloud points, each represented by a list of 3 doubles (x, y, z)

    /**
     * Empty Constructor to enforce Singleton pattern
     */
    private LiDarDataBase() {
        this.cloudPoints = new LinkedList<StampedCloudPoints>();
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase instance = LiDarDataBaseHolder.instance;
        synchronized (instance) {
            // If the cloudPoints list is empty, load Data from file
            if (instance.cloudPoints.isEmpty()) {
                //instance.loadData(filePath);
                instance.loadData("C:\\Users\\meire\\Documents\\Third Semster\\SPL2\\Skeleton\\example_input_2\\lidar_data.json");
            }
        }
        return instance;
    }
    
        /**
     * Load data from a JSON file.
     * @param filepath
     */
    private void loadData(String filepath) {
        Gson gson = new Gson(); 
        try (FileReader reader = new FileReader(filepath)) {
            // Convert JSON File to Java Object
            this.cloudPoints = gson.fromJson(reader, new TypeToken<List<StampedCloudPoints>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //".\\example_input_2\\lidar_data.json"

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }
}


