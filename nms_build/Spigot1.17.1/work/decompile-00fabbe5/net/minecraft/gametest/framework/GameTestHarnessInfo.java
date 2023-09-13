package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.phys.AxisAlignedBB;

public class GameTestHarnessInfo {

    private final GameTestHarnessTestFunction testFunction;
    @Nullable
    private BlockPosition structureBlockPos;
    private final WorldServer level;
    private final Collection<GameTestHarnessListener> listeners = Lists.newArrayList();
    private final int timeoutTicks;
    private final Collection<GameTestHarnessSequence> sequences = Lists.newCopyOnWriteArrayList();
    private final Object2LongMap<Runnable> runAtTickTimeMap = new Object2LongOpenHashMap();
    private long startTick;
    private long tickCount;
    private boolean started;
    private final Stopwatch timer = Stopwatch.createUnstarted();
    private boolean done;
    private final EnumBlockRotation rotation;
    @Nullable
    private Throwable error;
    @Nullable
    private TileEntityStructure structureBlockEntity;

    public GameTestHarnessInfo(GameTestHarnessTestFunction gametestharnesstestfunction, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        this.testFunction = gametestharnesstestfunction;
        this.level = worldserver;
        this.timeoutTicks = gametestharnesstestfunction.c();
        this.rotation = gametestharnesstestfunction.g().a(enumblockrotation);
    }

    void a(BlockPosition blockposition) {
        this.structureBlockPos = blockposition;
    }

    void a() {
        this.startTick = this.level.getTime() + 1L + this.testFunction.f();
        this.timer.start();
    }

    public void b() {
        if (!this.k()) {
            this.A();
            if (this.k()) {
                if (this.error != null) {
                    this.listeners.forEach((gametestharnesslistener) -> {
                        gametestharnesslistener.c(this);
                    });
                } else {
                    this.listeners.forEach((gametestharnesslistener) -> {
                        gametestharnesslistener.b(this);
                    });
                }
            }

        }
    }

    private void A() {
        this.tickCount = this.level.getTime() - this.startTick;
        if (this.tickCount >= 0L) {
            if (this.tickCount == 0L) {
                this.B();
            }

            ObjectIterator objectiterator = this.runAtTickTimeMap.object2LongEntrySet().iterator();

            while (objectiterator.hasNext()) {
                Entry<Runnable> entry = (Entry) objectiterator.next();

                if (entry.getLongValue() <= this.tickCount) {
                    try {
                        ((Runnable) entry.getKey()).run();
                    } catch (Exception exception) {
                        this.a((Throwable) exception);
                    }

                    objectiterator.remove();
                }
            }

            if (this.tickCount > (long) this.timeoutTicks) {
                if (this.sequences.isEmpty()) {
                    this.a((Throwable) (new GameTestHarnessTimeout("Didn't succeed or fail within " + this.testFunction.c() + " ticks")));
                } else {
                    this.sequences.forEach((gametestharnesssequence) -> {
                        gametestharnesssequence.b(this.tickCount);
                    });
                    if (this.error == null) {
                        this.a((Throwable) (new GameTestHarnessTimeout("No sequences finished")));
                    }
                }
            } else {
                this.sequences.forEach((gametestharnesssequence) -> {
                    gametestharnesssequence.a(this.tickCount);
                });
            }

        }
    }

    private void B() {
        if (this.started) {
            throw new IllegalStateException("Test already started");
        } else {
            this.started = true;

            try {
                this.testFunction.a(new GameTestHarnessHelper(this));
            } catch (Exception exception) {
                this.a((Throwable) exception);
            }

        }
    }

    public void a(long i, Runnable runnable) {
        this.runAtTickTimeMap.put(runnable, i);
    }

    public String c() {
        return this.testFunction.a();
    }

    public BlockPosition d() {
        return this.structureBlockPos;
    }

    @Nullable
    public BaseBlockPosition e() {
        TileEntityStructure tileentitystructure = this.C();

        return tileentitystructure == null ? null : tileentitystructure.i();
    }

    @Nullable
    public AxisAlignedBB f() {
        TileEntityStructure tileentitystructure = this.C();

        return tileentitystructure == null ? null : GameTestHarnessStructures.a(tileentitystructure);
    }

    @Nullable
    private TileEntityStructure C() {
        return (TileEntityStructure) this.level.getTileEntity(this.structureBlockPos);
    }

    public WorldServer g() {
        return this.level;
    }

    public boolean h() {
        return this.done && this.error == null;
    }

    public boolean i() {
        return this.error != null;
    }

    public boolean j() {
        return this.started;
    }

    public boolean k() {
        return this.done;
    }

    public long l() {
        return this.timer.elapsed(TimeUnit.MILLISECONDS);
    }

    private void D() {
        if (!this.done) {
            this.done = true;
            this.timer.stop();
        }

    }

    public void m() {
        if (this.error == null) {
            this.D();
        }

    }

    public void a(Throwable throwable) {
        this.error = throwable;
        this.D();
    }

    @Nullable
    public Throwable n() {
        return this.error;
    }

    public String toString() {
        return this.c();
    }

    public void a(GameTestHarnessListener gametestharnesslistener) {
        this.listeners.add(gametestharnesslistener);
    }

    public void a(BlockPosition blockposition, int i) {
        this.structureBlockEntity = GameTestHarnessStructures.a(this.t(), blockposition, this.u(), i, this.level, false);
        this.structureBlockPos = this.structureBlockEntity.getPosition();
        this.structureBlockEntity.setStructureName(this.c());
        GameTestHarnessStructures.a(this.structureBlockPos, new BlockPosition(1, 0, -1), this.u(), this.level);
        this.listeners.forEach((gametestharnesslistener) -> {
            gametestharnesslistener.a(this);
        });
    }

    public void o() {
        if (this.structureBlockEntity == null) {
            throw new IllegalStateException("Expected structure to be initialized, but it was null");
        } else {
            StructureBoundingBox structureboundingbox = GameTestHarnessStructures.b(this.structureBlockEntity);

            GameTestHarnessStructures.a(structureboundingbox, this.structureBlockPos.getY(), this.level);
        }
    }

    long p() {
        return this.tickCount;
    }

    GameTestHarnessSequence q() {
        GameTestHarnessSequence gametestharnesssequence = new GameTestHarnessSequence(this);

        this.sequences.add(gametestharnesssequence);
        return gametestharnesssequence;
    }

    public boolean r() {
        return this.testFunction.d();
    }

    public boolean s() {
        return !this.testFunction.d();
    }

    public String t() {
        return this.testFunction.b();
    }

    public EnumBlockRotation u() {
        return this.rotation;
    }

    public GameTestHarnessTestFunction v() {
        return this.testFunction;
    }

    public int w() {
        return this.timeoutTicks;
    }

    public boolean x() {
        return this.testFunction.h();
    }

    public int y() {
        return this.testFunction.i();
    }

    public int z() {
        return this.testFunction.j();
    }
}
