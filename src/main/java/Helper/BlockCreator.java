package Helper;

import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.primitive.StringTag;

public class BlockCreator {

    public static CompoundTag createBasicBlock(String name)
    {
        CompoundTag tag = new CompoundTag();

        tag.put(new StringTag("Name", name));

        return tag;
    }
}
