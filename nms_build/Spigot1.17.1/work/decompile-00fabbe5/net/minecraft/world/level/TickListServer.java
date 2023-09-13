package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TickListServer<T> implements TickList<T> {

    public static final int MAX_TICK_BLOCKS_PER_TICK = 65536;
    protected final Predicate<T> ignore;
    private final Function<T, MinecraftKey> toId;
    private final Set<NextTickListEntry<T>> tickNextTickSet = Sets.newHashSet();
    private final Set<NextTickListEntry<T>> tickNextTickList = Sets.newTreeSet(NextTickListEntry.a());
    private final WorldServer level;
    private final Queue<NextTickListEntry<T>> currentlyTicking = Queues.newArrayDeque();
    private final List<NextTickListEntry<T>> alreadyTicked = Lists.newArrayList();
    private final Consumer<NextTickListEntry<T>> ticker;

    public TickListServer(WorldServer worldserver, Predicate<T> predicate, Function<T, MinecraftKey> function, Consumer<NextTickListEntry<T>> consumer) {
        this.ignore = predicate;
        this.toId = function;
        this.level = worldserver;
        this.ticker = consumer;
    }

    public void b() {
        int i = this.tickNextTickList.size();

        if (i != this.tickNextTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        } else {
            if (i > 65536) {
                i = 65536;
            }

            Iterator<NextTickListEntry<T>> iterator = this.tickNextTickList.iterator();

            this.level.getMethodProfiler().enter("cleaning");

            NextTickListEntry nextticklistentry;

            while (i > 0 && iterator.hasNext()) {
                nextticklistentry = (NextTickListEntry) iterator.next();
                if (nextticklistentry.triggerTick > this.level.getTime()) {
                    break;
                }

                if (this.level.e(nextticklistentry.pos)) {
                    iterator.remove();
                    this.tickNextTickSet.remove(nextticklistentry);
                    this.currentlyTicking.add(nextticklistentry);
                    --i;
                }
            }

            this.level.getMethodProfiler().exitEnter("ticking");

            while ((nextticklistentry = (NextTickListEntry) this.currentlyTicking.poll()) != null) {
                if (this.level.e(nextticklistentry.pos)) {
                    try {
                        this.alreadyTicked.add(nextticklistentry);
                        this.ticker.accept(nextticklistentry);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Exception while ticking");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being ticked");

                        CrashReportSystemDetails.a(crashreportsystemdetails, this.level, nextticklistentry.pos, (IBlockData) null);
                        throw new ReportedException(crashreport);
                    }
                } else {
                    this.a(nextticklistentry.pos, nextticklistentry.b(), 0);
                }
            }

            this.level.getMethodProfiler().exit();
            this.alreadyTicked.clear();
            this.currentlyTicking.clear();
        }
    }

    @Override
    public boolean b(BlockPosition blockposition, T t0) {
        return this.currentlyTicking.contains(new NextTickListEntry<>(blockposition, t0));
    }

    public List<NextTickListEntry<T>> a(ChunkCoordIntPair chunkcoordintpair, boolean flag, boolean flag1) {
        int i = chunkcoordintpair.d() - 2;
        int j = i + 16 + 2;
        int k = chunkcoordintpair.e() - 2;
        int l = k + 16 + 2;

        return this.a(new StructureBoundingBox(i, this.level.getMinBuildHeight(), k, j, this.level.getMaxBuildHeight(), l), flag, flag1);
    }

    public List<NextTickListEntry<T>> a(StructureBoundingBox structureboundingbox, boolean flag, boolean flag1) {
        List<NextTickListEntry<T>> list = this.a((List) null, this.tickNextTickList, structureboundingbox, flag);

        if (flag && list != null) {
            this.tickNextTickSet.removeAll(list);
        }

        list = this.a(list, this.currentlyTicking, structureboundingbox, flag);
        if (!flag1) {
            list = this.a(list, this.alreadyTicked, structureboundingbox, flag);
        }

        return list == null ? Collections.emptyList() : list;
    }

    @Nullable
    private List<NextTickListEntry<T>> a(@Nullable List<NextTickListEntry<T>> list, Collection<NextTickListEntry<T>> collection, StructureBoundingBox structureboundingbox, boolean flag) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) iterator.next();
            BlockPosition blockposition = nextticklistentry.pos;

            if (blockposition.getX() >= structureboundingbox.g() && blockposition.getX() < structureboundingbox.j() && blockposition.getZ() >= structureboundingbox.i() && blockposition.getZ() < structureboundingbox.l()) {
                if (flag) {
                    iterator.remove();
                }

                if (list == null) {
                    list = Lists.newArrayList();
                }

                ((List) list).add(nextticklistentry);
            }
        }

        return (List) list;
    }

    public void a(StructureBoundingBox structureboundingbox, BlockPosition blockposition) {
        List<NextTickListEntry<T>> list = this.a(structureboundingbox, false, false);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) iterator.next();

            if (structureboundingbox.b((BaseBlockPosition) nextticklistentry.pos)) {
                BlockPosition blockposition1 = nextticklistentry.pos.f(blockposition);
                T t0 = nextticklistentry.b();

                this.a(new NextTickListEntry<>(blockposition1, t0, nextticklistentry.triggerTick, nextticklistentry.priority));
            }
        }

    }

    public NBTTagList a(ChunkCoordIntPair chunkcoordintpair) {
        List<NextTickListEntry<T>> list = this.a(chunkcoordintpair, false, true);

        return a(this.toId, list, this.level.getTime());
    }

    private static <T> NBTTagList a(Function<T, MinecraftKey> function, Iterable<NextTickListEntry<T>> iterable, long i) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) iterator.next();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("i", ((MinecraftKey) function.apply(nextticklistentry.b())).toString());
            nbttagcompound.setInt("x", nextticklistentry.pos.getX());
            nbttagcompound.setInt("y", nextticklistentry.pos.getY());
            nbttagcompound.setInt("z", nextticklistentry.pos.getZ());
            nbttagcompound.setInt("t", (int) (nextticklistentry.triggerTick - i));
            nbttagcompound.setInt("p", nextticklistentry.priority.a());
            nbttaglist.add(nbttagcompound);
        }

        return nbttaglist;
    }

    @Override
    public boolean a(BlockPosition blockposition, T t0) {
        return this.tickNextTickSet.contains(new NextTickListEntry<>(blockposition, t0));
    }

    @Override
    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        if (!this.ignore.test(t0)) {
            this.a(new NextTickListEntry<>(blockposition, t0, (long) i + this.level.getTime(), ticklistpriority));
        }

    }

    private void a(NextTickListEntry<T> nextticklistentry) {
        if (!this.tickNextTickSet.contains(nextticklistentry)) {
            this.tickNextTickSet.add(nextticklistentry);
            this.tickNextTickList.add(nextticklistentry);
        }

    }

    @Override
    public int a() {
        return this.tickNextTickSet.size();
    }
}
