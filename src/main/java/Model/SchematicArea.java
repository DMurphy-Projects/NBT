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
    int globalIndex = 0, packing = 1;

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
        if (area.size() == 0)
        {
            area.add(0l);
        }

        updatePacking();
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
        int newPacking = (int) (Math.max(2, Math.log(blockPalette.size()) / Math.log(2) + 1));
        if (packing != newPacking)
        {
            //todo update packing
            packing = newPacking;
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

    public void addArea(SchematicArea section, int x, int y ,int z, int w, int h, int l)
    {
        for (int a=x;a<x+w;a++)
        for (int b=y;b<y+h;b++)
        for (int c=z; c<z+l; c++)
        {
            int pos = section.flatten(a, b, c);
            int bits = pos * section.packing;

            long data = section.readWithPosition(bits / 64, section.packing, bits % 64);
            String block = section.blockPaletteInverse.get((int)data);

            addPalette(block);
            addBlock(block, a, b, c);
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
        int bits = pos * packing;
        writeWithPosition(bits / 64, data, bits % 64);
    }

    public void writeWithPosition(int baseIndex, long x, int pos)
    {
        //writes x starting at pos to the base
        Long base = area.get(baseIndex);
        long digits = (long) (Math.log(x) / Math.log(2)) + 1;
        long mask = (1 << digits) - 1;

        boolean split = (pos + digits) > 64;
        if (split)
        {
            long firstHalfMask = (1 << (64 - pos)) - 1;
            long secondHalfMask = mask - firstHalfMask;

            if (baseIndex+1 > area.size())
            {
                area.add(0l);
            }

            writeWithPosition(baseIndex, x & firstHalfMask, pos);
            writeWithPosition(baseIndex+1, (x & secondHalfMask) >> (64 - pos), 0);
        }
        else {

            long maskValue = (base >> pos) & mask;
//            System.out.println(base + ((x - maskValue) << pos));
            area.set(baseIndex, base + ((x - maskValue) << pos));
        }
    }

    public long readWithPosition(int baseIndex, int digits, int pos)
    {
        long base = area.get(baseIndex);
        boolean split = (pos + digits) > 64;
        if (split)
        {
            long l1 = readWithPosition(baseIndex, (64 - pos), pos);
            int l2Size = (pos + digits) - 64;
            long l2 = readWithPosition(baseIndex+1, l2Size, 0);

            return (l1 << l2Size) | l2;
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
        System.out.println(String.format("Packing: %s", packing));

        int offset = 0, block = 0;
        for (int i=0;i<area.size();i++)
        {
            while(offset < 64 && block < totalMax) {
                long data = readWithPosition(i, packing, offset);
                System.out.println(String.format("%s : %s -> %s", block, data, blockPaletteInverse.get((int)data)));

                offset += packing;
                block++;
            }

            offset %= 64;
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
