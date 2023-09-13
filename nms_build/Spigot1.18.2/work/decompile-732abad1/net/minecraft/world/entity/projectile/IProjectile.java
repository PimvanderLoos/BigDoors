package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public abstract class IProjectile extends Entity {

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean leftOwner;
    private boolean hasBeenShot;

    IProjectile(EntityTypes<? extends IProjectile> entitytypes, World world) {
        super(entitytypes, world);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUUID();
            this.cachedOwner = entity;
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof WorldServer) {
            this.cachedOwner = ((WorldServer) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public Entity getEffectSource() {
        return (Entity) MoreObjects.firstNonNull(this.getOwner(), this);
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        if (this.ownerUUID != null) {
            nbttagcompound.putUUID("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            nbttagcompound.putBoolean("LeftOwner", true);
        }

        nbttagcompound.putBoolean("HasBeenShot", this.hasBeenShot);
    }

    protected boolean ownedBy(Entity entity) {
        return entity.getUUID().equals(this.ownerUUID);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasUUID("Owner")) {
            this.ownerUUID = nbttagcompound.getUUID("Owner");
        }

        this.leftOwner = nbttagcompound.getBoolean("LeftOwner");
        this.hasBeenShot = nbttagcompound.getBoolean("HasBeenShot");
    }

    @Override
    public void tick() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();

        if (entity != null) {
            Iterator iterator = this.level.getEntities((Entity) this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (entity1) -> {
                return !entity1.isSpectator() && entity1.isPickable();
            }).iterator();

            while (iterator.hasNext()) {
                Entity entity1 = (Entity) iterator.next();

                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        Vec3D vec3d = (new Vec3D(d0, d1, d2)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1).scale((double) f);

        this.setDeltaMovement(vec3d);
        double d3 = vec3d.horizontalDistance();

        this.setYRot((float) (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D));
        this.setXRot((float) (MathHelper.atan2(vec3d.y, d3) * 57.2957763671875D));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin((f + f2) * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        Vec3D vec3d = entity.getDeltaMovement();

        this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, entity.isOnGround() ? 0.0D : vec3d.y, vec3d.z));
    }

    protected void onHit(MovingObjectPosition movingobjectposition) {
        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = movingobjectposition.getType();

        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
            this.onHitEntity((MovingObjectPositionEntity) movingobjectposition);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            this.onHitBlock((MovingObjectPositionBlock) movingobjectposition);
        }

        if (movingobjectposition_enummovingobjecttype != MovingObjectPosition.EnumMovingObjectType.MISS) {
            this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
        }

    }

    protected void onHitEntity(MovingObjectPositionEntity movingobjectpositionentity) {}

    protected void onHitBlock(MovingObjectPositionBlock movingobjectpositionblock) {
        IBlockData iblockdata = this.level.getBlockState(movingobjectpositionblock.getBlockPos());

        iblockdata.onProjectileHit(this.level, iblockdata, movingobjectpositionblock, this);
    }

    @Override
    public void lerpMotion(double d0, double d1, double d2) {
        this.setDeltaMovement(d0, d1, d2);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            this.setXRot((float) (MathHelper.atan2(d1, d3) * 57.2957763671875D));
            this.setYRot((float) (MathHelper.atan2(d0, d2) * 57.2957763671875D));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isPickable()) {
            Entity entity1 = this.getOwner();

            return entity1 == null || this.leftOwner || !entity1.isPassengerOfSameVehicle(entity);
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        Vec3D vec3d = this.getDeltaMovement();
        double d0 = vec3d.horizontalDistance();

        this.setXRot(lerpRotation(this.xRotO, (float) (MathHelper.atan2(vec3d.y, d0) * 57.2957763671875D)));
        this.setYRot(lerpRotation(this.yRotO, (float) (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D)));
    }

    protected static float lerpRotation(float f, float f1) {
        while (f1 - f < -180.0F) {
            f -= 360.0F;
        }

        while (f1 - f >= 180.0F) {
            f += 360.0F;
        }

        return MathHelper.lerp(0.2F, f, f1);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();

        return new PacketPlayOutSpawnEntity(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.recreateFromPacket(packetplayoutspawnentity);
        Entity entity = this.level.getEntity(packetplayoutspawnentity.getData());

        if (entity != null) {
            this.setOwner(entity);
        }

    }

    @Override
    public boolean mayInteract(World world, BlockPosition blockposition) {
        Entity entity = this.getOwner();

        return entity instanceof EntityHuman ? entity.mayInteract(world, blockposition) : entity == null || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }
}
