import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WriteTest {

    int xMax = 10, yMax = 10, zMax = 10;

    HashMap<String, Integer> blockPalette = new HashMap<String, Integer>();
    int[] area = new int[xMax * yMax * zMax];
    int index = 0;

    private int flatten(int x, int y, int z)
    {
        return x + (y * xMax) + (z * xMax * yMax);
    }

    private int[] unflatten(int index)
    {
        int x = index % xMax;
        int y = (index / xMax) % xMax;
        int z = index / (xMax * xMax);

        return new int[]{x, y, z};
    }

    public void addBlock(String s, int x, int y, int z)
    {
        if (!blockPalette.containsKey(s))
        {
            blockPalette.put(s, index++);
        }
        int index = flatten(x, y, z);
        area[index] = blockPalette.get(s);
//        System.out.println(index);
    }

    public Long[] createLongArray()
    {
        int packing = (int) Math.ceil(Math.log(blockPalette.size()) / Math.log(2));
        ArrayList<Long> dataList = new ArrayList<Long>();

        int offset = 0;
        long data = 0;

        for (int i=0;i<area.length;i++)
        {
            if (64 - packing >= offset)
            {
                data |= ((long)area[i]) << offset;
                offset += packing;

                if (offset >= 64)
                {
                    offset %= 64;
                    dataList.add(data);
                    data = 0;
                }
            }
            else
            {
                int first = 64 - offset;

                data |= (((long)area[i]) & (1 << first) - 1) << offset;

                offset = packing - first;
                dataList.add(data);
                data = (((long)area[i]) >> first) & (1 << offset) - 1;
            }
        }

        if (offset > 0)
        {
            dataList.add(data);
        }

        Long[] dataArray = new Long[dataList.size()];
        dataList.toArray(dataArray);
        return dataArray;
    }

    public void read(Long[] dataArray)
    {
        int _index = 0;

        int packing = (int) Math.ceil(Math.log(blockPalette.size()) / Math.log(2));
        int mask = (1 << packing) - 1;

        int offset = 0;
        for (int j=0;j<dataArray.length;j++)
        {
            long value = dataArray[j];
            int i;
            for (i=offset;i <= 64-packing;i+=packing)
            {
                long v1 = (value >> i) & mask;

                if (v1 > 0) System.out.println(v1 + " " + Arrays.toString(unflatten(_index)));
                _index++;
            }

            if ((64 - offset) % packing > 0 && j < dataArray.length-1) {
                int first = (64-offset) % packing;
                offset = packing - first;

                long v2 = ((value >> i) & (1 << first) - 1) | ((dataArray[j + 1] & (1 << offset) - 1) << first);

                if (v2 > 0) System.out.println(v2 + " " + Arrays.toString(unflatten(_index)));
                _index++;

            }
            else
            {
                offset = 0;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        WriteTest test = new WriteTest();

        test.addBlock("Air", 0, 0, 0);
        test.addBlock("Stone", 1, 0, 0);
        test.addBlock("Dirt", 2, 0, 0);
        test.addBlock("Cobble", 5, 5, 5);
        test.addBlock("Glass", 9, 9, 9);

        Long[] data = test.createLongArray();

        test.read(data);
    }
}
