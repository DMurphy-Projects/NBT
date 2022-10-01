package IO;

import dev.dewy.nbt.tags.array.LongArrayTag;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;
import dev.dewy.nbt.tags.primitive.IntTag;
import dev.dewy.nbt.tags.primitive.LongTag;
import dev.dewy.nbt.tags.primitive.StringTag;

import java.util.LinkedList;
import java.util.List;

public class SchematicFileHandler {

    public CompoundTag root, enclosingSize, position, size, regions;
    public IntTag version, dataVersion, regionCount, totalBlocks, totalVolume;
    public StringTag author, description, name;
    public LongTag timeCreated, timeModified;
    public ListTag<CompoundTag> blockStatePalette, entities, pendingBlockTicks, pendingFluidTicks, tileEntities;
    public LongArrayTag blockStates;

    public SchematicFileHandler(){}
    public SchematicFileHandler(CompoundTag root)
    {
        rdReadRoot(root);
    }

    //modify data
    public void modifyDataVersion(int i)
    {
        dataVersion.setValue(i);
    }

    public void modifyVersion(int i)
    {
        version.setValue(i);
    }

    public void modifyEnclosingSize(int x, int y, int z)
    {
        enclosingSize.getInt("x").setValue(x);
        enclosingSize.getInt("y").setValue(y);
        enclosingSize.getInt("z").setValue(z);
    }

    public void modifyAuthor(String s)
    {
        author.setValue(s);
    }

    public void modifyDescription(String s)
    {
        description.setValue(s);
    }

    public void modifyName(String s)
    {
        CompoundTag namedRegion = regions.getCompound(name.getValue());
        regions.remove(name.getValue());

        name.setValue(s);
        regions.put(s, namedRegion);
    }

    public void modifyRegionCount(int i)
    {
        regionCount.setValue(i);
    }

    public void modifyTimeCreated(long l)
    {
        timeCreated.setValue(l);
    }

    public void modifyTimeModified(long l)
    {
        timeModified.setValue(l);
    }

    public void modifyTotalBlocks(int i)
    {
        totalBlocks.setValue(i);
    }

    public void modifyTotalVolume(int i)
    {
        totalVolume.setValue(i);
    }

    public void modifyPosition(int x, int y, int z)
    {
        position.getInt("x").setValue(x);
        position.getInt("y").setValue(y);
        position.getInt("z").setValue(z);
    }

    public void modifySize(int x, int y, int z)
    {
        size.getInt("x").setValue(x);
        size.getInt("y").setValue(y);
        size.getInt("z").setValue(z);
    }

    public void modifyBlockStatePalette(ListTag<CompoundTag> list)
    {
        blockStatePalette.clear();

        for (CompoundTag t: list)
        {
            blockStatePalette.add(t);
        }
    }

    public void modifyEntities(ListTag<CompoundTag> list)
    {
        entities.clear();

        for (CompoundTag t: list)
        {
            entities.add(t);
        }
    }

    public void modifyPendingBlockTicks(ListTag<CompoundTag> list)
    {
        pendingBlockTicks.clear();

        for (CompoundTag t: list)
        {
            pendingBlockTicks.add(t);
        }
    }

    public void modifyPendingFluidTickss(ListTag<CompoundTag> list)
    {
        pendingFluidTicks.clear();

        for (CompoundTag t: list)
        {
            pendingFluidTicks.add(t);
        }
    }

    public void modifyTileEntities(ListTag<CompoundTag> list)
    {
        tileEntities.clear();

        for (CompoundTag t: list)
        {
            tileEntities.add(t);
        }
    }

    public void modifyBlockStates(long[] arr)
    {
        blockStates.setValue(arr);
    }

    //create root from tags
    public void createRoot()
    {
        root = new CompoundTag();
        createMetadata(root);
        createRegions(root);
        createDataVersion(root);
        createVersion(root);
    }

