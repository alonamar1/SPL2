import bgu.spl.mics.application.objects.CloudPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloudPointsArrayTest {

    @Test
    public void testGenerateCloudPointsArray() {
        List<CloudPoint>[] cloudPointsArray = generateCloudPointsArray(10, 1, 5);

        // Verify the array size
        assertTrue(cloudPointsArray.length == 10);

        // Verify each list's size is within the specified range
        for (List<CloudPoint> cloudPoints : cloudPointsArray) {
            assertTrue(cloudPoints.size() >= 1 && cloudPoints.size() <= 5);
        }
    }

    private List<CloudPoint>[] generateCloudPointsArray(int arraySize, int minListSize, int maxListSize) {
        List<CloudPoint>[] cloudPointsArray = new List[arraySize];
        Random random = new Random();

        for (int i = 0; i < arraySize; i++) {
            int listSize = random.nextInt(maxListSize - minListSize + 1) + minListSize;
            List<CloudPoint> cloudPointsList = new ArrayList<>(listSize);

            for (int j = 0; j < listSize; j++) {
                double x = random.nextDouble() * 100; // Random x coordinate
                double y = random.nextDouble() * 100; // Random y coordinate
                cloudPointsList.add(new CloudPoint(x, y));
            }

            cloudPointsArray[i] = cloudPointsList;
        }

        return cloudPointsArray;
    }
}
