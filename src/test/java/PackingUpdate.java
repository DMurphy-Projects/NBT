import Helper.BlockCreator;
import Model.SchematicArea;
import dev.dewy.nbt.tags.collection.CompoundTag;

public class PackingUpdate {
    public static void main(String[] args)
    {
        SchematicArea area = new SchematicArea(10, 10, 10);

        CompoundTag[] blocks = {
                BlockCreator.createBasicBlock("minecraft:air"),
                BlockCreator.createBasicBlock("minecraft:stone"),
                BlockCreator.createBasicBlock("minecraft:gold_block"),
                BlockCreator.createBasicBlock("minecraft:iron_block"),
                BlockCreator.createBasicBlock("minecraft:diamond_block"),
        };

        for (int i=0;i<blocks.length;i++)
        {
            area.addPalette(blocks[i]);
            area.addBlock(blocks[i], i, i, i);
        }
    }
}
