package IO;

import Model.SchematicArea;

public class SchematicReader {

    public static SchematicArea read(SchematicFileHandler fileHelper)
    {
        SchematicArea area = new SchematicArea(
                fileHelper.enclosingSize.getInt("x").getValue(),
                fileHelper.enclosingSize.getInt("y").getValue(),
                fileHelper.enclosingSize.getInt("z").getValue()
        );
        int areaIndex = 0;

        int packing = (int) Math.ceil(Math.log(fileHelper.blockStatePalette.size()) / Math.log(2));
        int mask = (1 << packing) - 1;

        int offset = 0;
        for (int j=0;j<fileHelper.blockStates.size();j++)
        {
            long value = fileHelper.blockStates.get(j);
            int i;
            for (i=offset;i <= 64-packing;i+=packing)
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
