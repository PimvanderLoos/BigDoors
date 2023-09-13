package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFleeSun;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRestrictSun;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class EntitySkeletonAbstract extends EntityMonster implements IRangedEntity {

    private final PathfinderGoalBowShoot<EntitySkeletonAbstract> bowGoal = new PathfinderGoalBowShoot<>(this, 1.0D, 20, 15.0F);
    private final PathfinderGoalMeleeAttack meleeGoal = new PathfinderGoalMeleeAttack(this, 1.2D, false) {
        @Override
        public void stop() {
            super.stop();
            EntitySkeletonAbstract.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            EntitySkeletonAbstract.this.setAggressive(true);
        }
    };

    protected EntitySkeletonAbstract(EntityTypes<? extends EntitySkeletonAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.reassessWeaponGoal();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.addGoal(3, new PathfinderGoalFleeSun(this, 1.0D));
        this.goalSelector.addGoal(3, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.addGoal(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    abstract SoundEffect getStepSound();

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    public void aiStep() {
        boolean flag = this.isSunBurnTick();

        if (flag) {
            ItemStack itemstack = this.getItemBySlot(EnumItemSlot.HEAD);

            if (!itemstack.isEmpty()) {
                if (itemstack.isDamageableItem()) {
                    itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                        this.broadcastBreakEvent(EnumItemSlot.HEAD);
                        this.setItemSlot(EnumItemSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                this.setSecondsOnFire(8);
            }
        }

        super.aiStep();
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (this.getVehicle() instanceof EntityCreature) {
            EntityCreature entitycreature = (EntityCreature) this.getVehicle();

            this.yBodyRot = entitycreature.yBodyRot;
        }

    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyDamageScaler difficultydamagescaler) {
        super.populateDefaultEquipmentSlots(difficultydamagescaler);
        this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        this.populateDefaultEquipmentSlots(difficultydamagescaler);
        this.populateDefaultEquipmentEnchantments(difficultydamagescaler);
        this.reassessWeaponGoal();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * difficultydamagescaler.getSpecialMultiplier());
        if (this.getItemBySlot(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setItemSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EnumItemSlot.HEAD.getIndex()] = 0.0F;
            }
        }

        return groupdataentity;
    }

    public void reassessWeaponGoal() {
        if (this.level != null && !this.level.isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW));

            if (itemstack.is(Items.BOW)) {
                byte b0 = 20;

                if (this.level.getDifficulty() != EnumDifficulty.HARD) {
                    b0 = 40;
                }

                this.bowGoal.setMinAttackInterval(b0);
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }

        }
    }

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
        EntityArrow entityarrow = this.getArrow(itemstack, f);
        double d0 = entityliving.getX() - this.getX();
        double d1 = entityliving.getY(0.3333333333333333D) - entityarrow.getY();
        double d2 = entityliving.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEffects.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(entityarrow);
    }

    protected EntityArrow getArrow(ItemStack itemstack, float f) {
        return ProjectileHelper.getMobArrow(this, itemstack, f);
    }

    @Override
    public boolean canFireProjectileWeapon(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.BOW;
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.reassessWeaponGoal();
    }

    @Override
    public void setItemSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        super.setItemSlot(enumitemslot, itemstack);
        if (!this.level.isClientSide) {
            this.reassessWeaponGoal();
        }

    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 1.74F;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.6D;
    }

    public boolean isShaking() {
        return this.isFullyFrozen();
    }
}
