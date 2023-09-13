package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.PersistentStructureLegacy;
import net.minecraft.world.level.storage.WorldPersistentData;

public class IChunkLoader implements AutoCloseable {

    public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
    private final IOWorker worker;
    protected final DataFixer fixerUpper;
    @Nullable
    private PersistentStructureLegacy legacyStructureHandler;

    public IChunkLoader(Path path, DataFixer datafixer, boolean flag) {
        this.fixerUpper = datafixer;
        this.worker = new IOWorker(path, flag, "chunk");
    }

    public NBTTagCompound upgradeChunkTag(ResourceKey<World> resourcekey, Supplier<WorldPersistentData> supplier, NBTTagCompound nbttagcompound, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> optional) {
        int i = getVersion(nbttagcompound);

        if (i < 1493) {
            nbttagcompound = GameProfileSerializer.update(this.fixerUpper, DataFixTypes.CHUNK, nbttagcompound, i, 1493);
            if (nbttagcompound.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                if (this.legacyStructureHandler == null) {
                    this.legacyStructureHandler = PersistentStructureLegacy.getLegacyStructureHandler(resourcekey, (WorldPersistentData) supplier.get());
                }

                nbttagcompound = this.legacyStructureHandler.updateFromLegacy(nbttagcompound);
            }
        }

        injectDatafixingContext(nbttagcompound, resourcekey, optional);
        nbttagcompound = GameProfileSerializer.update(this.fixerUpper, DataFixTypes.CHUNK, nbttagcompound, Math.max(1493, i));
        if (i < SharedConstants.getCurrentVersion().getWorldVersion()) {
            nbttagcompound.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        }

        nbttagcompound.remove("__context");
        return nbttagcompound;
    }

    public static void injectDatafixingContext(NBTTagCompound nbttagcompound, ResourceKey<World> resourcekey, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> optional) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.putString("dimension", resourcekey.location().toString());
        optional.ifPresent((resourcekey1) -> {
            nbttagcompound1.putString("generator", resourcekey1.location().toString());
        });
        nbttagcompound.put("__context", nbttagcompound1);
    }

    public static int getVersion(NBTTagCompound nbttagcompound) {
        return nbttagcompound.contains("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : -1;
    }

    @Nullable
    public NBTTagCompound read(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        return this.worker.load(chunkcoordintpair);
    }

    public void write(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        this.worker.store(chunkcoordintpair, nbttagcompound);
        if (this.legacyStructureHandler != null) {
            this.legacyStructureHandler.removeIndex(chunkcoordintpair.toLong());
        }

    }

    public void flushWorker() {
        this.worker.synchronize(true).join();
    }

    public void close() throws IOException {
        this.worker.close();
    }

    public ChunkScanAccess chunkScanner() {
        return this.worker;
    }
}
