package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;

public class GameTestHarnessInfo {

    private final GameTestHarnessTestFunction a;
    private BlockPosition b;
    private final WorldServer c;
    private final Collection<GameTestHarnessListener> d;
    private final int e;
    private final Collection<GameTestHarnessSequence> f;
    private Object2LongMap<Runnable> g;
    private long h;
    private long i;
    private boolean j;
    private final Stopwatch k;
    private boolean l;
    @Nullable
    private Throwable m;

    public GameTestHarnessInfo(GameTestHarnessTestFunction gametestharnesstestfunction, WorldServer worldserver) {
        this.d = Lists.newArrayList();
        this.f = Lists.newCopyOnWriteArrayList();
        this.g = new Object2LongOpenHashMap();
        this.j = false;
        this.k = Stopwatch.createUnstarted();
        this.l = false;
        this.a = gametestharnesstestfunction;
        this.c = worldserver;
        this.e = gametestharnesstestfunction.c();
    }

    public GameTestHarnessInfo(GameTestHarnessTestFunction gametestharnesstestfunction, BlockPosition blockposition, WorldServer worldserver) {
        this(gametestharnesstestfunction, worldserver);
        this.a(blockposition);
    }

    void a(BlockPosition blockposition) {
        this.b = blockposition;
    }

    void a() {
        this.h = this.c.getTime() + 1L + this.a.f();
        this.k.start();
    }

    public void b() {
        if (!this.k()) {
            this.i = this.c.getTime() - this.h;
            if (this.i >= 0L) {
                if (this.i == 0L) {
                    this.t();
                }

                ObjectIterator objectiterator = this.g.object2LongEntrySet().iterator();

                while (objectiterator.hasNext()) {
                    Entry<Runnable> entry = (Entry) objectiterator.next();

                    if (entry.getLongValue() <= this.i) {
                        try {
                            ((Runnable) entry.getKey()).run();
                        } catch (Exception exception) {
                            this.a((Throwable) exception);
                        }

                        objectiterator.remove();
                    }
                }

                if (this.i > (long) this.e) {
                    if (this.f.isEmpty()) {
                        this.a((Throwable) (new GameTestHarnessTimeout("Didn't succeed or fail within " + this.a.c() + " ticks")));
                    } else {
                        this.f.forEach((gametestharnesssequence) -> {
                            gametestharnesssequence.b(this.i);
                        });
                        if (this.m == null) {
                            this.a((Throwable) (new GameTestHarnessTimeout("No sequences finished")));
                        }
                    }
                } else {
                    this.f.forEach((gametestharnesssequence) -> {
                        gametestharnesssequence.a(this.i);
                    });
                }

            }
        }
    }

    private void t() {
        if (this.j) {
            throw new IllegalStateException("Test already started");
        } else {
            this.j = true;

            try {
                this.a.a(new GameTestHarnessHelper(this));
            } catch (Exception exception) {
                this.a((Throwable) exception);
            }

        }
    }

    public String c() {
        return this.a.a();
    }

    public BlockPosition d() {
        return this.b;
    }

    @Nullable
    public BlockPosition e() {
        TileEntityStructure tileentitystructure = this.u();

        return tileentitystructure == null ? null : tileentitystructure.j();
    }

    @Nullable
    private TileEntityStructure u() {
        return (TileEntityStructure) this.c.getTileEntity(this.b);
    }

    public WorldServer g() {
        return this.c;
    }

    public boolean h() {
        return this.l && this.m == null;
    }

    public boolean i() {
        return this.m != null;
    }

    public boolean j() {
        return this.j;
    }

    public boolean k() {
        return this.l;
    }

    private void v() {
        if (!this.l) {
            this.l = true;
            this.k.stop();
        }

    }

    public void a(Throwable throwable) {
        this.v();
        this.m = throwable;
        this.d.forEach((gametestharnesslistener) -> {
            gametestharnesslistener.c(this);
        });
    }

    @Nullable
    public Throwable n() {
        return this.m;
    }

    public String toString() {
        return this.c();
    }

    public void a(GameTestHarnessListener gametestharnesslistener) {
        this.d.add(gametestharnesslistener);
    }

    public void a(int i) {
        TileEntityStructure tileentitystructure = GameTestHarnessStructures.a(this.a.b(), this.b, i, this.c, false);

        tileentitystructure.setStructureName(this.c());
        GameTestHarnessStructures.a(this.b.b(1, 0, -1), this.c);
        this.d.forEach((gametestharnesslistener) -> {
            gametestharnesslistener.a(this);
        });
    }

    public boolean q() {
        return this.a.d();
    }

    public boolean r() {
        return !this.a.d();
    }

    public String s() {
        return this.a.b();
    }
}
