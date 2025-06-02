package Model;

import Helper.ArrayIndexHelper;
import Helper.BinaryPacker;
import Helper.Conversion;
import dev.dewy.nbt.tags.array.LongArrayTag;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;

import java.util.*;

public class SchematicArea {
    int xMax, yMax, zMax, totalMax;

    CompoundTag[][][] area;
    Set<CompoundTag> blockPalette = new LinkedHashSet<CompoundTag>();

    public SchematicArea(int x, int y, int z)
    {
        area = new CompoundTag[x][y][z];

        xMax = x;
        yMax = y;
        zMax = z;
        totalMax = xMax * yMax * zMax;
    }

    public SchematicArea(int x, int y, int z, LongArrayTag blockStates, ListTag<CompoundTag> blockPalette)
    {
        this(x, y, z);

        for (CompoundTag tag: blockPalette)
        {
            addPalette(tag);
        }

        int location = 0;
        BinaryPacker packer = new BinaryPacker(Conversion.BlockStatesToLongArray(blockStates), calculateDigits());
        while (packer.canRead() && location < totalMax)
        {
            addBlock(blockPalette.get((int) packer.read()), location++);
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

    private int calculateDigits()
    {
        return (int) (Math.max(2, (Math.log(blockPalette.size()-1) / Math.log(2)) + 1));
    }

    public void addBlock(CompoundTag tag, int x, int y, int z)
    {
        area[x][y][z] = tag;
    }

    public void addBlock(CompoundTag tag, int pos)
    {
        int[] i = ArrayIndexHelper.unflatten(pos, xMax, zMax);
        area[i[0]][i[1]][i[2]] = tag;
    }

    public void addPalette(CompoundTag tag) {
        blockPalette.add(tag);
    }

    public void addArea(SchematicArea section, int x, int y ,int z, int w, int h, int l, int tx, int ty, int tz)
    {
        for (int a=0;a<w;a++)
        for (int b=0;b<h;b++)
        for (int c=0; c<l; c++)
        {
            CompoundTag tag = section.area[x+a][y+b][z+c];

            addPalette(tag);
            addBlock(tag, tx+a, ty+b, tz+c);
        }
    }

    private Map<CompoundTag, Integer> createBlockPaletteLookup()
    {
        Map<CompoundTag, Integer> lookup = new HashMap<CompoundTag, Integer>();

        int index = 0;
        for (CompoundTag tag : blockPalette) {
            lookup.put(tag, index++);
        }

        return lookup;
    }

    public ListTag<CompoundTag> createBlockStatePalette()
    {
        ListTag<CompoundTag> blockStatePalette = new ListTag<CompoundTag>();
        for (CompoundTag s: blockPalette)
        {
            blockStatePalette.add(s);
        }
        return blockStatePalette;
    }

    public long[] createLongArray()
    {
        int digits = calculateDigits();
        BinaryPacker packer = new BinaryPacker((int) Math.ceil((double)(totalMax * digits) / 64d), digits);

        Map<CompoundTag, Integer> lookup = createBlockPaletteLookup();

        //should iterate x->z->y
        for (int i=0;i<yMax;i++)
        {
            for (int j=0;j<zMax;j++)
            {
                for (int k=0;k<xMax;k++)
                {
                    packer.write(lookup.get(area[k][i][j]));
                }
            }
        }

        return packer.getRawData();
    }

    public void printArea(int height)
    {
        for (int i=0;i<xMax;i++)
        {
            for (int j=0;j<zMax;j++)
            {
                System.out.println(String.format("%s %s %s", area[i][height][j], i, j));
            }
        }
    }
}
