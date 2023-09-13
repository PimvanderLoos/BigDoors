package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
import java.util.Iterator;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class WorldBorder {

    public static final double MAX_SIZE = 5.9999968E7D;
    private final List<IWorldBorderListener> listeners = Lists.newArrayList();
    private double damagePerBlock = 0.2D;
    private double damageSafeZone = 5.0D;
    private int warningTime = 15;
    private int warningBlocks = 5;
    private double centerX;
    private double centerZ;
    int absoluteMaxSize = 29999984;
    private WorldBorder.a extent = new WorldBorder.d(5.9999968E7D);
    public static final WorldBorder.c DEFAULT_SETTINGS = new WorldBorder.c(0.0D, 0.0D, 0.2D, 5.0D, 5, 15, 5.9999968E7D, 0L, 0.0D);

    public WorldBorder() {}

    public boolean a(BlockPosition blockposition) {
        return (double) (blockposition.getX() + 1) > this.e() && (double) blockposition.getX() < this.g() && (double) (blockposition.getZ() + 1) > this.f() && (double) blockposition.getZ() < this.h();
    }

    public boolean isInBounds(ChunkCoordIntPair chunkcoordintpair) {
        return (double) chunkcoordintpair.f() > this.e() && (double) chunkcoordintpair.d() < this.g() && (double) chunkcoordintpair.g() > this.f() && (double) chunkcoordintpair.e() < this.h();
    }

    public boolean a(double d0, double d1) {
        return d0 > this.e() && d0 < this.g() && d1 > this.f() && d1 < this.h();
    }

    public boolean a(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.maxX > this.e() && axisalignedbb.minX < this.g() && axisalignedbb.maxZ > this.f() && axisalignedbb.minZ < this.h();
    }

    public double a(Entity entity) {
        return this.b(entity.locX(), entity.locZ());
    }

    public VoxelShape c() {
        return this.extent.m();
    }

    public double b(double d0, double d1) {
        double d2 = d1 - this.f();
        double d3 = this.h() - d1;
        double d4 = d0 - this.e();
        double d5 = this.g() - d0;
        double d6 = Math.min(d4, d5);

        d6 = Math.min(d6, d2);
        return Math.min(d6, d3);
    }

    public BorderStatus d() {
        return this.extent.i();
    }

    public double e() {
        return this.extent.a();
    }

    public double f() {
        return this.extent.c();
    }

    public double g() {
        return this.extent.b();
    }

    public double h() {
        return this.extent.d();
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public void setCenter(double d0, double d1) {
        this.centerX = d0;
        this.centerZ = d1;
        this.extent.k();
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1);
        }

    }

    public double getSize() {
        return this.extent.e();
    }

    public long j() {
        return this.extent.g();
    }

    public double k() {
        return this.extent.h();
    }

    public void setSize(double d0) {
        this.extent = new WorldBorder.d(d0);
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0);
        }

    }

    public void transitionSizeBetween(double d0, double d1, long i) {
        this.extent = (WorldBorder.a) (d0 == d1 ? new WorldBorder.d(d1) : new WorldBorder.b(d0, d1, i));
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1, i);
        }

    }

    protected List<IWorldBorderListener> l() {
        return Lists.newArrayList(this.listeners);
    }

    public void a(IWorldBorderListener iworldborderlistener) {
        this.listeners.add(iworldborderlistener);
    }

    public void b(IWorldBorderListener iworldborderlistener) {
        this.listeners.remove(iworldborderlistener);
    }

    public void a(int i) {
        this.absoluteMaxSize = i;
        this.extent.j();
    }

    public int m() {
        return this.absoluteMaxSize;
    }

    public double getDamageBuffer() {
        return this.damageSafeZone;
    }

    public void setDamageBuffer(double d0) {
        this.damageSafeZone = d0;
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.c(this, d0);
        }

    }

    public double getDamageAmount() {
        return this.damagePerBlock;
    }

    public void setDamageAmount(double d0) {
        this.damagePerBlock = d0;
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, d0);
        }

    }

    public double p() {
        return this.extent.f();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int i) {
        this.warningTime = i;
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, i);
        }

    }

    public int getWarningDistance() {
        return this.warningBlocks;
    }

    public void setWarningDistance(int i) {
        this.warningBlocks = i;
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, i);
        }

    }

    public void s() {
        this.extent = this.extent.l();
    }

    public WorldBorder.c t() {
        return new WorldBorder.c(this);
    }

    public void a(WorldBorder.c worldborder_c) {
        this.setCenter(worldborder_c.a(), worldborder_c.b());
        this.setDamageAmount(worldborder_c.c());
        this.setDamageBuffer(worldborder_c.d());
        this.setWarningDistance(worldborder_c.e());
        this.setWarningTime(worldborder_c.f());
        if (worldborder_c.h() > 0L) {
            this.transitionSizeBetween(worldborder_c.g(), worldborder_c.i(), worldborder_c.h());
        } else {
            this.setSize(worldborder_c.g());
        }

    }

    private class d implements WorldBorder.a {

        private final double size;
        private double minX;
        private double minZ;
        private double maxX;
        private double maxZ;
        private VoxelShape shape;

        public d(double d0) {
            this.size = d0;
            this.n();
        }

        @Override
        public double a() {
            return this.minX;
        }

        @Override
        public double b() {
            return this.maxX;
        }

        @Override
        public double c() {
            return this.minZ;
        }

        @Override
        public double d() {
            return this.maxZ;
        }

        @Override
        public double e() {
            return this.size;
        }

        @Override
        public BorderStatus i() {
            return BorderStatus.STATIONARY;
        }

        @Override
        public double f() {
            return 0.0D;
        }

        @Override
        public long g() {
            return 0L;
        }

        @Override
        public double h() {
            return this.size;
        }

        private void n() {
            this.minX = MathHelper.a(WorldBorder.this.getCenterX() - this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.minZ = MathHelper.a(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.maxX = MathHelper.a(WorldBorder.this.getCenterX() + this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.maxZ = MathHelper.a(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.shape = VoxelShapes.a(VoxelShapes.INFINITY, VoxelShapes.create(Math.floor(this.a()), Double.NEGATIVE_INFINITY, Math.floor(this.c()), Math.ceil(this.b()), Double.POSITIVE_INFINITY, Math.ceil(this.d())), OperatorBoolean.ONLY_FIRST);
        }

        @Override
        public void j() {
            this.n();
        }

        @Override
        public void k() {
            this.n();
        }

        @Override
        public WorldBorder.a l() {
            return this;
        }

        @Override
        public VoxelShape m() {
            return this.shape;
        }
    }

    private interface a {

        double a();

        double b();

        double c();

        double d();

        double e();

        double f();

        long g();

        double h();

        BorderStatus i();

        void j();

        void k();

        WorldBorder.a l();

        VoxelShape m();
    }

    private class b implements WorldBorder.a {

        private final double from;
        private final double to;
        private final long lerpEnd;
        private final long lerpBegin;
        private final double lerpDuration;

        b(double d0, double d1, long i) {
            this.from = d0;
            this.to = d1;
            this.lerpDuration = (double) i;
            this.lerpBegin = SystemUtils.getMonotonicMillis();
            this.lerpEnd = this.lerpBegin + i;
        }

        @Override
        public double a() {
            return MathHelper.a(WorldBorder.this.getCenterX() - this.e() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double c() {
            return MathHelper.a(WorldBorder.this.getCenterZ() - this.e() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double b() {
            return MathHelper.a(WorldBorder.this.getCenterX() + this.e() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double d() {
            return MathHelper.a(WorldBorder.this.getCenterZ() + this.e() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double e() {
            double d0 = (double) (SystemUtils.getMonotonicMillis() - this.lerpBegin) / this.lerpDuration;

            return d0 < 1.0D ? MathHelper.d(d0, this.from, this.to) : this.to;
        }

        @Override
        public double f() {
            return Math.abs(this.from - this.to) / (double) (this.lerpEnd - this.lerpBegin);
        }

        @Override
        public long g() {
            return this.lerpEnd - SystemUtils.getMonotonicMillis();
        }

        @Override
        public double h() {
            return this.to;
        }

        @Override
        public BorderStatus i() {
            return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
        }

        @Override
        public void k() {}

        @Override
        public void j() {}

        @Override
        public WorldBorder.a l() {
            return (WorldBorder.a) (this.g() <= 0L ? WorldBorder.this.new d(this.to) : this);
        }

        @Override
        public VoxelShape m() {
            return VoxelShapes.a(VoxelShapes.INFINITY, VoxelShapes.create(Math.floor(this.a()), Double.NEGATIVE_INFINITY, Math.floor(this.c()), Math.ceil(this.b()), Double.POSITIVE_INFINITY, Math.ceil(this.d())), OperatorBoolean.ONLY_FIRST);
        }
    }

    public static class c {

        private final double centerX;
        private final double centerZ;
        private final double damagePerBlock;
        private final double safeZone;
        private final int warningBlocks;
        private final int warningTime;
        private final double size;
        private final long sizeLerpTime;
        private final double sizeLerpTarget;

        c(double d0, double d1, double d2, double d3, int i, int j, double d4, long k, double d5) {
            this.centerX = d0;
            this.centerZ = d1;
            this.damagePerBlock = d2;
            this.safeZone = d3;
            this.warningBlocks = i;
            this.warningTime = j;
            this.size = d4;
            this.sizeLerpTime = k;
            this.sizeLerpTarget = d5;
        }

        c(WorldBorder worldborder) {
            this.centerX = worldborder.getCenterX();
            this.centerZ = worldborder.getCenterZ();
            this.damagePerBlock = worldborder.getDamageAmount();
            this.safeZone = worldborder.getDamageBuffer();
            this.warningBlocks = worldborder.getWarningDistance();
            this.warningTime = worldborder.getWarningTime();
            this.size = worldborder.getSize();
            this.sizeLerpTime = worldborder.j();
            this.sizeLerpTarget = worldborder.k();
        }

        public double a() {
            return this.centerX;
        }

        public double b() {
            return this.centerZ;
        }

        public double c() {
            return this.damagePerBlock;
        }

        public double d() {
            return this.safeZone;
        }

        public int e() {
            return this.warningBlocks;
        }

        public int f() {
            return this.warningTime;
        }

        public double g() {
            return this.size;
        }

        public long h() {
            return this.sizeLerpTime;
        }

        public double i() {
            return this.sizeLerpTarget;
        }

        public static WorldBorder.c a(DynamicLike<?> dynamiclike, WorldBorder.c worldborder_c) {
            double d0 = dynamiclike.get("BorderCenterX").asDouble(worldborder_c.centerX);
            double d1 = dynamiclike.get("BorderCenterZ").asDouble(worldborder_c.centerZ);
            double d2 = dynamiclike.get("BorderSize").asDouble(worldborder_c.size);
            long i = dynamiclike.get("BorderSizeLerpTime").asLong(worldborder_c.sizeLerpTime);
            double d3 = dynamiclike.get("BorderSizeLerpTarget").asDouble(worldborder_c.sizeLerpTarget);
            double d4 = dynamiclike.get("BorderSafeZone").asDouble(worldborder_c.safeZone);
            double d5 = dynamiclike.get("BorderDamagePerBlock").asDouble(worldborder_c.damagePerBlock);
            int j = dynamiclike.get("BorderWarningBlocks").asInt(worldborder_c.warningBlocks);
            int k = dynamiclike.get("BorderWarningTime").asInt(worldborder_c.warningTime);

            return new WorldBorder.c(d0, d1, d5, d4, j, k, d2, i, d3);
        }

        public void a(NBTTagCompound nbttagcompound) {
            nbttagcompound.setDouble("BorderCenterX", this.centerX);
            nbttagcompound.setDouble("BorderCenterZ", this.centerZ);
            nbttagcompound.setDouble("BorderSize", this.size);
            nbttagcompound.setLong("BorderSizeLerpTime", this.sizeLerpTime);
            nbttagcompound.setDouble("BorderSafeZone", this.safeZone);
            nbttagcompound.setDouble("BorderDamagePerBlock", this.damagePerBlock);
            nbttagcompound.setDouble("BorderSizeLerpTarget", this.sizeLerpTarget);
            nbttagcompound.setDouble("BorderWarningBlocks", (double) this.warningBlocks);
            nbttagcompound.setDouble("BorderWarningTime", (double) this.warningTime);
        }
    }
}
