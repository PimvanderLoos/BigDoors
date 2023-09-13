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
        this.setPositionRotation(d0, d1, d2, this.getYRot(), this.getXRot());
        this.ah();
        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        if (d6 != 0.0D) {
            this.xPower = d3 / d6 * 0.1D;
            this.yPower = d4 / d6 * 0.1D;
            this.zPower = d5 / d6 * 0.1D;
        }

    }

    public EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, EntityLiving entityliving, double d0, double d1, double d2, World world) {
        this(entitytypes, entityliving.locX(), entityliving.locY(), entityliving.locZ(), d0, d1, d2, world);
        this.setShooter(entityliving);
        this.setYawPitch(entityliving.getYRot(), entityliving.getXRot());
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public boolean a(double d0) {
        double d1 = this.getBoundingBox().a() * 4.0D;

        if (Double.isNaN(d1)) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    @Override
    public void tick() {
        Entity entity = this.getShooter();

        if (!this.level.isClientSide && (entity != null && entity.isRemoved() || !this.level.isLoaded(this.getChunkCoordinates()))) {
            this.die();
        } else {
            super.tick();
            if (this.J_()) {
                this.setOnFire(1);
            }

            MovingObjectPosition movingobjectposition = ProjectileHelper.a((Entity) this, this::a);

            if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                this.a(movingobjectposition);
            }

            this.checkBlockCollisions();
            Vec3D vec3d = this.getMot();
            double d0 = this.locX() + vec3d.x;
            double d1 = this.locY() + vec3d.y;
            double d2 = this.locZ() + vec3d.z;

            ProjectileHelper.a(this, 0.2F);
            float f = this.j();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;

                    this.level.addParticle(Particles.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
                }

                f = 0.8F;
            }

            this.setMot(vec3d.add(this.xPower, this.yPower, this.zPower).a((double) f));
            this.level.addParticle(this.i(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
            this.setPosition(d0, d1, d2);
        }
    }

    @Override
    protected boolean a(Entity entity) {
        return super.a(entity) && !entity.noPhysics;
    }

    protected boolean J_() {
        return true;
    }

    protected ParticleParam i() {
        return Particles.SMOKE;
    }

    protected float j() {
        return 0.95F;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.set("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("power", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("power", 6);

            if (nbttaglist.size() == 3) {
                this.xPower = nbttaglist.h(0);
                this.yPower = nbttaglist.h(1);
                this.zPower = nbttaglist.h(2);
            }
        }

    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public float bp() {
        return 1.0F;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.velocityChanged();
            Entity entity = damagesource.getEntity();

            if (entity != null) {
                Vec3D vec3d = entity.getLookDirection();

                this.setMot(vec3d);
                this.xPower = vec3d.x * 0.1D;
                this.yPower = vec3d.y * 0.1D;
                this.zPower = vec3d.z * 0.1D;
                this.setShooter(entity);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public float aY() {
        return 1.0F;
    }

    @Override
    public Packet<?> getPacket() {
        Entity entity = this.getShooter();
        int i = entity == null ? 0 : entity.getId();

        return new PacketPlayOutSpawnEntity(this.getId(), this.getUniqueID(), this.locX(), this.locY(), this.locZ(), this.getXRot(), this.getYRot(), this.getEntityType(), i, new Vec3D(this.xPower, this.yPower, this.zPower));
    }

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        double d0 = packetplayoutspawnentity.g();
        double d1 = packetplayoutspawnentity.h();
        double d2 = packetplayoutspawnentity.i();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        if (d3 != 0.0D) {
            this.xPower = d0 / d3 * 0.1D;
            this.yPower = d1 / d3 * 0.1D;
            this.zPower = d2 / d3 * 0.1D;
        }

    }
}
