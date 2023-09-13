package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityShulkerBullet extends IProjectile {

    private static final double SPEED = 0.15D;
    @Nullable
    private Entity finalTarget;
    @Nullable
    private EnumDirection currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetId;

    public EntityShulkerBullet(EntityTypes<? extends EntityShulkerBullet> entitytypes, World world) {
        super(entitytypes, world);
        this.noPhysics = true;
    }

    public EntityShulkerBullet(World world, EntityLiving entityliving, Entity entity, EnumDirection.EnumAxis enumdirection_enumaxis) {
        this(EntityTypes.SHULKER_BULLET, world);
        this.setShooter(entityliving);
        BlockPosition blockposition = entityliving.getChunkCoordinates();
        double d0 = (double) blockposition.getX() + 0.5D;
        double d1 = (double) blockposition.getY() + 0.5D;
        double d2 = (double) blockposition.getZ() + 0.5D;

        this.setPositionRotation(d0, d1, d2, this.getYRot(), this.getXRot());
        this.finalTarget = entity;
        this.currentMoveDirection = EnumDirection.UP;
        this.a(enumdirection_enumaxis);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.finalTarget != null) {
            nbttagcompound.a("Target", this.finalTarget.getUniqueID());
        }

        if (this.currentMoveDirection != null) {
            nbttagcompound.setInt("Dir", this.currentMoveDirection.b());
        }

        nbttagcompound.setInt("Steps", this.flightSteps);
        nbttagcompound.setDouble("TXD", this.targetDeltaX);
        nbttagcompound.setDouble("TYD", this.targetDeltaY);
        nbttagcompound.setDouble("TZD", this.targetDeltaZ);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.flightSteps = nbttagcompound.getInt("Steps");
        this.targetDeltaX = nbttagcompound.getDouble("TXD");
        this.targetDeltaY = nbttagcompound.getDouble("TYD");
        this.targetDeltaZ = nbttagcompound.getDouble("TZD");
        if (nbttagcompound.hasKeyOfType("Dir", 99)) {
            this.currentMoveDirection = EnumDirection.fromType1(nbttagcompound.getInt("Dir"));
        }

        if (nbttagcompound.b("Target")) {
            this.targetId = nbttagcompound.a("Target");
        }

    }

    @Override
    protected void initDatawatcher() {}

    @Nullable
    private EnumDirection h() {
        return this.currentMoveDirection;
    }

    private void a(@Nullable EnumDirection enumdirection) {
        this.currentMoveDirection = enumdirection;
    }

    private void a(@Nullable EnumDirection.EnumAxis enumdirection_enumaxis) {
        double d0 = 0.5D;
        BlockPosition blockposition;

        if (this.finalTarget == null) {
            blockposition = this.getChunkCoordinates().down();
        } else {
            d0 = (double) this.finalTarget.getHeight() * 0.5D;
            blockposition = new BlockPosition(this.finalTarget.locX(), this.finalTarget.locY() + d0, this.finalTarget.locZ());
        }

        double d1 = (double) blockposition.getX() + 0.5D;
        double d2 = (double) blockposition.getY() + d0;
        double d3 = (double) blockposition.getZ() + 0.5D;
        EnumDirection enumdirection = null;

        if (!blockposition.a((IPosition) this.getPositionVector(), 2.0D)) {
            BlockPosition blockposition1 = this.getChunkCoordinates();
            List<EnumDirection> list = Lists.newArrayList();

            if (enumdirection_enumaxis != EnumDirection.EnumAxis.X) {
                if (blockposition1.getX() < blockposition.getX() && this.level.isEmpty(blockposition1.east())) {
                    list.add(EnumDirection.EAST);
                } else if (blockposition1.getX() > blockposition.getX() && this.level.isEmpty(blockposition1.west())) {
                    list.add(EnumDirection.WEST);
                }
            }

            if (enumdirection_enumaxis != EnumDirection.EnumAxis.Y) {
                if (blockposition1.getY() < blockposition.getY() && this.level.isEmpty(blockposition1.up())) {
                    list.add(EnumDirection.UP);
                } else if (blockposition1.getY() > blockposition.getY() && this.level.isEmpty(blockposition1.down())) {
                    list.add(EnumDirection.DOWN);
                }
            }

            if (enumdirection_enumaxis != EnumDirection.EnumAxis.Z) {
                if (blockposition1.getZ() < blockposition.getZ() && this.level.isEmpty(blockposition1.south())) {
                    list.add(EnumDirection.SOUTH);
                } else if (blockposition1.getZ() > blockposition.getZ() && this.level.isEmpty(blockposition1.north())) {
                    list.add(EnumDirection.NORTH);
                }
            }

            enumdirection = EnumDirection.a(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.level.isEmpty(blockposition1.shift(enumdirection)) && i > 0; --i) {
                    enumdirection = EnumDirection.a(this.random);
                }
            } else {
                enumdirection = (EnumDirection) list.get(this.random.nextInt(list.size()));
            }

            d1 = this.locX() + (double) enumdirection.getAdjacentX();
            d2 = this.locY() + (double) enumdirection.getAdjacentY();
            d3 = this.locZ() + (double) enumdirection.getAdjacentZ();
        }

        this.a(enumdirection);
        double d4 = d1 - this.locX();
        double d5 = d2 - this.locY();
        double d6 = d3 - this.locZ();
        double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);

        if (d7 == 0.0D) {
            this.targetDeltaX = 0.0D;
            this.targetDeltaY = 0.0D;
            this.targetDeltaZ = 0.0D;
        } else {
            this.targetDeltaX = d4 / d7 * 0.15D;
            this.targetDeltaY = d5 / d7 * 0.15D;
            this.targetDeltaZ = d6 / d7 * 0.15D;
        }

        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d;

        if (!this.level.isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                this.finalTarget = ((WorldServer) this.level).getEntity(this.targetId);
                if (this.finalTarget == null) {
                    this.targetId = null;
                }
            }

            if (this.finalTarget != null && this.finalTarget.isAlive() && (!(this.finalTarget instanceof EntityHuman) || !this.finalTarget.isSpectator())) {
                this.targetDeltaX = MathHelper.a(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
                this.targetDeltaY = MathHelper.a(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
                this.targetDeltaZ = MathHelper.a(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
                vec3d = this.getMot();
                this.setMot(vec3d.add((this.targetDeltaX - vec3d.x) * 0.2D, (this.targetDeltaY - vec3d.y) * 0.2D, (this.targetDeltaZ - vec3d.z) * 0.2D));
            } else if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.04D, 0.0D));
            }

            MovingObjectPosition movingobjectposition = ProjectileHelper.a((Entity) this, this::a);

            if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                this.a(movingobjectposition);
            }
        }

        this.checkBlockCollisions();
        vec3d = this.getMot();
        this.setPosition(this.locX() + vec3d.x, this.locY() + vec3d.y, this.locZ() + vec3d.z);
        ProjectileHelper.a(this, 0.5F);
        if (this.level.isClientSide) {
            this.level.addParticle(Particles.END_ROD, this.locX() - vec3d.x, this.locY() - vec3d.y + 0.15D, this.locZ() - vec3d.z, 0.0D, 0.0D, 0.0D);
        } else if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.a(this.currentMoveDirection == null ? null : this.currentMoveDirection.n());
                }
            }

            if (this.currentMoveDirection != null) {
                BlockPosition blockposition = this.getChunkCoordinates();
                EnumDirection.EnumAxis enumdirection_enumaxis = this.currentMoveDirection.n();

                if (this.level.a(blockposition.shift(this.currentMoveDirection), (Entity) this)) {
                    this.a(enumdirection_enumaxis);
                } else {
                    BlockPosition blockposition1 = this.finalTarget.getChunkCoordinates();

                    if (enumdirection_enumaxis == EnumDirection.EnumAxis.X && blockposition.getX() == blockposition1.getX() || enumdirection_enumaxis == EnumDirection.EnumAxis.Z && blockposition.getZ() == blockposition1.getZ() || enumdirection_enumaxis == EnumDirection.EnumAxis.Y && blockposition.getY() == blockposition1.getY()) {
                        this.a(enumdirection_enumaxis);
                    }
                }
            }
        }

    }

    @Override
    protected boolean a(Entity entity) {
        return super.a(entity) && !entity.noPhysics;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean a(double d0) {
        return d0 < 16384.0D;
    }

    @Override
    public float aY() {
        return 1.0F;
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        Entity entity = movingobjectpositionentity.getEntity();
        Entity entity1 = this.getShooter();
        EntityLiving entityliving = entity1 instanceof EntityLiving ? (EntityLiving) entity1 : null;
        boolean flag = entity.damageEntity(DamageSource.a((Entity) this, entityliving).c(), 4.0F);

        if (flag) {
            this.a(entityliving, entity);
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.LEVITATION, 200), (Entity) MoreObjects.firstNonNull(entity1, this));
            }
        }

    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        super.a(movingobjectpositionblock);
        ((WorldServer) this.level).a(Particles.EXPLOSION, this.locX(), this.locY(), this.locZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
        this.playSound(SoundEffects.SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        this.die();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.level.isClientSide) {
            this.playSound(SoundEffects.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((WorldServer) this.level).a(Particles.CRIT, this.locX(), this.locY(), this.locZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.die();
        }

        return true;
    }

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        double d0 = packetplayoutspawnentity.g();
        double d1 = packetplayoutspawnentity.h();
        double d2 = packetplayoutspawnentity.i();

        this.setMot(d0, d1, d2);
    }
}
