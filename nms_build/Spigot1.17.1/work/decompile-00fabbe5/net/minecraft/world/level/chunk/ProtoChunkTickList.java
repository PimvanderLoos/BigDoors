package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickListPriority;
import net.minecraft.world.level.chunk.storage.ChunkRegionLoader;

public class ProtoChunkTickList<T> implements TickList<T> {

    protected final Predicate<T> ignore;
    private final ChunkCoordIntPair chunkPos;
    private final ShortList[] toBeTicked;
    private LevelHeightAccessor levelHeightAccessor;

    public ProtoChunkTickList(Predicate<T> predicate, ChunkCoordIntPair chunkcoordintpair, LevelHeightAccessor levelheightaccessor) {
        this(predicate, chunkcoordintpair, new NBTTagList(), levelheightaccessor);
    }

    public ProtoChunkTickList(Predicate<T> predicate, ChunkCoordIntPair chunkcoordintpair, NBTTagList nbttaglist, LevelHeightAccessor levelheightaccessor) {
        this.ignore = predicate;
        this.chunkPos = chunkcoordintpair;
        this.levelHeightAccessor = levelheightaccessor;
        this.toBeTicked = new ShortList[levelheightaccessor.getSectionsCount()];

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagList nbttaglist1 = nbttaglist.b(i);

            for (int j = 0; j < nbttaglist1.size(); ++j) {
                IChunkAccess.a(this.toBeTicked, i).add(nbttaglist1.d(j));
            }
        }

    }

    public NBTTagList b() {
        return ChunkRegionLoader.a(this.toBeTicked);
    }

    public void a(TickList<T> ticklist, Function<BlockPosition, T> function) {
        for (int i = 0; i < this.toBeTicked.length; ++i) {
            if (this.toBeTicked[i] != null) {
                ShortListIterator shortlistiterator = this.toBeTicked[i].iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();
                    BlockPosition blockposition = ProtoChunk.a(oshort, this.levelHeightAccessor.getSectionYFromSectionIndex(i), this.chunkPos);

                    ticklist.a(blockposition, function.apply(blockposition), 0);
                }

                this.toBeTicked[i].clear();
            }
        }

    }

    @Override
    public boolean a(BlockPosition blockposition, T t0) {
        return false;
    }

    @Override
    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        int j = this.levelHeightAccessor.getSectionIndex(blockposition.getY());

        if (j >= 0 && j < this.levelHeightAccessor.getSectionsCount()) {
            IChunkAccess.a(this.toBeTicked, j).add(ProtoChunk.k(blockposition));
        }
    }

    @Override
    public boolean b(BlockPosition blockposition, T t0) {
        return false;
    }

    @Override
    public int a() {
        return Stream.of(this.toBeTicked).filter(Objects::nonNull).mapToInt(List::size).sum();
    }
}
