package net.minecraft.world.entity.animal;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalOfferFlower;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalStrollVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalStrollVillageGolem;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalDefendVillage;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.Vec3D;

public class EntityIronGolem extends EntityGolem implements IEntityAngerable {

    protected static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.defineId(EntityIronGolem.class, DataWatcherRegistry.BYTE);
    private static final int IRON_INGOT_HEAL_AMOUNT = 25;
    private int attackAnimationTick;
    private int offerFlowerTick;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public EntityIronGolem(EntityTypes<? extends EntityIronGolem> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.addGoal(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(2, new PathfinderGoalStrollVillage(this, 0.6D, false));
        this.goalSelector.addGoal(4, new PathfinderGoalStrollVillageGolem(this, 0.6D));
        this.goalSelector.addGoal(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.addGoal(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.addGoal(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 5, false, false, (entityliving) -> {
            return entityliving instanceof IMonster && !(entityliving instanceof EntityCreeper);
        }));
        this.targetSelector.addGoal(4, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityIronGolem.DATA_FLAGS_ID, (byte) 0);
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 100.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.25D).add(GenericAttributes.KNOCKBACK_RESISTANCE, 1.0D).add(GenericAttributes.ATTACK_DAMAGE, 15.0D);
    }

    @Override
    protected int decreaseAirSupply(int i) {
        return i;
    }

    @Override
    protected void doPush(Entity entity) {
        if (entity instanceof IMonster && !(entity instanceof EntityCreeper) && this.getRandom().nextInt(20) == 0) {
            this.setTarget((EntityLiving) entity);
        }

        super.doPush(entity);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        if (this.offerFlowerTick > 0) {
            --this.offerFlowerTick;
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY() - 0.20000000298023224D);
            int k = MathHelper.floor(this.getZ());
            IBlockData iblockdata = this.level.getBlockState(new BlockPosition(i, j, k));

            if (!iblockdata.isAir()) {
                this.level.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

        if (!this.level.isClientSide) {
            this.updatePersistentAnger((WorldServer) this.level, true);
        }

    }

    @Override
    public boolean canAttackType(EntityTypes<?> entitytypes) {
        return this.isPlayerCreated() && entitytypes == EntityTypes.PLAYER ? false : (entitytypes == EntityTypes.CREEPER ? false : super.canAttackType(entitytypes));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("PlayerCreated", this.isPlayerCreated());
        this.addPersistentAngerSaveData(nbttagcompound);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setPlayerCreated(nbttagcompound.getBoolean("PlayerCreated"));
        this.readPersistentAngerSaveData(this.level, nbttagcompound);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(EntityIronGolem.PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        this.remainingPersistentAngerTime = i;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);
        float f = this.getAttackDamage();
        float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
        boolean flag = entity.hurt(DamageSource.mobAttack(this), f1);

        if (flag) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.4000000059604645D, 0.0D));
            this.doEnchantDamageEffects(this, entity);
        }

        this.playSound(SoundEffects.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        EntityIronGolem.CrackLevel entityirongolem_cracklevel = this.getCrackiness();
        boolean flag = super.hurt(damagesource, f);

        if (flag && this.getCrackiness() != entityirongolem_cracklevel) {
            this.playSound(SoundEffects.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }

        return flag;
    }

    public EntityIronGolem.CrackLevel getCrackiness() {
        return EntityIronGolem.CrackLevel.byFraction(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 4) {
            this.attackAnimationTick = 10;
            this.playSound(SoundEffects.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else if (b0 == 11) {
            this.offerFlowerTick = 400;
        } else if (b0 == 34) {
            this.offerFlowerTick = 0;
        } else {
            super.handleEntityEvent(b0);
        }

    }

    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    public void offerFlower(boolean flag) {
        if (flag) {
            this.offerFlowerTick = 400;
            this.level.broadcastEntityEvent(this, (byte) 11);
        } else {
            this.offerFlowerTick = 0;
            this.level.broadcastEntityEvent(this, (byte) 34);
        }

    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.IRON_GOLEM_DEATH;
    }

    @Override
    protected EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!itemstack.is(Items.IRON_INGOT)) {
            return EnumInteractionResult.PASS;
        } else {
            float f = this.getHealth();

            this.heal(25.0F);
            if (this.getHealth() == f) {
                return EnumInteractionResult.PASS;
            } else {
                float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;

                this.playSound(SoundEffects.IRON_GOLEM_REPAIR, 1.0F, f1);
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public int getOfferFlowerTick() {
        return this.offerFlowerTick;
    }

    public boolean isPlayerCreated() {
        return ((Byte) this.entityData.get(EntityIronGolem.DATA_FLAGS_ID) & 1) != 0;
    }

    public void setPlayerCreated(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityIronGolem.DATA_FLAGS_ID);

        if (flag) {
            this.entityData.set(EntityIronGolem.DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(EntityIronGolem.DATA_FLAGS_ID, (byte) (b0 & -2));
        }

    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        BlockPosition blockposition = this.blockPosition();
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata = iworldreader.getBlockState(blockposition1);

        if (!iblockdata.entityCanStandOn(iworldreader, blockposition1, this)) {
            return false;
        } else {
            for (int i = 1; i < 3; ++i) {
                BlockPosition blockposition2 = blockposition.above(i);
                IBlockData iblockdata1 = iworldreader.getBlockState(blockposition2);

                if (!SpawnerCreature.isValidEmptySpawnBlock(iworldreader, blockposition2, iblockdata1, iblockdata1.getFluidState(), EntityTypes.IRON_GOLEM)) {
                    return false;
                }
            }

            return SpawnerCreature.isValidEmptySpawnBlock(iworldreader, blockposition, iworldreader.getBlockState(blockposition), FluidTypes.EMPTY.defaultFluidState(), EntityTypes.IRON_GOLEM) && iworldreader.isUnobstructed(this);
        }
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.875F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }

    public static enum CrackLevel {

        NONE(1.0F), LOW(0.75F), MEDIUM(0.5F), HIGH(0.25F);

        private static final List<EntityIronGolem.CrackLevel> BY_DAMAGE = (List) Stream.of(values()).sorted(Comparator.comparingDouble((entityirongolem_cracklevel) -> {
            return (double) entityirongolem_cracklevel.fraction;
        })).collect(ImmutableList.toImmutableList());
        private final float fraction;

        private CrackLevel(float f) {
            this.fraction = f;
        }

        public static EntityIronGolem.CrackLevel byFraction(float f) {
            Iterator iterator = EntityIronGolem.CrackLevel.BY_DAMAGE.iterator();

            EntityIronGolem.CrackLevel entityirongolem_cracklevel;

            do {
                if (!iterator.hasNext()) {
                    return EntityIronGolem.CrackLevel.NONE;
                }

                entityirongolem_cracklevel = (EntityIronGolem.CrackLevel) iterator.next();
            } while (f >= entityirongolem_cracklevel.fraction);

            return entityirongolem_cracklevel;
        }
    }
}
