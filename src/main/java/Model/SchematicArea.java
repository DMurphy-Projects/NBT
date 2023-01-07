package Model;

import Helper.ArrayIndexHelper;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;
import dev.dewy.nbt.tags.primitive.StringTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicArea {
    int xMax, yMax, zMax, totalMax;

    HashMap<String, Integer> blockPalette = new HashMap<String, Integer>();
    HashMap<Integer, String> blockPaletteInverse = new HashMap<Integer, String>();
    int globalIndex = 0, digits = 2;

    List<Long> area;

    public SchematicArea(int x, int y, int z)
    {
        this(x, y, z, new ArrayList<Long>());
    }

    public SchematicArea(int x, int y, int z, List<Long> a)
    {
        xMax = x;
        yMax = y;
        zMax = z;

        totalMax = x * y * z;

        area = a;

        initList(area, totalMax, digits, a.size());

        updatePacking();
    }

    private void initList(List<Long> list, int max, int digits, int existing)
    {
        int needed = (int) (Math.ceil((max * digits) / 64d) - existing);
        for (int i=0;i<needed;i++)
        {
            list.add(0L);
        }
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
        return ArrayIndexHelper.flatten(x, y, z, xMax, zMax);
    }

    public void updatePacking()
    {
        int newDigits = (int) (Math.max(2, (Math.log(blockPalette.size()-1) / Math.log(2)) + 1));
        if (newDigits > digits)
        {
            System.out.println(String.format("Update Packing - %s -> %s, Size: %s", digits, newDigits, blockPalette.size()));

            List<Long> newArea = new ArrayList<Long>();
            initList(newArea, totalMax, newDigits, 0);

            for (int i=0;i<totalMax;i++)
            {
                int readBits = i * digits;
                long data = readWithPosition(area, readBits / 64, readBits % 64, digits);

                int writeBits = i * newDigits;
                writeWithPosition(newArea, writeBits / 64, data, writeBits % 64, newDigits);
            }

            digits = newDigits;
            area = newArea;
        }
    }

    public void addPalette(String s)
    {
        if (!blockPalette.containsKey(s))
        {
            System.out.println("Added: " + s);

            int i = globalIndex++;
            blockPalette.put(s, i);
            blockPaletteInverse.put(i, s);

            updatePacking();
        }
    }

    public void addArea(SchematicArea section, int x, int y ,int z, int w, int h, int l, int tx, int ty, int tz)
    {
        for (int a=0;a<w;a++)
        for (int b=0;b<h;b++)
        for (int c=0; c<l; c++)
        {
            int pos = section.flatten(x + a, y + b, z + c);
            int bits = pos * section.digits;

            long data = readWithPosition(section.area, bits / 64, bits % 64, section.digits);
            String block = section.blockPaletteInverse.get((int)data);

            addPalette(block);
            addBlock(block, a + tx, b + ty, c + tz);
        }
    }

    public void addBlock(String s, int x, int y, int z)
    {
        addBlock(s, flatten(x, y, z));
    }

    public void addBlock(String s, int pos)
    {
        addBlock(blockPalette.get(s), pos);
    }

    public void addBlock(long data, int pos)
    {
        int bits = pos * digits;
        writeWithPosition(area, bits / 64, data, bits % 64, digits);
    }

    public void writeWithPosition(List<Long> list, int baseIndex, long x, int pos, int digits)
    {
        //writes x starting at pos to the base
        Long base = list.get(baseIndex);
        long mask = (1 << digits) - 1;

        boolean split = (pos + digits) > 64;
        if (split)
        {
            long firstHalfMask = (1 << (64 - pos)) - 1;
            long secondHalfMask = mask - firstHalfMask;

            writeWithPosition(list, baseIndex, x & firstHalfMask, pos, 64 - pos);
            writeWithPosition(list, baseIndex+1, (x & secondHalfMask) >> (64 - pos), 0, digits - (64 - pos));
        }
        else {

            long maskValue = (base >> pos) & mask;
//            System.out.println(base + ((x - maskValue) << pos));
            list.set(baseIndex, base + ((x - maskValue) << pos));
        }
    }

    public long readWithPosition(List<Long> list, int baseIndex, int pos, int digits)
    {
        long base = list.get(baseIndex);
        boolean split = (pos + digits) > 64;
        if (split)
        {
            long l1 = readWithPosition(list, baseIndex, pos, (64 - pos));
            long l2 = readWithPosition(list, baseIndex+1, 0, (pos + digits) - 64);

            return (l2 << (64 - pos)) | l1;
        }
        else
        {
            long mask = (1 << digits) - 1;
            return (base >> pos) & mask;
        }
    }

    public int getDataSize()
    {
        return area.size();
    }

    public int getPaletteSize()
    {
        return blockPalette.size();
    }

    public void print() {
        System.out.println(String.format("Packing: %s", digits));

        for (int i=0;i<totalMax;i++)
        {
            int bits = (i * digits);
            long data = readWithPosition(area, bits / 64, bits % 64, digits);
            System.out.println(String.format("%s : %s -> %s", i, data, blockPaletteInverse.get((int)data)));
        }
    }

    public long[] createLongArray()
    {
        long[] arr = new long[area.size()];
        for (int i=0;i<area.size();i++)
        {
            arr[i] = area.get(i);
        }
        return arr;
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
