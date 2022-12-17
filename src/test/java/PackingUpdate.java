import Model.SchematicArea;

public class PackingUpdate {
    public static void main(String[] args)
    {
        SchematicArea area = new SchematicArea(10, 10, 10);

        String[] blocks = {
                "minecraft:air",
                "minecraft:stone",
                "minecraft:gold_block",
                "minecraft:iron_block",
                "minecraft:diamond_block",
        };

        for (int i=0;i<blocks.length;i++)
        {
            area.addPalette(blocks[i]);
            area.addBlock(blocks[i], i * 64);
        }

        area.print();

    }
}
