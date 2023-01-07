import Helper.BlockCreator;
import IO.SchematicFileHandler;
import Model.SchematicArea;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.io.CompressionType;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;

import java.io.File;
import java.io.IOException;

public class SchematicTest {

    public static void readTest(Nbt nbt) throws IOException {
        CompoundTag root = nbt.fromFile(new File("src/main/resources/Large Area Test.litematic"));

        SchematicFileHandler fileHelper = new SchematicFileHandler(root);

        SchematicArea area = fileHelper.createArea();
//        area.print();
    }

    static CompoundTag air = BlockCreator.createBasicBlock("minecraft:air"),
            stone = BlockCreator.createBasicBlock("minecraft:stone");
    static int menger_order = 5;
    public static SchematicArea createArea()
    {
        int order = menger_order;
        int size = (int) Math.pow(3, order-1);

        SchematicArea area = new SchematicArea(size, size, size);
        area.addBlock(BlockCreator.createBasicBlock("minecraft:air"), 0);
        draw(order, size/2, size/2, size/2, size, area);

        return area;
    }

    public static void writeTest(Nbt nbt) throws IOException {
        SchematicFileHandler fileHelper = new SchematicFileHandler();
        fileHelper.createRoot();

        SchematicArea area = createArea();

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
        fileHelper.modifyTotalBlocks(2);
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

        nbt.toFile(fileHelper.root, new File(String.format("src/main/resources/menger%s.litematic", menger_order)), CompressionType.GZIP);
    }

    public static void drawCube(int x, int y, int z, int size, SchematicArea area)
    {
        if (size > 0) return;
//        System.out.println(String.format("(%s, %s, %s) : %s", x, y, z, size));
        area.addBlock(stone, x, y, z);
    }

    public static void fillCube(int x, int y, int z, int size, SchematicArea area, CompoundTag block)
    {
        int size_3 = size / 3;
        for (int i=x-size_3;i<size;i++)
        {
            for (int ii=y-size_3;ii<size;ii++)
            {
                for (int iii=z-size_3;iii<size;iii++)
                {
                    area.addBlock(block, i, ii, iii);
                }
            }
        }
    }

    public static void draw(int n, int x, int y, int z, int size, SchematicArea area) {
        if (n == 0) return;
        int size_3 = size / 3;

        drawCube(x, y, z, size_3, area);

        int x0 = x - size_3;
        int x1 = x + size_3;

        int y0 = y - size_3;
        int y1 = y + size_3;

        int z0 = z - size_3;
        int z1 = z + size_3;

        //bottom
        draw(n-1, x0, y0, z0, size_3, area);
        draw(n-1, x0, y0, z, size_3, area);
        draw(n-1, x0, y0, z1, size_3, area);

        draw(n-1, x, y0, z0, size_3, area);
//        draw(n-1, x, y0, z, size_3, area);
        draw(n-1, x, y0, z1, size_3, area);

        draw(n-1, x1, y0, z0, size_3, area);
        draw(n-1, x1, y0, z, size_3, area);
        draw(n-1, x1, y0, z1, size_3, area);

        //middle
        draw(n-1, x0, y, z0, size_3, area);
//        draw(n-1, x0, y, z, size_3, area);
        draw(n-1, x0, y, z1, size_3, area);

//        draw(n-1, x, y, z0, size_3, area);
//        draw(n-1, x, y, z, size_3, area);
//        draw(n-1, x, y, z1, size_3, area);

        draw(n-1, x1, y, z0, size_3, area);
//        draw(n-1, x1, y, z, size_3, area);
        draw(n-1, x1, y, z1, size_3, area);

        //top
        draw(n-1, x0, y1, z0, size_3, area);
        draw(n-1, x0, y1, z, size_3, area);
        draw(n-1, x0, y1, z1, size_3, area);

        draw(n-1, x, y1, z0, size_3, area);
//        draw(n-1, x, y1, z, size_3, area);
        draw(n-1, x, y1, z1, size_3, area);

        draw(n-1, x1, y1, z0, size_3, area);
        draw(n-1, x1, y1, z, size_3, area);
        draw(n-1, x1, y1, z1, size_3, area);
    }

    public static void main(String[] args) throws IOException {
        Nbt nbt = new Nbt();

//        readTest(nbt);

        writeTest(nbt);
    }
}
