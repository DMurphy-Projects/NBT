package IO;

import Model.SchematicArea;
import dev.dewy.nbt.tags.collection.CompoundTag;

import java.util.ArrayList;
import java.util.Iterator;

public class SchematicReader {

    public static SchematicArea read(SchematicFileHandler fileHelper)
    {
        ArrayList<Long> data = new ArrayList<Long>();
        Iterator<Long> it = fileHelper.blockStates.iterator();
        while (it.hasNext())
        {
            data.add(it.next());
        }

        SchematicArea area = new SchematicArea(
                fileHelper.enclosingSize.getInt("x").getValue(),
                fileHelper.enclosingSize.getInt("y").getValue(),
                fileHelper.enclosingSize.getInt("z").getValue(),
                data
        );

        for (CompoundTag tag: fileHelper.blockStatePalette)
        {
            area.addPalette((String) tag.getValue().get("Name").getValue());
        }

        return area;
    }
}
