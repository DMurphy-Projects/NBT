import Helper.BinaryPacker;
import Helper.BlockCreator;
import IO.SchematicFileHandler;
import Model.SchematicArea;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;

import java.io.File;
import java.io.IOException;

public class LargeAreaTest {

    public static SchematicArea createFromFile(String file) throws IOException {
        Nbt nbt = new Nbt();

        CompoundTag root = nbt.fromFile(new File(file));

        SchematicFileHandler fileHelper = new SchematicFileHandler(root);
        SchematicArea area = fileHelper.createArea();

        return area;
    }

    public static void main(String[] args) throws IOException {

        String inFolder = "C:\\Users\\Dean\\AppData\\Roaming\\.minecraft\\installations\\1.18.1\\schematics\\City Generator\\City Tiles\\";
        String outFolder = "C:\\Users\\Dean\\AppData\\Roaming\\.minecraft\\installations\\1.18.1\\schematics\\City Generator\\City Examples\\";

        SchematicArea road = createFromFile(String.format("%s%s%s.litematic", inFolder, "ROAD", 0));

        int size = road.getWidth(), height = road.getHeight();

        SchematicArea area = new SchematicArea(size * 3, height, size * 3);
        area.addPalette(BlockCreator.createBasicBlock("minecraft:air"));

        area.addArea(road, 0, 0, 0, size, height, size, 0, 0, 0);
        area.addArea(road, 0, 0, 0, size, height, size, 9, 0, 0);
        area.addArea(road, 0, 0, 0, size, height, size, 18, 0, 0);

        area.addArea(road, 0, 0, 0, size, height, size, 0, 0, 9);
        area.addArea(road, 0, 0, 0, size, height, size, 9, 0, 9);
        area.addArea(road, 0, 0, 0, size, height, size, 18, 0, 9);

        area.addArea(road, 0, 0, 0, size, height, size, 0, 0, 18);
        area.addArea(road, 0, 0, 0, size, height, size, 9, 0, 18);
        area.addArea(road, 0, 0, 0, size, height, size, 18, 0, 18);

        //origin marker
        area.addPalette(BlockCreator.createBasicBlock("minecraft:white_concrete"));
        area.addBlock(BlockCreator.createBasicBlock("minecraft:white_concrete"), 0, 0, 0);

        CloneAreaTest.write(String.format("%s%s", outFolder, "cloneTest.litematic"), new Nbt(), area);
    }
}
