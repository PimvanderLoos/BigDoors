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
    public static final double MAX_CENTER_COORDINATE = 2.9999984E7D;
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

    public boolean isWithinBounds(BlockPosition blockposition) {
        return (double) (blockposition.getX() + 1) > this.getMinX() && (double) blockposition.getX() < this.getMaxX() && (double) (blockposition.getZ() + 1) > this.getMinZ() && (double) blockposition.getZ() < this.getMaxZ();
    }

    public boolean isWithinBounds(ChunkCoordIntPair chunkcoordintpair) {
        return (double) chunkcoordintpair.getMaxBlockX() > this.getMinX() && (double) chunkcoordintpair.getMinBlockX() < this.getMaxX() && (double) chunkcoordintpair.getMaxBlockZ() > this.getMinZ() && (double) chunkcoordintpair.getMinBlockZ() < this.getMaxZ();
    }

    public boolean isWithinBounds(double d0, double d1) {
        return d0 > this.getMinX() && d0 < this.getMaxX() && d1 > this.getMinZ() && d1 < this.getMaxZ();
    }

    public boolean isWithinBounds(double d0, double d1, double d2) {
        return d0 > this.getMinX() - d2 && d0 < this.getMaxX() + d2 && d1 > this.getMinZ() - d2 && d1 < this.getMaxZ() + d2;
    }

    public boolean isWithinBounds(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.maxX > this.getMinX() && axisalignedbb.minX < this.getMaxX() && axisalignedbb.maxZ > this.getMinZ() && axisalignedbb.minZ < this.getMaxZ();
    }

    public BlockPosition clampToBounds(double d0, double d1, double d2) {
        return BlockPosition.containing(MathHelper.clamp(d0, this.getMinX(), this.getMaxX()), d1, MathHelper.clamp(d2, this.getMinZ(), this.getMaxZ()));
    }

    public double getDistanceToBorder(Entity entity) {
        return this.getDistanceToBorder(entity.getX(), entity.getZ());
    }

    public VoxelShape getCollisionShape() {
        return this.extent.getCollisionShape();
    }

    public double getDistanceToBorder(double d0, double d1) {
        double d2 = d1 - this.getMinZ();
        double d3 = this.getMaxZ() - d1;
        double d4 = d0 - this.getMinX();
        double d5 = this.getMaxX() - d0;
        double d6 = Math.min(d4, d5);

        d6 = Math.min(d6, d2);
        return Math.min(d6, d3);
    }

    public boolean isInsideCloseToBorder(Entity entity, AxisAlignedBB axisalignedbb) {
        double d0 = Math.max(MathHelper.absMax(axisalignedbb.getXsize(), axisalignedbb.getZsize()), 1.0D);

        return this.getDistanceToBorder(entity) < d0 * 2.0D && this.isWithinBounds(entity.getX(), entity.getZ(), d0);
    }

    public BorderStatus getStatus() {
        return this.extent.getStatus();
    }

    public double getMinX() {
        return this.extent.getMinX();
    }

    public double getMinZ() {
        return this.extent.getMinZ();
    }

    public double getMaxX() {
        return this.extent.getMaxX();
    }

    public double getMaxZ() {
        return this.extent.getMaxZ();
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
        this.extent.onCenterChange();
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderCenterSet(this, d0, d1);
        }

    }

    public double getSize() {
        return this.extent.getSize();
    }

    public long getLerpRemainingTime() {
        return this.extent.getLerpRemainingTime();
    }

    public double getLerpTarget() {
        return this.extent.getLerpTarget();
    }

    public void setSize(double d0) {
        this.extent = new WorldBorder.d(d0);
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderSizeSet(this, d0);
        }

    }

    public void lerpSizeBetween(double d0, double d1, long i) {
        this.extent = (WorldBorder.a) (d0 == d1 ? new WorldBorder.d(d1) : new WorldBorder.b(d0, d1, i));
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderSizeLerping(this, d0, d1, i);
        }

    }

    protected List<IWorldBorderListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(IWorldBorderListener iworldborderlistener) {
        this.listeners.add(iworldborderlistener);
    }

    public void removeListener(IWorldBorderListener iworldborderlistener) {
        this.listeners.remove(iworldborderlistener);
    }

    public void setAbsoluteMaxSize(int i) {
        this.absoluteMaxSize = i;
        this.extent.onAbsoluteMaxSizeChange();
    }

    public int getAbsoluteMaxSize() {
        return this.absoluteMaxSize;
    }

    public double getDamageSafeZone() {
        return this.damageSafeZone;
    }

    public void setDamageSafeZone(double d0) {
        this.damageSafeZone = d0;
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderSetDamageSafeZOne(this, d0);
        }

    }

    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public void setDamagePerBlock(double d0) {
        this.damagePerBlock = d0;
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderSetDamagePerBlock(this, d0);
        }

    }

    public double getLerpSpeed() {
        return this.extent.getLerpSpeed();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int i) {
        this.warningTime = i;
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderSetWarningTime(this, i);
        }

    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int i) {
        this.warningBlocks = i;
        Iterator iterator = this.getListeners().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.onBorderSetWarningBlocks(this, i);
        }

    }

    public void tick() {
        this.extent = this.extent.update();
    }

    public WorldBorder.c createSettings() {
        return new WorldBorder.c(this);
    }

    public void applySettings(WorldBorder.c worldborder_c) {
        this.setCenter(worldborder_c.getCenterX(), worldborder_c.getCenterZ());
        this.setDamagePerBlock(worldborder_c.getDamagePerBlock());
        this.setDamageSafeZone(worldborder_c.getSafeZone());
        this.setWarningBlocks(worldborder_c.getWarningBlocks());
        this.setWarningTime(worldborder_c.getWarningTime());
        if (worldborder_c.getSizeLerpTime() > 0L) {
            this.lerpSizeBetween(worldborder_c.getSize(), worldborder_c.getSizeLerpTarget(), worldborder_c.getSizeLerpTime());
        } else {
            this.setSize(worldborder_c.getSize());
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
            this.updateBox();
        }

        @Override
        public double getMinX() {
            return this.minX;
        }

        @Override
        public double getMaxX() {
            return this.maxX;
        }

        @Override
        public double getMinZ() {
            return this.minZ;
        }

        @Override
        public double getMaxZ() {
            return this.maxZ;
        }

        @Override
        public double getSize() {
            return this.size;
        }

        @Override
        public BorderStatus getStatus() {
            return BorderStatus.STATIONARY;
        }

        @Override
        public double getLerpSpeed() {
            return 0.0D;
        }

        @Override
        public long getLerpRemainingTime() {
            return 0L;
        }

        @Override
        public double getLerpTarget() {
            return this.size;
        }

        private void updateBox() {
            this.minX = MathHelper.clamp(WorldBorder.this.getCenterX() - this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.minZ = MathHelper.clamp(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.maxX = MathHelper.clamp(WorldBorder.this.getCenterX() + this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.maxZ = MathHelper.clamp(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
            this.shape = VoxelShapes.join(VoxelShapes.INFINITY, VoxelShapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), OperatorBoolean.ONLY_FIRST);
        }

        @Override
        public void onAbsoluteMaxSizeChange() {
            this.updateBox();
        }

        @Override
        public void onCenterChange() {
            this.updateBox();
        }

        @Override
        public WorldBorder.a update() {
            return this;
        }

        @Override
        public VoxelShape getCollisionShape() {
            return this.shape;
        }
    }

    private interface a {

        double getMinX();

        double getMaxX();

        double getMinZ();

        double getMaxZ();

        double getSize();

        double getLerpSpeed();

        long getLerpRemainingTime();

        double getLerpTarget();

        BorderStatus getStatus();

        void onAbsoluteMaxSizeChange();

        void onCenterChange();

        WorldBorder.a update();

        VoxelShape getCollisionShape();
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
            this.lerpBegin = SystemUtils.getMillis();
            this.lerpEnd = this.lerpBegin + i;
        }

        @Override
        public double getMinX() {
            return MathHelper.clamp(WorldBorder.this.getCenterX() - this.getSize() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMinZ() {
            return MathHelper.clamp(WorldBorder.this.getCenterZ() - this.getSize() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMaxX() {
            return MathHelper.clamp(WorldBorder.this.getCenterX() + this.getSize() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMaxZ() {
            return MathHelper.clamp(WorldBorder.this.getCenterZ() + this.getSize() / 2.0D, (double) (-WorldBorder.this.absoluteMaxSize), (double) WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getSize() {
            double d0 = (double) (SystemUtils.getMillis() - this.lerpBegin) / this.lerpDuration;

            return d0 < 1.0D ? MathHelper.lerp(d0, this.from, this.to) : this.to;
        }

        @Override
        public double getLerpSpeed() {
            return Math.abs(this.from - this.to) / (double) (this.lerpEnd - this.lerpBegin);
        }

        @Override
        public long getLerpRemainingTime() {
            return this.lerpEnd - SystemUtils.getMillis();
        }

        @Override
        public double getLerpTarget() {
            return this.to;
        }

        @Override
        public BorderStatus getStatus() {
            return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
        }

        @Override
        public void onCenterChange() {}

        @Override
        public void onAbsoluteMaxSizeChange() {}

        @Override
        public WorldBorder.a update() {
            return (WorldBorder.a) (this.getLerpRemainingTime() <= 0L ? WorldBorder.this.new d(this.to) : this);
        }

        @Override
        public VoxelShape getCollisionShape() {
            return VoxelShapes.join(VoxelShapes.INFINITY, VoxelShapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), OperatorBoolean.ONLY_FIRST);
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
            this.damagePerBlock = worldborder.getDamagePerBlock();
            this.safeZone = worldborder.getDamageSafeZone();
            this.warningBlocks = worldborder.getWarningBlocks();
            this.warningTime = worldborder.getWarningTime();
            this.size = worldborder.getSize();
            this.sizeLerpTime = worldborder.getLerpRemainingTime();
            this.sizeLerpTarget = worldborder.getLerpTarget();
        }

        public double getCenterX() {
            return this.centerX;
        }

        public double getCenterZ() {
            return this.centerZ;
        }

        public double getDamagePerBlock() {
            return this.damagePerBlock;
        }

        public double getSafeZone() {
            return this.safeZone;
        }

        public int getWarningBlocks() {
            return this.warningBlocks;
        }

        public int getWarningTime() {
            return this.warningTime;
        }

        public double getSize() {
            return this.size;
        }

        public long getSizeLerpTime() {
            return this.sizeLerpTime;
        }

        public double getSizeLerpTarget() {
            return this.sizeLerpTarget;
        }

        public static WorldBorder.c read(DynamicLike<?> dynamiclike, WorldBorder.c worldborder_c) {
            double d0 = MathHelper.clamp(dynamiclike.get("BorderCenterX").asDouble(worldborder_c.centerX), -2.9999984E7D, 2.9999984E7D);
            double d1 = MathHelper.clamp(dynamiclike.get("BorderCenterZ").asDouble(worldborder_c.centerZ), -2.9999984E7D, 2.9999984E7D);
            double d2 = dynamiclike.get("BorderSize").asDouble(worldborder_c.size);
            long i = dynamiclike.get("BorderSizeLerpTime").asLong(worldborder_c.sizeLerpTime);
            double d3 = dynamiclike.get("BorderSizeLerpTarget").asDouble(worldborder_c.sizeLerpTarget);
            double d4 = dynamiclike.get("BorderSafeZone").asDouble(worldborder_c.safeZone);
            double d5 = dynamiclike.get("BorderDamagePerBlock").asDouble(worldborder_c.damagePerBlock);
            int j = dynamiclike.get("BorderWarningBlocks").asInt(worldborder_c.warningBlocks);
            int k = dynamiclike.get("BorderWarningTime").asInt(worldborder_c.warningTime);

            return new WorldBorder.c(d0, d1, d5, d4, j, k, d2, i, d3);
        }

        public void write(NBTTagCompound nbttagcompound) {
            nbttagcompound.putDouble("BorderCenterX", this.centerX);
            nbttagcompound.putDouble("BorderCenterZ", this.centerZ);
            nbttagcompound.putDouble("BorderSize", this.size);
            nbttagcompound.putLong("BorderSizeLerpTime", this.sizeLerpTime);
            nbttagcompound.putDouble("BorderSafeZone", this.safeZone);
            nbttagcompound.putDouble("BorderDamagePerBlock", this.damagePerBlock);
            nbttagcompound.putDouble("BorderSizeLerpTarget", this.sizeLerpTarget);
            nbttagcompound.putDouble("BorderWarningBlocks", (double) this.warningBlocks);
            nbttagcompound.putDouble("BorderWarningTime", (double) this.warningTime);
        }
    }
}
