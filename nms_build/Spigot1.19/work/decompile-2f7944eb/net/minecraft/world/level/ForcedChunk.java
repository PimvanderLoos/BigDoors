package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.saveddata.PersistentBase;

public class ForcedChunk extends PersistentBase {

    public static final String FILE_ID = "chunks";
    private static final String TAG_FORCED = "Forced";
    private final LongSet chunks;

    private ForcedChunk(LongSet longset) {
        this.chunks = longset;
    }

    public ForcedChunk() {
        this(new LongOpenHashSet());
    }

    public static ForcedChunk load(NBTTagCompound nbttagcompound) {
        return new ForcedChunk(new LongOpenHashSet(nbttagcompound.getLongArray("Forced")));
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.putLongArray("Forced", this.chunks.toLongArray());
        return nbttagcompound;
    }

    public LongSet getChunks() {
        return this.chunks;
    }
}
