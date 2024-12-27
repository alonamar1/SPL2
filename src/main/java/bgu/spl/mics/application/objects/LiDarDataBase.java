package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class LiDarDataBaseHolder {
        private static LiDarDataBase instance = new LiDarDataBase(".\\example_input_2\\lidar_data.json");
    }

    private List<StampedCloudPoints> cloudPoints; // List of cloud points, each represented by a list of 3 doubles (x, y, z)

    private LiDarDataBase(String filepath) {
        Gson gson = new Gson(); 
        try (FileReader reader = new FileReader(filepath)) {
            // Convert JSON File to Java Object
            this.cloudPoints = gson.fromJson(reader, new TypeToken<List<StampedCloudPoints>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //".\\example_input_2\\lidar_data.json"

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    public static LiDarDataBase getInstance(String filePath) {
        if (LiDarDataBaseHolder.instance == null) {
            LiDarDataBaseHolder.instance = new LiDarDataBase(filePath);
        }
        return LiDarDataBaseHolder.instance;
    }
    
    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

   /*public static void main(String[] args) {
        for (int i = 0; i < 30 ; i++) {
            System.out.print("ID: ");
            System.out.print(getInstance(".\\example_input_2\\lidar_data.json").getCloudPoints().get(i).getID());
            System.out.print("    Time: ");
            System.out.print(getInstance(".\\example_input_2\\lidar_data.json").getCloudPoints().get(i).getTime());
            //System.out.print(getInstance(".\\example_input_2\\lidar_data.json").getCloudPoints().get(i).getCloudPoints().toString());
            System.out.println("     ");
        }
    }*/
}


