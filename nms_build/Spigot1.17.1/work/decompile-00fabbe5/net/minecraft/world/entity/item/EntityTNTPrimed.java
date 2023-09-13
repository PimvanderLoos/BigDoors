package net.minecraft.world.entity.item;

import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;

public class EntityTNTPrimed extends Entity {

    private static final DataWatcherObject<Integer> DATA_FUSE_ID = DataWatcher.a(EntityTNTPrimed.class, DataWatcherRegistry.INT);
    private static final int DEFAULT_FUSE_TIME = 80;
    @Nullable
    public EntityLiving owner;

    public EntityTNTPrimed(EntityTypes<? extends EntityTNTPrimed> entitytypes, World world) {
        super(entitytypes, world);
        this.blocksBuilding = true;
    }

    public EntityTNTPrimed(World world, double d0, double d1, double d2, @Nullable EntityLiving entityliving) {
        this(EntityTypes.TNT, world);
        this.setPosition(d0, d1, d2);
        double d3 = world.random.nextDouble() * 6.2831854820251465D;

        this.setMot(-Math.sin(d3) * 0.02D, 0.20000000298023224D, -Math.cos(d3) * 0.02D);
        this.setFuseTicks(80);
        this.xo = d0;
        this.yo = d1;
        this.zo = d2;
        this.owner = entityliving;
    }

    @Override
    protected void initDatawatcher() {
        this.entityData.register(EntityTNTPrimed.DATA_FUSE_ID, 80);
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isInteractable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setMot(this.getMot().add(0.0D, -0.04D, 0.0D));
        }

        this.move(EnumMoveType.SELF, this.getMot());
        this.setMot(this.getMot().a(0.98D));
        if (this.onGround) {
            this.setMot(this.getMot().d(0.7D, -0.5D, 0.7D));
        }

        int i = this.getFuseTicks() - 1;

        this.setFuseTicks(i);
        if (i <= 0) {
            this.die();
            if (!this.level.isClientSide) {
                this.explode();
            }
        } else {
            this.aR();
            if (this.level.isClientSide) {
                this.level.addParticle(Particles.SMOKE, this.locX(), this.locY() + 0.5D, this.locZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    private void explode() {
        float f = 4.0F;

        this.level.explode(this, this.locX(), this.e(0.0625D), this.locZ(), 4.0F, Explosion.Effect.BREAK);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Fuse", (short) this.getFuseTicks());
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        this.setFuseTicks(nbttagcompound.getShort("Fuse"));
    }

    @Nullable
    public EntityLiving getSource() {
        return this.owner;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.15F;
    }

    public void setFuseTicks(int i) {
        this.entityData.set(EntityTNTPrimed.DATA_FUSE_ID, i);
    }

    public int getFuseTicks() {
        return (Integer) this.entityData.get(EntityTNTPrimed.DATA_FUSE_ID);
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
