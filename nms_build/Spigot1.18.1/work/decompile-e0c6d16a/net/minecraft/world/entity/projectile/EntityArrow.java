package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class EntityArrow extends IProjectile {

    private static final double ARROW_BASE_DAMAGE = 2.0D;
    private static final DataWatcherObject<Byte> ID_FLAGS = DataWatcher.defineId(EntityArrow.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Byte> PIERCE_LEVEL = DataWatcher.defineId(EntityArrow.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_CRIT = 1;
    private static final int FLAG_NOPHYSICS = 2;
    private static final int FLAG_CROSSBOW = 4;
    @Nullable
    private IBlockData lastState;
    public boolean inGround;
    protected int inGroundTime;
    public EntityArrow.PickupStatus pickup;
    public int shakeTime;
    public int life;
    private double baseDamage;
    public int knockback;
    private SoundEffect soundEvent;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, World world) {
        super(entitytypes, world);
        this.pickup = EntityArrow.PickupStatus.DISALLOWED;
        this.baseDamage = 2.0D;
        this.soundEvent = this.getDefaultHitGroundSoundEvent();
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPos(d0, d1, d2);
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.getX(), entityliving.getEyeY() - 0.10000000149011612D, entityliving.getZ(), world);
        this.setOwner(entityliving);
        if (entityliving instanceof EntityHuman) {
            this.pickup = EntityArrow.PickupStatus.ALLOWED;
        }

    }

    public void setSoundEvent(SoundEffect soundeffect) {
        this.soundEvent = soundeffect;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        double d1 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d1)) {
            d1 = 1.0D;
        }

        d1 *= 64.0D * getViewScale();
        return d0 < d1 * d1;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EntityArrow.ID_FLAGS, (byte) 0);
        this.entityData.define(EntityArrow.PIERCE_LEVEL, (byte) 0);
    }

    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {
        super.shoot(d0, d1, d2, f, f1);
        this.life = 0;
    }

    @Override
    public void lerpTo(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.setPos(d0, d1, d2);
        this.setRot(f, f1);
    }

    @Override
    public void lerpMotion(double d0, double d1, double d2) {
        super.lerpMotion(d0, d1, d2);
        this.life = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.isNoPhysics();
        Vec3D vec3d = this.getDeltaMovement();

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3d.horizontalDistance();

            this.setYRot((float) (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D));
            this.setXRot((float) (MathHelper.atan2(vec3d.y, d0) * 57.2957763671875D));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPosition blockposition = this.blockPosition();
        IBlockData iblockdata = this.level.getBlockState(blockposition);
        Vec3D vec3d1;

        if (!iblockdata.isAir() && !flag) {
            VoxelShape voxelshape = iblockdata.getCollisionShape(this.level, blockposition);

            if (!voxelshape.isEmpty()) {
                vec3d1 = this.position();
                Iterator iterator = voxelshape.toAabbs().iterator();

                while (iterator.hasNext()) {
                    AxisAlignedBB axisalignedbb = (AxisAlignedBB) iterator.next();

                    if (axisalignedbb.move(blockposition).contains(vec3d1)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || iblockdata.is(Blocks.POWDER_SNOW)) {
            this.clearFire();
        }

        if (this.inGround && !flag) {
            if (this.lastState != iblockdata && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3D vec3d2 = this.position();

            vec3d1 = vec3d2.add(vec3d);
            Object object = this.level.clip(new RayTrace(vec3d2, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this));

            if (((MovingObjectPosition) object).getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                vec3d1 = ((MovingObjectPosition) object).getLocation();
            }

            while (!this.isRemoved()) {
                MovingObjectPositionEntity movingobjectpositionentity = this.findHitEntity(vec3d2, vec3d1);

                if (movingobjectpositionentity != null) {
                    object = movingobjectpositionentity;
                }

                if (object != null && ((MovingObjectPosition) object).getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                    Entity entity = ((MovingObjectPositionEntity) object).getEntity();
                    Entity entity1 = this.getOwner();

                    if (entity instanceof EntityHuman && entity1 instanceof EntityHuman && !((EntityHuman) entity1).canHarmPlayer((EntityHuman) entity)) {
                        object = null;
                        movingobjectpositionentity = null;
                    }
                }

                if (object != null && !flag) {
                    this.onHit((MovingObjectPosition) object);
                    this.hasImpulse = true;
                }

                if (movingobjectpositionentity == null || this.getPierceLevel() <= 0) {
                    break;
                }

                object = null;
            }

            vec3d = this.getDeltaMovement();
            double d1 = vec3d.x;
            double d2 = vec3d.y;
            double d3 = vec3d.z;

            if (this.isCritArrow()) {
                for (int i = 0; i < 4; ++i) {
                    this.level.addParticle(Particles.CRIT, this.getX() + d1 * (double) i / 4.0D, this.getY() + d2 * (double) i / 4.0D, this.getZ() + d3 * (double) i / 4.0D, -d1, -d2 + 0.2D, -d3);
                }
            }

            double d4 = this.getX() + d1;
            double d5 = this.getY() + d2;
            double d6 = this.getZ() + d3;
            double d7 = vec3d.horizontalDistance();

            if (flag) {
                this.setYRot((float) (MathHelper.atan2(-d1, -d3) * 57.2957763671875D));
            } else {
                this.setYRot((float) (MathHelper.atan2(d1, d3) * 57.2957763671875D));
            }

            this.setXRot((float) (MathHelper.atan2(d2, d7) * 57.2957763671875D));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 0.99F;
            float f1 = 0.05F;

            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f2 = 0.25F;

                    this.level.addParticle(Particles.BUBBLE, d4 - d1 * 0.25D, d5 - d2 * 0.25D, d6 - d3 * 0.25D, d1, d2, d3);
                }

                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3d.scale((double) f));
            if (!this.isNoGravity() && !flag) {
                Vec3D vec3d3 = this.getDeltaMovement();

                this.setDeltaMovement(vec3d3.x, vec3d3.y - 0.05000000074505806D, vec3d3.z);
            }

            this.setPos(d4, d5, d6);
            this.checkInsideBlocks();
        }
    }

    private boolean shouldFall() {
        return this.inGround && this.level.noCollision((new AxisAlignedBB(this.position(), this.position())).inflate(0.06D));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3D vec3d = this.getDeltaMovement();

        this.setDeltaMovement(vec3d.multiply((double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        this.life = 0;
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        if (enummovetype != EnumMoveType.SELF && this.shouldFall()) {
            this.startFalling();
        }

    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.discard();
        }

    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }

    }

    @Override
    protected void onHitEntity(MovingObjectPositionEntity movingobjectpositionentity) {
        super.onHitEntity(movingobjectpositionentity);
        Entity entity = movingobjectpositionentity.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = MathHelper.ceil(MathHelper.clamp((double) f * this.baseDamage, 0.0D, 2.147483647E9D));

        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (this.isCritArrow()) {
            long j = (long) this.random.nextInt(i / 2 + 2);

            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource;

        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof EntityLiving) {
                ((EntityLiving) entity1).setLastHurtMob(entity);
            }
        }

        boolean flag = entity.getType() == EntityTypes.ENDERMAN;
        int k = entity.getRemainingFireTicks();

        if (this.isOnFire() && !flag) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    entityliving.setArrowCount(entityliving.getArrowCount() + 1);
                }

                if (this.knockback > 0) {
                    Vec3D vec3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) this.knockback * 0.6D);

                    if (vec3d.lengthSqr() > 0.0D) {
                        entityliving.push(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.level.isClientSide && entity1 instanceof EntityLiving) {
                    EnchantmentManager.doPostHurtEffects(entityliving, entity1);
                    EnchantmentManager.doPostDamageEffects((EntityLiving) entity1, entityliving);
                }

                this.doPostHurtEffects(entityliving);
                if (entity1 != null && entityliving != entity1 && entityliving instanceof EntityHuman && entity1 instanceof EntityPlayer && !this.isSilent()) {
                    ((EntityPlayer) entity1).connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(entityliving);
                }

                if (!this.level.isClientSide && entity1 instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) entity1;

                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriterionTriggers.KILLED_BY_CROSSBOW.trigger(entityplayer, (Collection) this.piercedAndKilledEntities);
                    } else if (!entity.isAlive() && this.shotFromCrossbow()) {
                        CriterionTriggers.KILLED_BY_CROSSBOW.trigger(entityplayer, (Collection) Arrays.asList(entity));
                    }
                }
            }

            this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == EntityArrow.PickupStatus.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }

    }

    @Override
    protected void onHitBlock(MovingObjectPositionBlock movingobjectpositionblock) {
        this.lastState = this.level.getBlockState(movingobjectpositionblock.getBlockPos());
        super.onHitBlock(movingobjectpositionblock);
        Vec3D vec3d = movingobjectpositionblock.getLocation().subtract(this.getX(), this.getY(), this.getZ());

        this.setDeltaMovement(vec3d);
        Vec3D vec3d1 = vec3d.normalize().scale(0.05000000074505806D);

        this.setPosRaw(this.getX() - vec3d1.x, this.getY() - vec3d1.y, this.getZ() - vec3d1.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setSoundEvent(SoundEffects.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.resetPiercedEntities();
    }

    protected SoundEffect getDefaultHitGroundSoundEvent() {
        return SoundEffects.ARROW_HIT;
    }

    protected final SoundEffect getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    protected void doPostHurtEffects(EntityLiving entityliving) {}

    @Nullable
    protected MovingObjectPositionEntity findHitEntity(Vec3D vec3d, Vec3D vec3d1) {
        return ProjectileHelper.getEntityHitResult(this.level, this, vec3d, vec3d1, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putShort("life", (short) this.life);
        if (this.lastState != null) {
            nbttagcompound.put("inBlockState", GameProfileSerializer.writeBlockState(this.lastState));
        }

        nbttagcompound.putByte("shake", (byte) this.shakeTime);
        nbttagcompound.putBoolean("inGround", this.inGround);
        nbttagcompound.putByte("pickup", (byte) this.pickup.ordinal());
        nbttagcompound.putDouble("damage", this.baseDamage);
        nbttagcompound.putBoolean("crit", this.isCritArrow());
        nbttagcompound.putByte("PierceLevel", this.getPierceLevel());
        nbttagcompound.putString("SoundEvent", IRegistry.SOUND_EVENT.getKey(this.soundEvent).toString());
        nbttagcompound.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.life = nbttagcompound.getShort("life");
        if (nbttagcompound.contains("inBlockState", 10)) {
            this.lastState = GameProfileSerializer.readBlockState(nbttagcompound.getCompound("inBlockState"));
        }

        this.shakeTime = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getBoolean("inGround");
        if (nbttagcompound.contains("damage", 99)) {
            this.baseDamage = nbttagcompound.getDouble("damage");
        }

        this.pickup = EntityArrow.PickupStatus.byOrdinal(nbttagcompound.getByte("pickup"));
        this.setCritArrow(nbttagcompound.getBoolean("crit"));
        this.setPierceLevel(nbttagcompound.getByte("PierceLevel"));
        if (nbttagcompound.contains("SoundEvent", 8)) {
            this.soundEvent = (SoundEffect) IRegistry.SOUND_EVENT.getOptional(new MinecraftKey(nbttagcompound.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
        }

        this.setShotFromCrossbow(nbttagcompound.getBoolean("ShotFromCrossbow"));
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        if (entity instanceof EntityHuman) {
            this.pickup = ((EntityHuman) entity).getAbilities().instabuild ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
        }

    }

    @Override
    public void playerTouch(EntityHuman entityhuman) {
        if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
            if (this.tryPickup(entityhuman)) {
                entityhuman.take(this, 1);
                this.discard();
            }

        }
    }

    protected boolean tryPickup(EntityHuman entityhuman) {
        switch (this.pickup) {
            case ALLOWED:
                return entityhuman.getInventory().add(this.getPickupItem());
            case CREATIVE_ONLY:
                return entityhuman.getAbilities().instabuild;
            default:
                return false;
        }
    }

    protected abstract ItemStack getPickupItem();

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void setBaseDamage(double d0) {
        this.baseDamage = d0;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    public void setKnockback(int i) {
        this.knockback = i;
    }

    public int getKnockback() {
        return this.knockback;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected float getEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.13F;
    }

    public void setCritArrow(boolean flag) {
        this.setFlag(1, flag);
    }

    public void setPierceLevel(byte b0) {
        this.entityData.set(EntityArrow.PIERCE_LEVEL, b0);
    }

    private void setFlag(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityArrow.ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityArrow.ID_FLAGS, (byte) (b0 | i));
        } else {
            this.entityData.set(EntityArrow.ID_FLAGS, (byte) (b0 & ~i));
        }

    }

    public boolean isCritArrow() {
        byte b0 = (Byte) this.entityData.get(EntityArrow.ID_FLAGS);

        return (b0 & 1) != 0;
    }

    public boolean shotFromCrossbow() {
        byte b0 = (Byte) this.entityData.get(EntityArrow.ID_FLAGS);

        return (b0 & 4) != 0;
    }

    public byte getPierceLevel() {
        return (Byte) this.entityData.get(EntityArrow.PIERCE_LEVEL);
    }

    public void setEnchantmentEffectsFromEntity(EntityLiving entityliving, float f) {
        int i = EnchantmentManager.getEnchantmentLevel(Enchantments.POWER_ARROWS, entityliving);
        int j = EnchantmentManager.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, entityliving);

        this.setBaseDamage((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.level.getDifficulty().getId() * 0.11F));
        if (i > 0) {
            this.setBaseDamage(this.getBaseDamage() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            this.setKnockback(j);
        }

        if (EnchantmentManager.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, entityliving) > 0) {
            this.setSecondsOnFire(100);
        }

    }

    protected float getWaterInertia() {
        return 0.6F;
    }

    public void setNoPhysics(boolean flag) {
        this.noPhysics = flag;
        this.setFlag(2, flag);
    }

    public boolean isNoPhysics() {
        return !this.level.isClientSide ? this.noPhysics : ((Byte) this.entityData.get(EntityArrow.ID_FLAGS) & 2) != 0;
    }

    public void setShotFromCrossbow(boolean flag) {
        this.setFlag(4, flag);
    }

    public static enum PickupStatus {

        DISALLOWED, ALLOWED, CREATIVE_ONLY;

        private PickupStatus() {}

        public static EntityArrow.PickupStatus byOrdinal(int i) {
            if (i < 0 || i > values().length) {
                i = 0;
            }

            return values()[i];
        }
    }
}
