package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.level.ChunkCoordIntPair;

public record TickListChunk<T> (T type, BlockPosition pos, int delay, TickListPriority priority) {

    private static final String TAG_ID = "i";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_DELAY = "t";
    private static final String TAG_PRIORITY = "p";
    public static final Strategy<TickListChunk<?>> UNIQUE_TICK_HASH = new Strategy<TickListChunk<?>>() {
        public int hashCode(TickListChunk<?> ticklistchunk) {
            return 31 * ticklistchunk.pos().hashCode() + ticklistchunk.type().hashCode();
        }

        public boolean equals(@Nullable TickListChunk<?> ticklistchunk, @Nullable TickListChunk<?> ticklistchunk1) {
            return ticklistchunk == ticklistchunk1 ? true : (ticklistchunk != null && ticklistchunk1 != null ? ticklistchunk.type() == ticklistchunk1.type() && ticklistchunk.pos().equals(ticklistchunk1.pos()) : false);
        }
    };

    public static <T> void loadTickList(NBTTagList nbttaglist, Function<String, Optional<T>> function, ChunkCoordIntPair chunkcoordintpair, Consumer<TickListChunk<T>> consumer) {
        long i = chunkcoordintpair.toLong();

        for (int j = 0; j < nbttaglist.size(); ++j) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(j);

            loadTick(nbttagcompound, function).ifPresent((ticklistchunk) -> {
                if (ChunkCoordIntPair.asLong(ticklistchunk.pos()) == i) {
                    consumer.accept(ticklistchunk);
                }

            });
        }

    }

    public static <T> Optional<TickListChunk<T>> loadTick(NBTTagCompound nbttagcompound, Function<String, Optional<T>> function) {
        return ((Optional) function.apply(nbttagcompound.getString("i"))).map((object) -> {
            BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));

            return new TickListChunk<>(object, blockposition, nbttagcompound.getInt("t"), TickListPriority.byValue(nbttagcompound.getInt("p")));
        });
    }

    private static NBTTagCompound saveTick(String s, BlockPosition blockposition, int i, TickListPriority ticklistpriority) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("i", s);
        nbttagcompound.putInt("x", blockposition.getX());
        nbttagcompound.putInt("y", blockposition.getY());
        nbttagcompound.putInt("z", blockposition.getZ());
        nbttagcompound.putInt("t", i);
        nbttagcompound.putInt("p", ticklistpriority.getValue());
        return nbttagcompound;
    }

    public static <T> NBTTagCompound saveTick(NextTickListEntry<T> nextticklistentry, Function<T, String> function, long i) {
        return saveTick((String) function.apply(nextticklistentry.type()), nextticklistentry.pos(), (int) (nextticklistentry.triggerTick() - i), nextticklistentry.priority());
    }

    public NBTTagCompound save(Function<T, String> function) {
        return saveTick((String) function.apply(this.type), this.pos, this.delay, this.priority);
    }

    public NextTickListEntry<T> unpack(long i, long j) {
        return new NextTickListEntry<>(this.type, this.pos, i + (long) this.delay, this.priority, j);
    }

    public static <T> TickListChunk<T> probe(T t0, BlockPosition blockposition) {
        return new TickListChunk<>(t0, blockposition, 0, TickListPriority.NORMAL);
    }
}
