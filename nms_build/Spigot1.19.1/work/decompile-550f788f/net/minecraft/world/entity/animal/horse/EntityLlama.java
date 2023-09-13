package net.minecraft.world.entity.animal.horse;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.IInventory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLlamaFollow;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTame;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityLlamaSpit;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCarpet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityLlama extends EntityHorseChestedAbstract implements IRangedEntity {

    private static final int MAX_STRENGTH = 5;
    private static final int VARIANTS = 4;
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.of(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
    private static final DataWatcherObject<Integer> DATA_STRENGTH_ID = DataWatcher.defineId(EntityLlama.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_SWAG_ID = DataWatcher.defineId(EntityLlama.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_VARIANT_ID = DataWatcher.defineId(EntityLlama.class, DataWatcherRegistry.INT);
    boolean didSpit;
    @Nullable
    private EntityLlama caravanHead;
    @Nullable
    private EntityLlama caravanTail;

    public EntityLlama(EntityTypes<? extends EntityLlama> entitytypes, World world) {
        super(entitytypes, world);
    }

    public boolean isTraderLlama() {
        return false;
    }

    private void setStrength(int i) {
        this.entityData.set(EntityLlama.DATA_STRENGTH_ID, Math.max(1, Math.min(5, i)));
    }

    private void setRandomStrength(RandomSource randomsource) {
        int i = randomsource.nextFloat() < 0.04F ? 5 : 3;

        this.setStrength(1 + randomsource.nextInt(i));
    }

    public int getStrength() {
        return (Integer) this.entityData.get(EntityLlama.DATA_STRENGTH_ID);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Variant", this.getVariant());
        nbttagcompound.putInt("Strength", this.getStrength());
        if (!this.inventory.getItem(1).isEmpty()) {
            nbttagcompound.put("DecorItem", this.inventory.getItem(1).save(new NBTTagCompound()));
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.setStrength(nbttagcompound.getInt("Strength"));
        super.readAdditionalSaveData(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.contains("DecorItem", 10)) {
            this.inventory.setItem(1, ItemStack.of(nbttagcompound.getCompound("DecorItem")));
        }

        this.updateContainerEquipment();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.addGoal(2, new PathfinderGoalLlamaFollow(this, 2.0999999046325684D));
        this.goalSelector.addGoal(3, new PathfinderGoalArrowAttack(this, 1.25D, 40, 20.0F));
        this.goalSelector.addGoal(3, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.addGoal(4, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.addGoal(5, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.of(Items.HAY_BLOCK), false));
        this.goalSelector.addGoal(6, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(9, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new EntityLlama.c(this));
        this.targetSelector.addGoal(2, new EntityLlama.a(this));
    }

    public static AttributeProvider.Builder createAttributes() {
        return createBaseChestedHorseAttributes().add(GenericAttributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityLlama.DATA_STRENGTH_ID, 0);
        this.entityData.define(EntityLlama.DATA_SWAG_ID, -1);
        this.entityData.define(EntityLlama.DATA_VARIANT_ID, 0);
    }

    public int getVariant() {
        return MathHelper.clamp((Integer) this.entityData.get(EntityLlama.DATA_VARIANT_ID), (int) 0, (int) 3);
    }

    public void setVariant(int i) {
        this.entityData.set(EntityLlama.DATA_VARIANT_ID, i);
    }

    @Override
    protected int getInventorySize() {
        return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
    }

    @Override
    public void positionRider(Entity entity) {
        if (this.hasPassenger(entity)) {
            float f = MathHelper.cos(this.yBodyRot * 0.017453292F);
            float f1 = MathHelper.sin(this.yBodyRot * 0.017453292F);
            float f2 = 0.3F;

            entity.setPos(this.getX() + (double) (0.3F * f1), this.getY() + this.getPassengersRidingOffset() + entity.getMyRidingOffset(), this.getZ() - (double) (0.3F * f));
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double) this.getBbHeight() * 0.6D;
    }

    @Nullable
    @Override
    public EntityLiving getControllingPassenger() {
        return null;
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return EntityLlama.FOOD_ITEMS.test(itemstack);
    }

    @Override
    protected boolean handleEating(EntityHuman entityhuman, ItemStack itemstack) {
        byte b0 = 0;
        byte b1 = 0;
        float f = 0.0F;
        boolean flag = false;

        if (itemstack.is(Items.WHEAT)) {
            b0 = 10;
            b1 = 3;
            f = 2.0F;
        } else if (itemstack.is(Blocks.HAY_BLOCK.asItem())) {
            b0 = 90;
            b1 = 6;
            f = 10.0F;
            if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
                flag = true;
                this.setInLove(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && b0 > 0) {
            this.level.addParticle(Particles.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
                this.ageUp(b0);
            }

            flag = true;
        }

        if (b1 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            flag = true;
            if (!this.level.isClientSide) {
                this.modifyTemper(b1);
            }
        }

        if (flag && !this.isSilent()) {
            SoundEffect soundeffect = this.getEatingSound();

            if (soundeffect != null) {
                this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(), this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

        return flag;
    }

    @Override
    protected boolean isImmobile() {
        return this.isDeadOrDying() || this.isEating();
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        RandomSource randomsource = worldaccess.getRandom();

        this.setRandomStrength(randomsource);
        int i;

        if (groupdataentity instanceof EntityLlama.b) {
            i = ((EntityLlama.b) groupdataentity).variant;
        } else {
            i = randomsource.nextInt(4);
            groupdataentity = new EntityLlama.b(i);
        }

        this.setVariant(i);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Override
    protected SoundEffect getAngrySound() {
        return SoundEffects.LLAMA_ANGRY;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.LLAMA_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.LLAMA_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.LLAMA_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getEatingSound() {
        return SoundEffects.LLAMA_EAT;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.LLAMA_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void playChestEquipsSound() {
        this.playSound(SoundEffects.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public void makeMad() {
        SoundEffect soundeffect = this.getAngrySound();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.getVoicePitch());
        }

    }

    @Override
    public int getInventoryColumns() {
        return this.getStrength();
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean isWearingArmor() {
        return !this.inventory.getItem(1).isEmpty();
    }

    @Override
    public boolean isArmor(ItemStack itemstack) {
        return itemstack.is(TagsItem.WOOL_CARPETS);
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void containerChanged(IInventory iinventory) {
        EnumColor enumcolor = this.getSwag();

        super.containerChanged(iinventory);
        EnumColor enumcolor1 = this.getSwag();

        if (this.tickCount > 20 && enumcolor1 != null && enumcolor1 != enumcolor) {
            this.playSound(SoundEffects.LLAMA_SWAG, 0.5F, 1.0F);
        }

    }

    @Override
    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            super.updateContainerEquipment();
            this.setSwag(getDyeColor(this.inventory.getItem(1)));
        }
    }

    private void setSwag(@Nullable EnumColor enumcolor) {
        this.entityData.set(EntityLlama.DATA_SWAG_ID, enumcolor == null ? -1 : enumcolor.getId());
    }

    @Nullable
    private static EnumColor getDyeColor(ItemStack itemstack) {
        Block block = Block.byItem(itemstack.getItem());

        return block instanceof BlockCarpet ? ((BlockCarpet) block).getColor() : null;
    }

    @Nullable
    public EnumColor getSwag() {
        int i = (Integer) this.entityData.get(EntityLlama.DATA_SWAG_ID);

        return i == -1 ? null : EnumColor.byId(i);
    }

    @Override
    public int getMaxTemper() {
        return 30;
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        return entityanimal != this && entityanimal instanceof EntityLlama && this.canParent() && ((EntityLlama) entityanimal).canParent();
    }

    @Override
    public EntityLlama getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        EntityLlama entityllama = this.makeBabyLlama();

        this.setOffspringAttributes(entityageable, entityllama);
        EntityLlama entityllama1 = (EntityLlama) entityageable;
        int i = this.random.nextInt(Math.max(this.getStrength(), entityllama1.getStrength())) + 1;

        if (this.random.nextFloat() < 0.03F) {
            ++i;
        }

        entityllama.setStrength(i);
        entityllama.setVariant(this.random.nextBoolean() ? this.getVariant() : entityllama1.getVariant());
        return entityllama;
    }

    protected EntityLlama makeBabyLlama() {
        return (EntityLlama) EntityTypes.LLAMA.create(this.level);
    }

    private void spit(EntityLiving entityliving) {
        EntityLlamaSpit entityllamaspit = new EntityLlamaSpit(this.level, this);
        double d0 = entityliving.getX() - this.getX();
        double d1 = entityliving.getY(0.3333333333333333D) - entityllamaspit.getY();
        double d2 = entityliving.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2) * 0.20000000298023224D;

        entityllamaspit.shoot(d0, d1 + d3, d2, 1.5F, 10.0F);
        if (!this.isSilent()) {
            this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        this.level.addFreshEntity(entityllamaspit);
        this.didSpit = true;
    }

    void setDidSpit(boolean flag) {
        this.didSpit = flag;
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        int i = this.calculateFallDamage(f, f1);

        if (i <= 0) {
            return false;
        } else {
            if (f >= 6.0F) {
                this.hurt(damagesource, (float) i);
                if (this.isVehicle()) {
                    Iterator iterator = this.getIndirectPassengers().iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        entity.hurt(damagesource, (float) i);
                    }
                }
            }

            this.playBlockFallSound();
            return true;
        }
    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityLlama entityllama) {
        this.caravanHead = entityllama;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityLlama getCaravanHead() {
        return this.caravanHead;
    }

    @Override
    protected double followLeashSpeed() {
        return 2.0D;
    }

    @Override
    protected void followMommy() {
        if (!this.inCaravan() && this.isBaby()) {
            super.followMommy();
        }

    }

    @Override
    public boolean canEatGrass() {
        return false;
    }

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        this.spit(entityliving);
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, 0.75D * (double) this.getEyeHeight(), (double) this.getBbWidth() * 0.5D);
    }

    private static class c extends PathfinderGoalHurtByTarget {

        public c(EntityLlama entityllama) {
            super(entityllama);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob instanceof EntityLlama) {
                EntityLlama entityllama = (EntityLlama) this.mob;

                if (entityllama.didSpit) {
                    entityllama.setDidSpit(false);
                    return false;
                }
            }

            return super.canContinueToUse();
        }
    }

    private static class a extends PathfinderGoalNearestAttackableTarget<EntityWolf> {

        public a(EntityLlama entityllama) {
            super(entityllama, EntityWolf.class, 16, false, true, (entityliving) -> {
                return !((EntityWolf) entityliving).isTame();
            });
        }

        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.25D;
        }
    }

    private static class b extends EntityAgeable.a {

        public final int variant;

        b(int i) {
            super(true);
            this.variant = i;
        }
    }
}
