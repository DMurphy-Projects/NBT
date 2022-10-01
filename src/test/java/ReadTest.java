import dev.dewy.nbt.*;
import dev.dewy.nbt.tags.array.*;
import dev.dewy.nbt.tags.collection.*;

import java.io.*;

public class ReadTest {

    public static void main(String[] args) throws IOException {
        Nbt nbt = new Nbt();
//        CompoundTag root = nbt.fromFile(new File("src/main/resources/Large Area Test.litematic"));
        CompoundTag root = nbt.fromFile(new File("src/main/resources/amystyst #1.litematic"));

        System.out.println(root+"\n");

        CompoundTag meta = root.getCompound("Metadata");
        CompoundTag region = root.getCompound("Regions");
        CompoundTag namedRegion = region.getCompound(meta.getString("Name").getValue());

        CompoundTag position = namedRegion.getCompound("Position");
        CompoundTag size = namedRegion.getCompound("Size");
        System.out.println(position);
        System.out.println(size);

        LongArrayTag blockStates = namedRegion.getLongArray("BlockStates");
        ListTag<CompoundTag> blockPalette = namedRegion.getList("BlockStatePalette");
        System.out.println(blockPalette);
        System.out.println(blockStates);

        int packing = Math.max((int) Math.ceil(Math.log(blockPalette.size()) / Math.log(2)), 2);
        int mask = (1 << packing) - 1;

        int offset = 0;
        for (int j=0;j<blockStates.size();j++)
        {
            long value = blockStates.get(j);
            int i;
            for (i=offset;i <= 64-packing;i+=packing)
            {
                long v1 = (value >> i) & mask;
                System.out.println(blockPalette.get((int) v1).getString("Name"));
            }

            if ((64 - offset) % packing > 0 && j < blockStates.size()-1) {
                int first = (64-offset) % packing;
                offset = packing - first;

                long v2 = ((value >> i) & (1 << first) - 1) | ((blockStates.get(j + 1) & (1 << offset) - 1) << first);

                System.out.println(blockPalette.get((int) v2).getString("Name"));

            }
            else
            {
                offset = 0;
            }

            System.out.println();
        }
    }
}
