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
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
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
        public void d() {
            super.d();
            EntitySkeletonAbstract.this.setAggressive(false);
        }

        @Override
        public void c() {
            super.c();
            EntitySkeletonAbstract.this.setAggressive(true);
        }
    };

    protected EntitySkeletonAbstract(EntityTypes<? extends EntitySkeletonAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.t();
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.p(), 0.15F, 1.0F);
    }

    abstract SoundEffect p();

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    public void movementTick() {
        boolean flag = this.fs();

        if (flag) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

            if (!itemstack.isEmpty()) {
                if (itemstack.f()) {
                    itemstack.setDamage(itemstack.getDamage() + this.random.nextInt(2));
                    if (itemstack.getDamage() >= itemstack.i()) {
                        this.broadcastItemBreak(EnumItemSlot.HEAD);
                        this.setSlot(EnumItemSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                this.setOnFire(8);
            }
        }

        super.movementTick();
    }

    @Override
    public void passengerTick() {
        super.passengerTick();
        if (this.getVehicle() instanceof EntityCreature) {
            EntityCreature entitycreature = (EntityCreature) this.getVehicle();

            this.yBodyRot = entitycreature.yBodyRot;
        }

    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        this.t();
        this.setCanPickupLoot(this.random.nextFloat() < 0.55F * difficultydamagescaler.d());
        if (this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EnumItemSlot.HEAD.b()] = 0.0F;
            }
        }

        return groupdataentity;
    }

    public void t() {
        if (this.level != null && !this.level.isClientSide) {
            this.goalSelector.a((PathfinderGoal) this.meleeGoal);
            this.goalSelector.a((PathfinderGoal) this.bowGoal);
            ItemStack itemstack = this.b(ProjectileHelper.a((EntityLiving) this, Items.BOW));

            if (itemstack.a(Items.BOW)) {
                byte b0 = 20;

                if (this.level.getDifficulty() != EnumDifficulty.HARD) {
                    b0 = 40;
                }

                this.bowGoal.a(b0);
                this.goalSelector.a(4, this.bowGoal);
            } else {
                this.goalSelector.a(4, this.meleeGoal);
            }

        }
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        ItemStack itemstack = this.h(this.b(ProjectileHelper.a((EntityLiving) this, Items.BOW)));
        EntityArrow entityarrow = this.b(itemstack, f);
        double d0 = entityliving.locX() - this.locX();
        double d1 = entityliving.e(0.3333333333333333D) - entityarrow.locY();
        double d2 = entityliving.locZ() - this.locZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level.getDifficulty().a() * 4));
        this.playSound(SoundEffects.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addEntity(entityarrow);
    }

    protected EntityArrow b(ItemStack itemstack, float f) {
        return ProjectileHelper.a(this, itemstack, f);
    }

    @Override
    public boolean a(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.BOW;
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.t();
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        super.setSlot(enumitemslot, itemstack);
        if (!this.level.isClientSide) {
            this.t();
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.74F;
    }

    @Override
    public double bk() {
        return -0.6D;
    }

    public boolean fw() {
        return this.isFullyFrozen();
    }
}
