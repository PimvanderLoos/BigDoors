package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IShearable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class EntitySnowman extends EntityGolem implements IShearable, IRangedEntity {

    private static final DataWatcherObject<Byte> DATA_PUMPKIN_ID = DataWatcher.defineId(EntitySnowman.class, DataWatcherRegistry.BYTE);
    private static final byte PUMPKIN_FLAG = 16;
    private static final float EYE_HEIGHT = 1.7F;

    public EntitySnowman(EntityTypes<? extends EntitySnowman> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PathfinderGoalArrowAttack(this, 1.25D, 20, 10.0F));
        this.goalSelector.addGoal(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 1.0000001E-5F));
        this.goalSelector.addGoal(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(4, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 10, true, false, (entityliving) -> {
            return entityliving instanceof IMonster;
        }));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 4.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntitySnowman.DATA_PUMPKIN_ID, (byte) 16);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("Pumpkin", this.hasPumpkin());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("Pumpkin")) {
            this.setPumpkin(nbttagcompound.getBoolean("Pumpkin"));
        }

    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            BlockPosition blockposition = new BlockPosition(i, j, k);
            BiomeBase biomebase = this.level.getBiome(blockposition);

            if (biomebase.shouldSnowGolemBurn(blockposition)) {
                this.hurt(DamageSource.ON_FIRE, 1.0F);
            }

            if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return;
            }

            IBlockData iblockdata = Blocks.SNOW.defaultBlockState();

            for (int l = 0; l < 4; ++l) {
                i = MathHelper.floor(this.getX() + (double) ((float) (l % 2 * 2 - 1) * 0.25F));
                j = MathHelper.floor(this.getY());
                k = MathHelper.floor(this.getZ() + (double) ((float) (l / 2 % 2 * 2 - 1) * 0.25F));
                BlockPosition blockposition1 = new BlockPosition(i, j, k);

                if (this.level.getBlockState(blockposition1).isAir() && iblockdata.canSurvive(this.level, blockposition1)) {
                    this.level.setBlockAndUpdate(blockposition1, iblockdata);
                }
            }
        }

    }

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        EntitySnowball entitysnowball = new EntitySnowball(this.level, this);
        double d0 = entityliving.getEyeY() - 1.100000023841858D;
        double d1 = entityliving.getX() - this.getX();
        double d2 = d0 - entitysnowball.getY();
        double d3 = entityliving.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224D;

        entitysnowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEffects.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(entitysnowball);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 1.7F;
    }

    @Override
    protected EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.is(Items.SHEARS) && this.readyForShearing()) {
            this.shear(SoundCategory.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, (Entity) entityhuman);
            if (!this.level.isClientSide) {
                itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastBreakEvent(enumhand);
                });
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public void shear(SoundCategory soundcategory) {
        this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.SNOW_GOLEM_SHEAR, soundcategory, 1.0F, 1.0F);
        if (!this.level.isClientSide()) {
            this.setPumpkin(false);
            this.spawnAtLocation(new ItemStack(Items.CARVED_PUMPKIN), 1.7F);
        }

    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && this.hasPumpkin();
    }

    public boolean hasPumpkin() {
        return ((Byte) this.entityData.get(EntitySnowman.DATA_PUMPKIN_ID) & 16) != 0;
    }

    public void setPumpkin(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntitySnowman.DATA_PUMPKIN_ID);

        if (flag) {
            this.entityData.set(EntitySnowman.DATA_PUMPKIN_ID, (byte) (b0 | 16));
        } else {
            this.entityData.set(EntitySnowman.DATA_PUMPKIN_ID, (byte) (b0 & -17));
        }

    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.SNOW_GOLEM_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.SNOW_GOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.SNOW_GOLEM_DEATH;
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.75F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }
}
