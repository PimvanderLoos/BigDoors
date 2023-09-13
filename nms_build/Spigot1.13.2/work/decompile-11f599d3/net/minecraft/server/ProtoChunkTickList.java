package net.minecraft.server;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProtoChunkTickList<T> implements TickList<T> {

    protected final Predicate<T> a;
    protected final Function<T, MinecraftKey> b;
    protected final Function<MinecraftKey, T> c;
    private final ChunkCoordIntPair d;
    private final ShortList[] e = new ShortList[16];

    public ProtoChunkTickList(Predicate<T> predicate, Function<T, MinecraftKey> function, Function<MinecraftKey, T> function1, ChunkCoordIntPair chunkcoordintpair) {
        this.a = predicate;
        this.b = function;
        this.c = function1;
        this.d = chunkcoordintpair;
    }

    public NBTTagList a() {
        return ChunkRegionLoader.a(this.e);
    }

    public void a(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagList nbttaglist1 = nbttaglist.f(i);

            for (int j = 0; j < nbttaglist1.size(); ++j) {
                ProtoChunk.a(this.e, i).add(nbttaglist1.g(j));
            }
        }

    }

    public void a(TickList<T> ticklist, Function<BlockPosition, T> function) {
        for (int i = 0; i < this.e.length; ++i) {
            if (this.e[i] != null) {
                ShortListIterator shortlistiterator = this.e[i].iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();
                    BlockPosition blockposition = ProtoChunk.a(oshort, i, this.d);

                    ticklist.a(blockposition, function.apply(blockposition), 0);
                }

                this.e[i].clear();
            }
        }

    }

    public boolean a(BlockPosition blockposition, T t0) {
        return false;
    }

    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        ProtoChunk.a(this.e, blockposition.getY() >> 4).add(ProtoChunk.i(blockposition));
    }

    public boolean b(BlockPosition blockposition, T t0) {
        return false;
    }
}
