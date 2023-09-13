package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;

public class TickListChunk<T> implements TickList<T> {

    private final List<TickListChunk.a<T>> ticks;
    private final Function<T, MinecraftKey> toId;

    public TickListChunk(Function<T, MinecraftKey> function, List<NextTickListEntry<T>> list, long i) {
        this(function, (List) list.stream().map((nextticklistentry) -> {
            return new TickListChunk.a<>(nextticklistentry.b(), nextticklistentry.pos, (int) (nextticklistentry.triggerTick - i), nextticklistentry.priority);
        }).collect(Collectors.toList()));
    }

    private TickListChunk(Function<T, MinecraftKey> function, List<TickListChunk.a<T>> list) {
        this.ticks = list;
        this.toId = function;
    }

    @Override
    public boolean a(BlockPosition blockposition, T t0) {
        return false;
    }

    @Override
    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        this.ticks.add(new TickListChunk.a<>(t0, blockposition, i, ticklistpriority));
    }

    @Override
    public boolean b(BlockPosition blockposition, T t0) {
        return false;
    }

    public NBTTagList b() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.ticks.iterator();

        while (iterator.hasNext()) {
            TickListChunk.a<T> ticklistchunk_a = (TickListChunk.a) iterator.next();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("i", ((MinecraftKey) this.toId.apply(ticklistchunk_a.type)).toString());
            nbttagcompound.setInt("x", ticklistchunk_a.pos.getX());
            nbttagcompound.setInt("y", ticklistchunk_a.pos.getY());
            nbttagcompound.setInt("z", ticklistchunk_a.pos.getZ());
            nbttagcompound.setInt("t", ticklistchunk_a.delay);
            nbttagcompound.setInt("p", ticklistchunk_a.priority.a());
            nbttaglist.add(nbttagcompound);
        }

        return nbttaglist;
    }

    public static <T> TickListChunk<T> a(NBTTagList nbttaglist, Function<T, MinecraftKey> function, Function<MinecraftKey, T> function1) {
        List<TickListChunk.a<T>> list = Lists.newArrayList();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            T t0 = function1.apply(new MinecraftKey(nbttagcompound.getString("i")));

            if (t0 != null) {
                BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));

                list.add(new TickListChunk.a<>(t0, blockposition, nbttagcompound.getInt("t"), TickListPriority.a(nbttagcompound.getInt("p"))));
            }
        }

        return new TickListChunk<>(function, list);
    }

    public void a(TickList<T> ticklist) {
        this.ticks.forEach((ticklistchunk_a) -> {
            ticklist.a(ticklistchunk_a.pos, ticklistchunk_a.type, ticklistchunk_a.delay, ticklistchunk_a.priority);
        });
    }

    @Override
    public int a() {
        return this.ticks.size();
    }

    private static class a<T> {

        final T type;
        public final BlockPosition pos;
        public final int delay;
        public final TickListPriority priority;

        a(T t0, BlockPosition blockposition, int i, TickListPriority ticklistpriority) {
            this.type = t0;
            this.pos = blockposition;
            this.delay = i;
            this.priority = ticklistpriority;
        }

        public String toString() {
            return this.type + ": " + this.pos + ", " + this.delay + ", " + this.priority;
        }
    }
}
