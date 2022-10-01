package Model;

import Helper.ArrayIndexHelper;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;
import dev.dewy.nbt.tags.primitive.StringTag;

import java.util.HashMap;
import java.util.Map;

public class SchematicArea {
    int xMax, yMax, zMax;

    HashMap<String, Integer> blockPalette = new HashMap<String, Integer>();
    HashMap<Integer, String> blockPaletteInverse = new HashMap<Integer, String>();
    int[] area;
    int globalIndex = 0;

    public SchematicArea(int x, int y, int z)
    {
        xMax = x;
        yMax = y;
        zMax = z;

        area = new int[xMax * yMax * zMax];
    }

    public int getHeight()
    {
        return yMax;
    }
    public int getWidth()
    {
        return xMax;
    }
    public int getDepth()
    {
        return zMax;
    }

    private int flatten(int x, int y, int z)
    {
        return ArrayIndexHelper.flatten(x, y, z, xMax, yMax, zMax);
    }

    public void addBlock(String s, int index)
    {
        if (!blockPalette.containsKey(s))
        {
            int i = globalIndex++;
            blockPalette.put(s, i);
            blockPaletteInverse.put(i, s);
        }

        area[index] = blockPalette.get(s);
    }

    public void addBlock(String s, int x, int y, int z)
    {
        addBlock(s, flatten(x, y, z));
    }

    public int getDataSize()
    {
        return area.length;
    }

    public int getPaletteSize()
    {
        return blockPalette.size();
    }

    public void print()
    {
        for (int i=0;i<area.length;i++)
        {
            System.out.println(String.format("%s -> %s : %s", i, area[i], blockPaletteInverse.get(area[i])));
        }
    }

    public long[] createLongArray()
    {
        int packing = Math.max((int) Math.ceil(Math.log(blockPalette.size()) / Math.log(2)), 2);

        long[] dataList = new long[(int) Math.ceil((xMax * yMax * zMax * packing) / 64d)];
        int dataListIndex = 0;

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
                    dataList[dataListIndex++] = data;
                    data = 0;
                }
            }
            else
            {
                int first = 64 - offset;

                data |= (((long)area[i]) & (1 << first) - 1) << offset;

                offset = packing - first;
                dataList[dataListIndex++] = data;
                data = (((long)area[i]) >> first) & (1 << offset) - 1;
            }
        }

        if (offset > 0)
        {
            dataList[dataListIndex++] = data;
        }

        return dataList;
    }

    public ListTag<CompoundTag> createBlockStatePalette()
    {
        ListTag<CompoundTag> blockStatePalette = new ListTag<CompoundTag>();
        String[] orderedArray = new String[blockPalette.size()];
        for (Map.Entry<String, Integer> s: blockPalette.entrySet())
        {
            orderedArray[s.getValue()] = s.getKey();
        }

        for (String s: orderedArray)
        {
            CompoundTag t = new CompoundTag();
            t.put(new StringTag("Name", s));
            blockStatePalette.add(t);
        }

        return blockStatePalette;
    }
}
