package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;

public class GameTestHarnessInfo {

    private final GameTestHarnessTestFunction a;
    @Nullable
    private BlockPosition b;
    private final WorldServer c;
    private final Collection<GameTestHarnessListener> d = Lists.newArrayList();
    private final int e;
    private final Collection<GameTestHarnessSequence> f = Lists.newCopyOnWriteArrayList();
    private Object2LongMap<Runnable> g = new Object2LongOpenHashMap();
    private long h;
    private long i;
    private boolean j = false;
    private final Stopwatch k = Stopwatch.createUnstarted();
    private boolean l = false;
    private final EnumBlockRotation m;
    @Nullable
    private Throwable n;

    public GameTestHarnessInfo(GameTestHarnessTestFunction gametestharnesstestfunction, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        this.a = gametestharnesstestfunction;
        this.c = worldserver;
        this.e = gametestharnesstestfunction.c();
        this.m = gametestharnesstestfunction.g().a(enumblockrotation);
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
                    this.v();
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
                        if (this.n == null) {
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

    private void v() {
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

    public WorldServer g() {
        return this.c;
    }

    public boolean h() {
        return this.l && this.n == null;
    }

    public boolean i() {
        return this.n != null;
    }

    public boolean j() {
        return this.j;
    }

    public boolean k() {
        return this.l;
    }

    private void x() {
        if (!this.l) {
            this.l = true;
            this.k.stop();
        }

    }

    public void a(Throwable throwable) {
        this.x();
        this.n = throwable;
        this.d.forEach((gametestharnesslistener) -> {
            gametestharnesslistener.c(this);
        });
    }

    @Nullable
    public Throwable n() {
        return this.n;
    }

    public String toString() {
        return this.c();
    }

    public void a(GameTestHarnessListener gametestharnesslistener) {
        this.d.add(gametestharnesslistener);
    }

    public void a(BlockPosition blockposition, int i) {
        TileEntityStructure tileentitystructure = GameTestHarnessStructures.a(this.s(), blockposition, this.t(), i, this.c, false);

        this.a(tileentitystructure.getPosition());
        tileentitystructure.setStructureName(this.c());
        GameTestHarnessStructures.a(this.b, new BlockPosition(1, 0, -1), this.t(), this.c);
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

    public EnumBlockRotation t() {
        return this.m;
    }

    public GameTestHarnessTestFunction u() {
        return this.a;
    }
}
