package Helper;

public class ArrayIndexHelper {

    public static int flatten(int x, int y, int z, int xMax, int yMax, int zMax)
    {
//        return x + (y * xMax) + (z * xMax * yMax);
        return x + (z * xMax) + (y * xMax * zMax);
    }

    public static int[] unflatten(int index, int xMax, int yMax)
    {
        int x = index % xMax;
        int y = (index / xMax) % yMax;
        int z = index / (xMax * yMax);

        return new int[]{x, y, z};
    }
}
