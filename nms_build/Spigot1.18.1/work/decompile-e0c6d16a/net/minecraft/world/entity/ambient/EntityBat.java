package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityBat extends EntityAmbient {

    public static final float FLAP_DEGREES_PER_TICK = 74.48451F;
    public static final int TICKS_PER_FLAP = MathHelper.ceil(2.4166098F);
    private static final DataWatcherObject<Byte> DATA_ID_FLAGS = DataWatcher.defineId(EntityBat.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_RESTING = 1;
    private static final PathfinderTargetCondition BAT_RESTING_TARGETING = PathfinderTargetCondition.forNonCombat().range(4.0D);
    @Nullable
    private BlockPosition targetPosition;

    public EntityBat(EntityTypes<? extends EntityBat> entitytypes, World world) {
        super(entitytypes, world);
        this.setResting(true);
    }

    @Override
    public boolean isFlapping() {
        return !this.isResting() && this.tickCount % EntityBat.TICKS_PER_FLAP == 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityBat.DATA_ID_FLAGS, (byte) 0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95F;
    }

    @Nullable
    @Override
    public SoundEffect getAmbientSound() {
        return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEffects.BAT_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.BAT_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.BAT_DEATH;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    protected void pushEntities() {}

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 6.0D);
    }

    public boolean isResting() {
        return ((Byte) this.entityData.get(EntityBat.DATA_ID_FLAGS) & 1) != 0;
    }

    public void setResting(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityBat.DATA_ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityBat.DATA_ID_FLAGS, (byte) (b0 | 1));
        } else {
            this.entityData.set(EntityBat.DATA_ID_FLAGS, (byte) (b0 & -2));
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isResting()) {
            this.setDeltaMovement(Vec3D.ZERO);
            this.setPosRaw(this.getX(), (double) MathHelper.floor(this.getY()) + 1.0D - (double) this.getBbHeight(), this.getZ());
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        BlockPosition blockposition = this.blockPosition();
        BlockPosition blockposition1 = blockposition.above();

        if (this.isResting()) {
            boolean flag = this.isSilent();

            if (this.level.getBlockState(blockposition1).isRedstoneConductor(this.level, blockposition)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = (float) this.random.nextInt(360);
                }

                if (this.level.getNearestPlayer(EntityBat.BAT_RESTING_TARGETING, this) != null) {
                    this.setResting(false);
                    if (!flag) {
                        this.level.levelEvent((EntityHuman) null, 1025, blockposition, 0);
                    }
                }
            } else {
                this.setResting(false);
                if (!flag) {
                    this.level.levelEvent((EntityHuman) null, 1025, blockposition, 0);
                }
            }
        } else {
            if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerThan((IPosition) this.position(), 2.0D)) {
                this.targetPosition = new BlockPosition(this.getX() + (double) this.random.nextInt(7) - (double) this.random.nextInt(7), this.getY() + (double) this.random.nextInt(6) - 2.0D, this.getZ() + (double) this.random.nextInt(7) - (double) this.random.nextInt(7));
            }

            double d0 = (double) this.targetPosition.getX() + 0.5D - this.getX();
            double d1 = (double) this.targetPosition.getY() + 0.1D - this.getY();
            double d2 = (double) this.targetPosition.getZ() + 0.5D - this.getZ();
            Vec3D vec3d = this.getDeltaMovement();
            Vec3D vec3d1 = vec3d.add((Math.signum(d0) * 0.5D - vec3d.x) * 0.10000000149011612D, (Math.signum(d1) * 0.699999988079071D - vec3d.y) * 0.10000000149011612D, (Math.signum(d2) * 0.5D - vec3d.z) * 0.10000000149011612D);

            this.setDeltaMovement(vec3d1);
            float f = (float) (MathHelper.atan2(vec3d1.z, vec3d1.x) * 57.2957763671875D) - 90.0F;
            float f1 = MathHelper.wrapDegrees(f - this.getYRot());

            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
            if (this.random.nextInt(100) == 0 && this.level.getBlockState(blockposition1).isRedstoneConductor(this.level, blockposition1)) {
                this.setResting(true);
            }
        }

    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            if (!this.level.isClientSide && this.isResting()) {
                this.setResting(false);
            }

            return super.hurt(damagesource, f);
        }
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.entityData.set(EntityBat.DATA_ID_FLAGS, nbttagcompound.getByte("BatFlags"));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putByte("BatFlags", (Byte) this.entityData.get(EntityBat.DATA_ID_FLAGS));
    }

    public static boolean checkBatSpawnRules(EntityTypes<EntityBat> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (blockposition.getY() >= generatoraccess.getSeaLevel()) {
            return false;
        } else {
            int i = generatoraccess.getMaxLocalRawBrightness(blockposition);
            byte b0 = 4;

            if (isHalloween()) {
                b0 = 7;
            } else if (random.nextBoolean()) {
                return false;
            }

            return i > random.nextInt(b0) ? false : checkMobSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, random);
        }
    }

    private static boolean isHalloween() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.get(ChronoField.DAY_OF_MONTH);
        int j = localdate.get(ChronoField.MONTH_OF_YEAR);

        return j == 10 && i >= 20 || j == 11 && i <= 3;
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height / 2.0F;
    }
}
