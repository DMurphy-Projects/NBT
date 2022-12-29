import IO.SchematicFileHandler;
import IO.SchematicReader;
import Model.SchematicArea;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.io.CompressionType;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;

import java.io.File;
import java.io.IOException;

public class CloneAreaTest {

    public static void write(String name, Nbt nbt, SchematicArea area) throws IOException {
        SchematicFileHandler fileHelper = new SchematicFileHandler();
        fileHelper.createRoot();

        fileHelper.modifyDataVersion(2865);
        fileHelper.modifyVersion(6);
        //metadata
        fileHelper.modifyEnclosingSize(area.getWidth(), area.getHeight(), area.getDepth());
        fileHelper.modifyAuthor("Dean");
        fileHelper.modifyDescription("Test File");
        fileHelper.modifyName("Test Area");
        fileHelper.modifyRegionCount(1);
        fileHelper.modifyTimeCreated(System.currentTimeMillis());
        fileHelper.modifyTimeModified(System.currentTimeMillis());
        fileHelper.modifyTotalBlocks(area.getWidth() * area.getHeight() * area.getDepth());
        fileHelper.modifyTotalVolume(area.getWidth() * area.getHeight() * area.getDepth());

        //region
        fileHelper.modifyPosition(0, 0, 0);
        fileHelper.modifySize(area.getWidth(), area.getHeight(), area.getDepth());
        fileHelper.modifyBlockStatePalette(area.createBlockStatePalette());
        fileHelper.modifyEntities(new ListTag<CompoundTag>());
        fileHelper.modifyPendingBlockTicks(new ListTag<CompoundTag>());
        fileHelper.modifyPendingFluidTickss(new ListTag<CompoundTag>());
        fileHelper.modifyTileEntities(new ListTag<CompoundTag>());
        fileHelper.modifyBlockStates(area.createLongArray());

        nbt.toFile(fileHelper.root, new File(name), CompressionType.GZIP);
    }

    public static void main(String[] args) throws IOException {

        Nbt nbt = new Nbt();

        String inFolder = "C:\\Users\\Dean\\Documents\\sourcetree\\NBT\\src\\main\\resources\\";
        String outFolder = "C:\\Users\\Dean\\AppData\\Roaming\\.minecraft\\installations\\1.18.1\\schematics\\";
//        CompoundTag root = nbt.fromFile(new File("src/main/resources/test.litematic"));
        CompoundTag root = nbt.fromFile(new File(String.format("%s%s", inFolder, "menger2.litematic")));

        SchematicFileHandler fileHelper = new SchematicFileHandler(root);
        SchematicArea area = SchematicReader.read(fileHelper);

        area.addPalette("minecraft:gold_block");

        SchematicArea copyArea = new SchematicArea(2, 2, 2);
        copyArea.addPalette("minecraft:gold_block");

        area.addArea(copyArea, 0, 0, 0, 2, 2, 2, 0, 0, 0);

//        area.print();
//        for (long l: area.createLongArray())
//        {
//            System.out.println(l);
//        }

        write(String.format("%s%s", outFolder, "cloneTest.litematic"), nbt, area);
    }
}
