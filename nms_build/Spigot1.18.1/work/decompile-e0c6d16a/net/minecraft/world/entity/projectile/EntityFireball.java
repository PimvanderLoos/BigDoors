package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityFireball extends IProjectile {

    public double xPower;
    public double yPower;
    public double zPower;

    protected EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, double d0, double d1, double d2, double d3, double d4, double d5, World world) {
        this(entitytypes, world);
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        if (d6 != 0.0D) {
            this.xPower = d3 / d6 * 0.1D;
            this.yPower = d4 / d6 * 0.1D;
            this.zPower = d5 / d6 * 0.1D;
        }

    }

    public EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, EntityLiving entityliving, double d0, double d1, double d2, World world) {
        this(entitytypes, entityliving.getX(), entityliving.getY(), entityliving.getZ(), d0, d1, d2, world);
        this.setOwner(entityliving);
        this.setRot(entityliving.getYRot(), entityliving.getXRot());
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        double d1 = this.getBoundingBox().getSize() * 4.0D;

        if (Double.isNaN(d1)) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();

        if (!this.level.isClientSide && (entity != null && entity.isRemoved() || !this.level.hasChunkAt(this.blockPosition()))) {
            this.discard();
        } else {
            super.tick();
            if (this.shouldBurn()) {
                this.setSecondsOnFire(1);
            }

            MovingObjectPosition movingobjectposition = ProjectileHelper.getHitResult(this, this::canHitEntity);

            if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                this.onHit(movingobjectposition);
            }

            this.checkInsideBlocks();
            Vec3D vec3d = this.getDeltaMovement();
            double d0 = this.getX() + vec3d.x;
            double d1 = this.getY() + vec3d.y;
            double d2 = this.getZ() + vec3d.z;

            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float f = this.getInertia();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;

                    this.level.addParticle(Particles.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
                }

                f = 0.8F;
            }

            this.setDeltaMovement(vec3d.add(this.xPower, this.yPower, this.zPower).scale((double) f));
            this.level.addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
            this.setPos(d0, d1, d2);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    protected boolean shouldBurn() {
        return true;
    }

    protected ParticleParam getTrailParticle() {
        return Particles.SMOKE;
    }

    protected float getInertia() {
        return 0.95F;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("power", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("power", 6);

            if (nbttaglist.size() == 3) {
                this.xPower = nbttaglist.getDouble(0);
                this.yPower = nbttaglist.getDouble(1);
                this.zPower = nbttaglist.getDouble(2);
            }
        }

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            this.markHurt();
            Entity entity = damagesource.getEntity();

            if (entity != null) {
                Vec3D vec3d = entity.getLookAngle();

                this.setDeltaMovement(vec3d);
                this.xPower = vec3d.x * 0.1D;
                this.yPower = vec3d.y * 0.1D;
                this.zPower = vec3d.z * 0.1D;
                this.setOwner(entity);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();

        return new PacketPlayOutSpawnEntity(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(), this.getYRot(), this.getType(), i, new Vec3D(this.xPower, this.yPower, this.zPower));
    }

    @Override
    public void recreateFromPacket(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.recreateFromPacket(packetplayoutspawnentity);
        double d0 = packetplayoutspawnentity.getXa();
        double d1 = packetplayoutspawnentity.getYa();
        double d2 = packetplayoutspawnentity.getZa();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        if (d3 != 0.0D) {
            this.xPower = d0 / d3 * 0.1D;
            this.yPower = d1 / d3 * 0.1D;
            this.zPower = d2 / d3 * 0.1D;
        }

    }
}
