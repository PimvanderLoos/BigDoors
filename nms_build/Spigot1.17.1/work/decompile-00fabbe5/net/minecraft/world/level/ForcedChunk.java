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

    public static ForcedChunk b(NBTTagCompound nbttagcompound) {
        return new ForcedChunk(new LongOpenHashSet(nbttagcompound.getLongArray("Forced")));
    }

    @Override
    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.a("Forced", this.chunks.toLongArray());
        return nbttagcompound;
    }

    public LongSet a() {
        return this.chunks;
    }
}
