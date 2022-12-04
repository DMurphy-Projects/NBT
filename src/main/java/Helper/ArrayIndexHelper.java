package Helper;

public class ArrayIndexHelper {

    public static int flatten(int x, int y, int z, int xMax, int zMax)
    {
//        return x + (y * xMax) + (z * xMax * yMax);
        return x + (z * xMax) + (y * xMax * zMax);
    }

    public static int[] unflatten(int index, int xMax, int zMax)
    {
        int x = index % xMax;
        int y = index / (xMax * zMax);
        int z = (index / xMax) % zMax;

        return new int[]{x, y, z};
    }
}