    private void createMetadata(CompoundTag tag)
    {
        CompoundTag metadata = new CompoundTag();
        tag.put("Metadata", metadata);

        createEnclosingSize(metadata);
        createAuthor(metadata);
        createDescription(metadata);
        createName(metadata);
        createRegionCount(metadata);
        createTimeCreated(metadata);
        createTimeModified(metadata);
        createTotalBlocks(metadata);
        createTotalVolume(metadata);
    }

    private void createRegions(CompoundTag tag)
    {
        regions = new CompoundTag();
        tag.put("Regions", regions);

        createNamedRegion(regions);
    }

    private void createDataVersion(CompoundTag tag)
    {
        dataVersion = new IntTag();
        tag.put("MinecraftDataVersion", dataVersion);
    }

    private void createVersion(CompoundTag tag)
    {
        version = new IntTag();
        tag.put("Version", version);
    }

    private void createEnclosingSize(CompoundTag tag)
    {
        enclosingSize = new CompoundTag();
        tag.put("EnclosingSize", enclosingSize);

        enclosingSize.put("x", new IntTag());
        enclosingSize.put("y", new IntTag());
        enclosingSize.put("z", new IntTag());
    }

    private void createAuthor(CompoundTag tag)
    {
        author = new StringTag();
        tag.put("Author", author);
    }

    private void createDescription(CompoundTag tag)
    {
        description = new StringTag();
        tag.put("Description", description);
    }

    private void createName(CompoundTag tag)
    {
        name = new StringTag("");
        tag.put("Name", name);
    }

    private void createRegionCount(CompoundTag tag)
    {
        regionCount = new IntTag();
        tag.put("RegionCount", regionCount);
    }

    private void createTimeCreated(CompoundTag tag)
    {
        timeCreated = new LongTag();
        tag.put("TimeCreated", timeCreated);
    }

    private void createTimeModified(CompoundTag tag)
    {
        timeModified = new LongTag();
        tag.put("TimeModified", timeModified);
    }

    private void createTotalBlocks(CompoundTag tag)
    {
        totalBlocks = new IntTag();
        tag.put("TotalBlocks", totalBlocks);
    }

    private void createTotalVolume(CompoundTag tag)
    {
        totalVolume = new IntTag();
        tag.put("TotalVolume", totalVolume);
    }

    private void createNamedRegion(CompoundTag tag)
    {
        CompoundTag namedRegion = new CompoundTag();
        tag.put(name.getValue(), namedRegion);

        createPosition(namedRegion);
        createSize(namedRegion);
        createBlockStatePalette(namedRegion);
        createEntities(namedRegion);
        createPendingBlockTicks(namedRegion);
        createPendingFluidTicks(namedRegion);
        createTileEntities(namedRegion);
        createBlockStates(namedRegion);
    }

    private void createPosition(CompoundTag tag)
    {
        position = new CompoundTag();
        tag.put("Position", position);

        position.put("x", new IntTag());
        position.put("y", new IntTag());
        position.put("z", new IntTag());
    }

    private void createSize(CompoundTag tag)
    {
        size = new CompoundTag();
        tag.put("Size", size);

        size.put("x", new IntTag());
        size.put("y", new IntTag());
        size.put("z", new IntTag());
    }

    private void createBlockStatePalette(CompoundTag tag)
    {
        blockStatePalette = new ListTag<CompoundTag>();
        tag.put("BlockStatePalette", blockStatePalette);
    }

    private void createEntities(CompoundTag tag)
    {
        entities = new ListTag<CompoundTag>();
        tag.put("Entities", entities);
    }

    private void createPendingBlockTicks(CompoundTag tag)
    {
        pendingBlockTicks = new ListTag<CompoundTag>();
        tag.put("PendingBlockTicks", pendingBlockTicks);
    }

    private void createPendingFluidTicks(CompoundTag tag)
    {
        pendingFluidTicks = new ListTag<CompoundTag>();
        tag.put("PendingFluidTicks", pendingBlockTicks);
    }

