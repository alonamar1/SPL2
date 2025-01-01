package bgu.spl.mics.application.Configurations;

import bgu.spl.mics.application.objects.Pose;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PoseData {

    private List<Pose> poses;

    public PoseData()
    {
        this.poses = new LinkedList<>();
    }

    public void loadData(String filepath) {
        Gson gson = new Gson(); 
        try (FileReader reader = new FileReader(filepath)) {
            // Convert JSON File to Java Object
            this.poses = gson.fromJson(reader, new TypeToken<List<Pose>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Pose> getPoses()
    {
        return poses;
    }

    /* 
    public static void main(String[] args)
    {
        PoseData p = new PoseData();
        p.loadData("C:\\Users\\alona\\SPL2\\example_input_2\\pose_data.json");
        List<Pose> listt = p.getPoses();
        for (Pose pose : listt)
        {
            System.out.println(pose.getX());
            System.out.println(pose.getY());
            System.out.println(pose.getYaw());
            System.out.println(pose.getTime());
            System.out.println();
        }
    }
        */
}
