package IO;

import Model.SchematicArea;
import dev.dewy.nbt.tags.collection.CompoundTag;

public class SchematicReader {

    public static SchematicArea read(SchematicFileHandler fileHelper)
    {
        SchematicArea area = new SchematicArea(
                fileHelper.enclosingSize.getInt("x").getValue(),
                fileHelper.enclosingSize.getInt("y").getValue(),
                fileHelper.enclosingSize.getInt("z").getValue()
        );

        for (CompoundTag tag: fileHelper.blockStatePalette)
        {
            area.addPalette((String) tag.getValue().get("Name").getValue());
        }

        int areaIndex = 0;
        int packing = (int) Math.max(2, Math.ceil(Math.log(fileHelper.blockStatePalette.size()) / Math.log(2)));
        int mask = (1 << packing) - 1;

        int offset = 0;
        for (int j=0;j<fileHelper.blockStates.size();j++)
        {
            long value = fileHelper.blockStates.get(j);
            int i;
            for (i=offset;i <= Math.min(64, fileHelper.totalVolume.getValue() * packing)-packing;i+=packing)
            {
                long v1 = (value >> i) & mask;
                area.addBlock(fileHelper.blockStatePalette.get((int) v1).getString("Name").getValue(), areaIndex++);
            }

            if ((64 - offset) % packing > 0 && j < fileHelper.blockStates.size()-1) {
                int first = (64-offset) % packing;
                offset = packing - first;

                long v2 = ((value >> i) & (1 << first) - 1) | ((fileHelper.blockStates.get(j + 1) & (1 << offset) - 1) << first);

                area.addBlock(fileHelper.blockStatePalette.get((int) v2).getString("Name").getValue(), areaIndex++);
            }
            else
            {
                offset = 0;
            }
        }

        return area;
    }
}