    private void createTileEntities(CompoundTag tag)
    {
        tileEntities = new ListTag<CompoundTag>();
        tag.put("TileEntities", tileEntities);
    }

    private void createBlockStates(CompoundTag tag)
    {
        blockStates = new LongArrayTag();
        tag.put("BlockStates", blockStates);
    }

    //parse from root
    private void rdReadRoot(CompoundTag tag)
    {
        root = tag;

        rdVersion(root.getInt("Version"));
        rdDataVersion(root.getInt("MinecraftDataVersion"));
        rdMetadata(root.getCompound("Metadata"));
        rdRegions(root.getCompound("Regions"));
    }

    private void rdVersion(IntTag tag)
    {
        version = tag;
    }

    private void rdDataVersion(IntTag tag)
    {
        dataVersion = tag;
    }

    private void rdMetadata(CompoundTag tag)
    {
        rdEnclosingSize(tag.getCompound("EnclosingSize"));
        rdAuthor(tag.getString("Author"));
        rdDescription(tag.getString("Description"));
        rdName(tag.getString("Name"));
        rdRegionCount(tag.getInt("RegionCount"));
        rdTimeCreated(tag.getLong("TimeCreated"));
        rdTimeModified(tag.getLong("TimeModified"));
        rdTotalBlocks(tag.getInt("TotalBlocks"));
        rdTotalVolume(tag.getInt("TotalVolume"));
    }

    private void rdEnclosingSize(CompoundTag tag)
    {
        enclosingSize = tag;
    }

    private void rdAuthor(StringTag tag)
    {
        author = tag;
    }

    private void rdDescription(StringTag tag)
    {
        description = tag;
    }

    private void rdName(StringTag tag)
    {
        name = tag;
    }

    private void rdRegionCount(IntTag tag)
    {
        regionCount = tag;
    }

    private void rdTimeCreated(LongTag tag)
    {
        timeCreated = tag;
    }

    private void rdTimeModified(LongTag tag)
    {
        timeModified = tag;
    }

    private void rdTotalBlocks(IntTag tag)
    {
        totalBlocks = tag;
    }

    private void rdTotalVolume(IntTag tag)
    {
        totalVolume = tag;
    }

    private void rdRegions(CompoundTag tag)
    {
        rdNamedRegion(tag.getCompound(name.getValue()));
    }

    private void rdNamedRegion(CompoundTag tag)
    {
        rdPosition(tag.getCompound("Position"));
        rdSize(tag.getCompound("Size"));
        rdBlockStatePalette(tag.<CompoundTag>getList("BlockStatePalette"));
        rdEntities(tag.<CompoundTag>getList("Entities"));
        rdPendingBlockTicks(tag.<CompoundTag>getList("PendingBlockTicks"));
        rdPendingFluidTicks(tag.<CompoundTag>getList("PendingFluidTicks"));
        rdTileEntities(tag.<CompoundTag>getList("TileEntities"));
        rdBlockStates(tag.getLongArray("BlockStates"));
    }

    private void rdPosition(CompoundTag tag) {
        position = tag;
    }

    private void rdSize(CompoundTag tag)
    {
        size = tag;
    }

    private void rdBlockStatePalette(ListTag<CompoundTag> tag)
    {
        blockStatePalette = tag;
    }

    private void rdEntities(ListTag<CompoundTag> tag)
    {
        entities = tag;
    }

    private void rdPendingBlockTicks(ListTag<CompoundTag> tag)
    {
        pendingBlockTicks = tag;
    }

    private void rdPendingFluidTicks(ListTag<CompoundTag> tag)
    {
        pendingFluidTicks = tag;
    }

    private void rdTileEntities(ListTag<CompoundTag> tag)
    {
        tileEntities = tag;
    }

    private void rdBlockStates(LongArrayTag tag)
    {
        blockStates = tag;
    }
}
