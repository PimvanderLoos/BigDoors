package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFleeSun;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalNearestVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWaterJumpAbstract;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockSweetBerryBush;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityFox extends EntityAnimal {

    private static final DataWatcherObject<Integer> DATA_TYPE_ID = DataWatcher.defineId(EntityFox.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.defineId(EntityFox.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_SITTING = 1;
    public static final int FLAG_CROUCHING = 4;
    public static final int FLAG_INTERESTED = 8;
    public static final int FLAG_POUNCING = 16;
    private static final int FLAG_SLEEPING = 32;
    private static final int FLAG_FACEPLANTED = 64;
    private static final int FLAG_DEFENDING = 128;
    public static final DataWatcherObject<Optional<UUID>> DATA_TRUSTED_ID_0 = DataWatcher.defineId(EntityFox.class, DataWatcherRegistry.OPTIONAL_UUID);
    public static final DataWatcherObject<Optional<UUID>> DATA_TRUSTED_ID_1 = DataWatcher.defineId(EntityFox.class, DataWatcherRegistry.OPTIONAL_UUID);
    static final Predicate<EntityItem> ALLOWED_ITEMS = (entityitem) -> {
        return !entityitem.hasPickUpDelay() && entityitem.isAlive();
    };
    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = (entity) -> {
        if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) entity;

            return entityliving.getLastHurtMob() != null && entityliving.getLastHurtMobTimestamp() < entityliving.tickCount + 600;
        }
    };
    static final Predicate<Entity> STALKABLE_PREY = (entity) -> {
        return entity instanceof EntityChicken || entity instanceof EntityRabbit;
    };
    private static final Predicate<Entity> AVOID_PLAYERS = (entity) -> {
        return !entity.isDiscrete() && IEntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);
    };
    private static final int MIN_TICKS_BEFORE_EAT = 600;
    private PathfinderGoal landTargetGoal;
    private PathfinderGoal turtleEggTargetGoal;
    private PathfinderGoal fishTargetGoal;
    private float interestedAngle;
    private float interestedAngleO;
    float crouchAmount;
    float crouchAmountO;
    private int ticksSinceEaten;

    public EntityFox(EntityTypes<? extends EntityFox> entitytypes, World world) {
        super(entitytypes, world);
        this.lookControl = new EntityFox.k();
        this.moveControl = new EntityFox.m();
        this.setPathfindingMalus(PathType.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 0.0F);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityFox.DATA_TRUSTED_ID_0, Optional.empty());
        this.entityData.define(EntityFox.DATA_TRUSTED_ID_1, Optional.empty());
        this.entityData.define(EntityFox.DATA_TYPE_ID, 0);
        this.entityData.define(EntityFox.DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        this.landTargetGoal = new PathfinderGoalNearestAttackableTarget<>(this, EntityAnimal.class, 10, false, false, (entityliving) -> {
            return entityliving instanceof EntityChicken || entityliving instanceof EntityRabbit;
        });
        this.turtleEggTargetGoal = new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, false, false, EntityTurtle.BABY_ON_LAND_SELECTOR);
        this.fishTargetGoal = new PathfinderGoalNearestAttackableTarget<>(this, EntityFish.class, 20, false, false, (entityliving) -> {
            return entityliving instanceof EntityFishSchool;
        });
        this.goalSelector.addGoal(0, new EntityFox.g());
        this.goalSelector.addGoal(1, new EntityFox.b());
        this.goalSelector.addGoal(2, new EntityFox.n(2.2D));
        this.goalSelector.addGoal(3, new EntityFox.e(1.0D));
        this.goalSelector.addGoal(4, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 16.0F, 1.6D, 1.4D, (entityliving) -> {
            return EntityFox.AVOID_PLAYERS.test(entityliving) && !this.trusts(entityliving.getUUID()) && !this.isDefending();
        }));
        this.goalSelector.addGoal(4, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 8.0F, 1.6D, 1.4D, (entityliving) -> {
            return !((EntityWolf) entityliving).isTame() && !this.isDefending();
        }));
        this.goalSelector.addGoal(4, new PathfinderGoalAvoidTarget<>(this, EntityPolarBear.class, 8.0F, 1.6D, 1.4D, (entityliving) -> {
            return !this.isDefending();
        }));
        this.goalSelector.addGoal(5, new EntityFox.u());
        this.goalSelector.addGoal(6, new EntityFox.o());
        this.goalSelector.addGoal(6, new EntityFox.s(1.25D));
        this.goalSelector.addGoal(7, new EntityFox.l(1.2000000476837158D, true));
        this.goalSelector.addGoal(7, new EntityFox.t());
        this.goalSelector.addGoal(8, new EntityFox.h(this, 1.25D));
        this.goalSelector.addGoal(9, new EntityFox.q(32, 200));
        this.goalSelector.addGoal(10, new EntityFox.f(1.2000000476837158D, 12, 1));
        this.goalSelector.addGoal(10, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.addGoal(11, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.addGoal(11, new EntityFox.p());
        this.goalSelector.addGoal(12, new EntityFox.j(this, EntityHuman.class, 24.0F));
        this.goalSelector.addGoal(13, new EntityFox.r());
        this.targetSelector.addGoal(3, new EntityFox.a(EntityLiving.class, false, false, (entityliving) -> {
            return EntityFox.TRUSTED_TARGET_SELECTOR.test(entityliving) && !this.trusts(entityliving.getUUID());
        }));
    }

    @Override
    public SoundEffect getEatingSound(ItemStack itemstack) {
        return SoundEffects.FOX_EAT;
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.ticksSinceEaten;
            ItemStack itemstack = this.getItemBySlot(EnumItemSlot.MAINHAND);

            if (this.canEat(itemstack)) {
                if (this.ticksSinceEaten > 600) {
                    ItemStack itemstack1 = itemstack.finishUsingItem(this.level, this);

                    if (!itemstack1.isEmpty()) {
                        this.setItemSlot(EnumItemSlot.MAINHAND, itemstack1);
                    }

                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
                    this.level.broadcastEntityEvent(this, (byte) 45);
                }
            }

            EntityLiving entityliving = this.getTarget();

            if (entityliving == null || !entityliving.isAlive()) {
                this.setIsCrouching(false);
                this.setIsInterested(false);
            }
        }

        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }

        super.aiStep();
        if (this.isDefending() && this.random.nextFloat() < 0.05F) {
            this.playSound(SoundEffects.FOX_AGGRO, 1.0F, 1.0F);
        }

    }

    @Override
    protected boolean isImmobile() {
        return this.isDeadOrDying();
    }

    private boolean canEat(ItemStack itemstack) {
        return itemstack.getItem().isEdible() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.2F) {
            float f = this.random.nextFloat();
            ItemStack itemstack;

            if (f < 0.05F) {
                itemstack = new ItemStack(Items.EMERALD);
            } else if (f < 0.2F) {
                itemstack = new ItemStack(Items.EGG);
            } else if (f < 0.4F) {
                itemstack = this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
            } else if (f < 0.6F) {
                itemstack = new ItemStack(Items.WHEAT);
            } else if (f < 0.8F) {
                itemstack = new ItemStack(Items.LEATHER);
            } else {
                itemstack = new ItemStack(Items.FEATHER);
            }

            this.setItemSlot(EnumItemSlot.MAINHAND, itemstack);
        }

    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 45) {
            ItemStack itemstack = this.getItemBySlot(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3D vec3d = (new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.getXRot() * 0.017453292F).yRot(-this.getYRot() * 0.017453292F);

                    this.level.addParticle(new ParticleParamItem(Particles.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleEntityEvent(b0);
        }

    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.FOLLOW_RANGE, 32.0D).add(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public EntityFox getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        EntityFox entityfox = (EntityFox) EntityTypes.FOX.create(worldserver);

        entityfox.setFoxType(this.random.nextBoolean() ? this.getFoxType() : ((EntityFox) entityageable).getFoxType());
        return entityfox;
    }

    public static boolean checkFoxSpawnRules(EntityTypes<EntityFox> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.FOXES_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        BiomeBase biomebase = worldaccess.getBiome(this.blockPosition());
        EntityFox.Type entityfox_type = EntityFox.Type.byBiome(biomebase);
        boolean flag = false;

        if (groupdataentity instanceof EntityFox.i) {
            entityfox_type = ((EntityFox.i) groupdataentity).type;
            if (((EntityFox.i) groupdataentity).getGroupSize() >= 2) {
                flag = true;
            }
        } else {
            groupdataentity = new EntityFox.i(entityfox_type);
        }

        this.setFoxType(entityfox_type);
        if (flag) {
            this.setAge(-24000);
        }

        if (worldaccess instanceof WorldServer) {
            this.setTargetGoals();
        }

        this.populateDefaultEquipmentSlots(difficultydamagescaler);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    private void setTargetGoals() {
        if (this.getFoxType() == EntityFox.Type.RED) {
            this.targetSelector.addGoal(4, this.landTargetGoal);
            this.targetSelector.addGoal(4, this.turtleEggTargetGoal);
            this.targetSelector.addGoal(6, this.fishTargetGoal);
        } else {
            this.targetSelector.addGoal(4, this.fishTargetGoal);
            this.targetSelector.addGoal(6, this.landTargetGoal);
            this.targetSelector.addGoal(6, this.turtleEggTargetGoal);
        }

    }

    @Override
    protected void usePlayerItem(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (this.isFood(itemstack)) {
            this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
        }

        super.usePlayerItem(entityhuman, enumhand, itemstack);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.85F : 0.4F;
    }

    public EntityFox.Type getFoxType() {
        return EntityFox.Type.byId((Integer) this.entityData.get(EntityFox.DATA_TYPE_ID));
    }

    public void setFoxType(EntityFox.Type entityfox_type) {
        this.entityData.set(EntityFox.DATA_TYPE_ID, entityfox_type.getId());
    }

    List<UUID> getTrustedUUIDs() {
        List<UUID> list = Lists.newArrayList();

        list.add((UUID) ((Optional) this.entityData.get(EntityFox.DATA_TRUSTED_ID_0)).orElse((Object) null));
        list.add((UUID) ((Optional) this.entityData.get(EntityFox.DATA_TRUSTED_ID_1)).orElse((Object) null));
        return list;
    }

    void addTrustedUUID(@Nullable UUID uuid) {
        if (((Optional) this.entityData.get(EntityFox.DATA_TRUSTED_ID_0)).isPresent()) {
            this.entityData.set(EntityFox.DATA_TRUSTED_ID_1, Optional.ofNullable(uuid));
        } else {
            this.entityData.set(EntityFox.DATA_TRUSTED_ID_0, Optional.ofNullable(uuid));
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        List<UUID> list = this.getTrustedUUIDs();
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            UUID uuid = (UUID) iterator.next();

            if (uuid != null) {
                nbttaglist.add(GameProfileSerializer.createUUID(uuid));
            }
        }

        nbttagcompound.put("Trusted", nbttaglist);
        nbttagcompound.putBoolean("Sleeping", this.isSleeping());
        nbttagcompound.putString("Type", this.getFoxType().getName());
        nbttagcompound.putBoolean("Sitting", this.isSitting());
        nbttagcompound.putBoolean("Crouching", this.isCrouching());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Trusted", 11);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.addTrustedUUID(GameProfileSerializer.loadUUID(nbttaglist.get(i)));
        }

        this.setSleeping(nbttagcompound.getBoolean("Sleeping"));
        this.setFoxType(EntityFox.Type.byName(nbttagcompound.getString("Type")));
        this.setSitting(nbttagcompound.getBoolean("Sitting"));
        this.setIsCrouching(nbttagcompound.getBoolean("Crouching"));
        if (this.level instanceof WorldServer) {
            this.setTargetGoals();
        }

    }

    public boolean isSitting() {
        return this.getFlag(1);
    }

    public void setSitting(boolean flag) {
        this.setFlag(1, flag);
    }

    public boolean isFaceplanted() {
        return this.getFlag(64);
    }

    void setFaceplanted(boolean flag) {
        this.setFlag(64, flag);
    }

    boolean isDefending() {
        return this.getFlag(128);
    }

    void setDefending(boolean flag) {
        this.setFlag(128, flag);
    }

    @Override
    public boolean isSleeping() {
        return this.getFlag(32);
    }

    public void setSleeping(boolean flag) {
        this.setFlag(32, flag);
    }

    private void setFlag(int i, boolean flag) {
        if (flag) {
            this.entityData.set(EntityFox.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityFox.DATA_FLAGS_ID) | i));
        } else {
            this.entityData.set(EntityFox.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityFox.DATA_FLAGS_ID) & ~i));
        }

    }

    private boolean getFlag(int i) {
        return ((Byte) this.entityData.get(EntityFox.DATA_FLAGS_ID) & i) != 0;
    }

    @Override
    public boolean canTakeItem(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return !this.getItemBySlot(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.canTakeItem(itemstack);
    }

    @Override
    public boolean canHoldItem(ItemStack itemstack) {
        Item item = itemstack.getItem();
        ItemStack itemstack1 = this.getItemBySlot(EnumItemSlot.MAINHAND);

        return itemstack1.isEmpty() || this.ticksSinceEaten > 0 && item.isEdible() && !itemstack1.getItem().isEdible();
    }

    private void spitOutItem(ItemStack itemstack) {
        if (!itemstack.isEmpty() && !this.level.isClientSide) {
            EntityItem entityitem = new EntityItem(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, itemstack);

            entityitem.setPickUpDelay(40);
            entityitem.setThrower(this.getUUID());
            this.playSound(SoundEffects.FOX_SPIT, 1.0F, 1.0F);
            this.level.addFreshEntity(entityitem);
        }
    }

    private void dropItemStack(ItemStack itemstack) {
        EntityItem entityitem = new EntityItem(this.level, this.getX(), this.getY(), this.getZ(), itemstack);

        this.level.addFreshEntity(entityitem);
    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItem();

        if (this.canHoldItem(itemstack)) {
            int i = itemstack.getCount();

            if (i > 1) {
                this.dropItemStack(itemstack.split(i - 1));
            }

            this.spitOutItem(this.getItemBySlot(EnumItemSlot.MAINHAND));
            this.onItemPickup(entityitem);
            this.setItemSlot(EnumItemSlot.MAINHAND, itemstack.split(1));
            this.handDropChances[EnumItemSlot.MAINHAND.getIndex()] = 2.0F;
            this.take(entityitem, itemstack.getCount());
            entityitem.discard();
            this.ticksSinceEaten = 0;
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isEffectiveAi()) {
            boolean flag = this.isInWater();

            if (flag || this.getTarget() != null || this.level.isThundering()) {
                this.wakeUp();
            }

            if (flag || this.isSleeping()) {
                this.setSitting(false);
            }

            if (this.isFaceplanted() && this.level.random.nextFloat() < 0.2F) {
                BlockPosition blockposition = this.blockPosition();
                IBlockData iblockdata = this.level.getBlockState(blockposition);

                this.level.levelEvent(2001, blockposition, Block.getId(iblockdata));
            }
        }

        this.interestedAngleO = this.interestedAngle;
        if (this.isInterested()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
        } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
        }

        this.crouchAmountO = this.crouchAmount;
        if (this.isCrouching()) {
            this.crouchAmount += 0.2F;
            if (this.crouchAmount > 3.0F) {
                this.crouchAmount = 3.0F;
            }
        } else {
            this.crouchAmount = 0.0F;
        }

    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return itemstack.is((Tag) TagsItem.FOX_FOOD);
    }

    @Override
    protected void onOffspringSpawnedFromEgg(EntityHuman entityhuman, EntityInsentient entityinsentient) {
        ((EntityFox) entityinsentient).addTrustedUUID(entityhuman.getUUID());
    }

    public boolean isPouncing() {
        return this.getFlag(16);
    }

    public void setIsPouncing(boolean flag) {
        this.setFlag(16, flag);
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public boolean isFullyCrouched() {
        return this.crouchAmount == 3.0F;
    }

    public void setIsCrouching(boolean flag) {
        this.setFlag(4, flag);
    }

    @Override
    public boolean isCrouching() {
        return this.getFlag(4);
    }

    public void setIsInterested(boolean flag) {
        this.setFlag(8, flag);
    }

    public boolean isInterested() {
        return this.getFlag(8);
    }

    public float getHeadRollAngle(float f) {
        return MathHelper.lerp(f, this.interestedAngleO, this.interestedAngle) * 0.11F * 3.1415927F;
    }

    public float getCrouchAmount(float f) {
        return MathHelper.lerp(f, this.crouchAmountO, this.crouchAmount);
    }

    @Override
    public void setTarget(@Nullable EntityLiving entityliving) {
        if (this.isDefending() && entityliving == null) {
            this.setDefending(false);
        }

        super.setTarget(entityliving);
    }

    @Override
    protected int calculateFallDamage(float f, float f1) {
        return MathHelper.ceil((f - 5.0F) * f1);
    }

    void wakeUp() {
        this.setSleeping(false);
    }

    void clearStates() {
        this.setIsInterested(false);
        this.setIsCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setDefending(false);
        this.setFaceplanted(false);
    }

    boolean canMove() {
        return !this.isSleeping() && !this.isSitting() && !this.isFaceplanted();
    }

    @Override
    public void playAmbientSound() {
        SoundEffect soundeffect = this.getAmbientSound();

        if (soundeffect == SoundEffects.FOX_SCREECH) {
            this.playSound(soundeffect, 2.0F, this.getVoicePitch());
        } else {
            super.playAmbientSound();
        }

    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        if (this.isSleeping()) {
            return SoundEffects.FOX_SLEEP;
        } else {
            if (!this.level.isDay() && this.random.nextFloat() < 0.1F) {
                List<EntityHuman> list = this.level.getEntitiesOfClass(EntityHuman.class, this.getBoundingBox().inflate(16.0D, 16.0D, 16.0D), IEntitySelector.NO_SPECTATORS);

                if (list.isEmpty()) {
                    return SoundEffects.FOX_SCREECH;
                }
            }

            return SoundEffects.FOX_AMBIENT;
        }
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.FOX_DEATH;
    }

    boolean trusts(UUID uuid) {
        return this.getTrustedUUIDs().contains(uuid);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damagesource) {
        ItemStack itemstack = this.getItemBySlot(EnumItemSlot.MAINHAND);

        if (!itemstack.isEmpty()) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        }

        super.dropAllDeathLoot(damagesource);
    }

    public static boolean isPathClear(EntityFox entityfox, EntityLiving entityliving) {
        double d0 = entityliving.getZ() - entityfox.getZ();
        double d1 = entityliving.getX() - entityfox.getX();
        double d2 = d0 / d1;
        boolean flag = true;

        for (int i = 0; i < 6; ++i) {
            double d3 = d2 == 0.0D ? 0.0D : d0 * (double) ((float) i / 6.0F);
            double d4 = d2 == 0.0D ? d1 * (double) ((float) i / 6.0F) : d3 / d2;

            for (int j = 1; j < 4; ++j) {
                if (!entityfox.level.getBlockState(new BlockPosition(entityfox.getX() + d4, entityfox.getY() + (double) j, entityfox.getZ() + d3)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.55F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }

    public class k extends ControllerLook {

        public k() {
            super(EntityFox.this);
        }

        @Override
        public void tick() {
            if (!EntityFox.this.isSleeping()) {
                super.tick();
            }

        }

        @Override
        protected boolean resetXRotOnTick() {
            return !EntityFox.this.isPouncing() && !EntityFox.this.isCrouching() && !EntityFox.this.isInterested() && !EntityFox.this.isFaceplanted();
        }
    }

    private class m extends ControllerMove {

        public m() {
            super(EntityFox.this);
        }

        @Override
        public void tick() {
            if (EntityFox.this.canMove()) {
                super.tick();
            }

        }
    }

    private class g extends PathfinderGoalFloat {

        public g() {
            super(EntityFox.this);
        }

        @Override
        public void start() {
            super.start();
            EntityFox.this.clearStates();
        }

        @Override
        public boolean canUse() {
            return EntityFox.this.isInWater() && EntityFox.this.getFluidHeight(TagsFluid.WATER) > 0.25D || EntityFox.this.isInLava();
        }
    }

    private class b extends PathfinderGoal {

        int countdown;

        public b() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntityFox.this.isFaceplanted();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && this.countdown > 0;
        }

        @Override
        public void start() {
            this.countdown = this.adjustedTickDelay(40);
        }

        @Override
        public void stop() {
            EntityFox.this.setFaceplanted(false);
        }

        @Override
        public void tick() {
            --this.countdown;
        }
    }

    private class n extends PathfinderGoalPanic {

        public n(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public boolean canUse() {
            return !EntityFox.this.isDefending() && super.canUse();
        }
    }

    private class e extends PathfinderGoalBreed {

        public e(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public void start() {
            ((EntityFox) this.animal).clearStates();
            ((EntityFox) this.partner).clearStates();
            super.start();
        }

        @Override
        protected void breed() {
            WorldServer worldserver = (WorldServer) this.level;
            EntityFox entityfox = (EntityFox) this.animal.getBreedOffspring(worldserver, this.partner);

            if (entityfox != null) {
                EntityPlayer entityplayer = this.animal.getLoveCause();
                EntityPlayer entityplayer1 = this.partner.getLoveCause();
                EntityPlayer entityplayer2 = entityplayer;

                if (entityplayer != null) {
                    entityfox.addTrustedUUID(entityplayer.getUUID());
                } else {
                    entityplayer2 = entityplayer1;
                }

                if (entityplayer1 != null && entityplayer != entityplayer1) {
                    entityfox.addTrustedUUID(entityplayer1.getUUID());
                }

                if (entityplayer2 != null) {
                    entityplayer2.awardStat(StatisticList.ANIMALS_BRED);
                    CriterionTriggers.BRED_ANIMALS.trigger(entityplayer2, this.animal, this.partner, entityfox);
                }

                this.animal.setAge(6000);
                this.partner.setAge(6000);
                this.animal.resetLove();
                this.partner.resetLove();
                entityfox.setAge(-24000);
                entityfox.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
                worldserver.addFreshEntityWithPassengers(entityfox);
                this.level.broadcastEntityEvent(this.animal, (byte) 18);
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    this.level.addFreshEntity(new EntityExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
                }

            }
        }
    }

    private class u extends PathfinderGoal {

        public u() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            if (EntityFox.this.isSleeping()) {
                return false;
            } else {
                EntityLiving entityliving = EntityFox.this.getTarget();

                return entityliving != null && entityliving.isAlive() && EntityFox.STALKABLE_PREY.test(entityliving) && EntityFox.this.distanceToSqr((Entity) entityliving) > 36.0D && !EntityFox.this.isCrouching() && !EntityFox.this.isInterested() && !EntityFox.this.jumping;
            }
        }

        @Override
        public void start() {
            EntityFox.this.setSitting(false);
            EntityFox.this.setFaceplanted(false);
        }

        @Override
        public void stop() {
            EntityLiving entityliving = EntityFox.this.getTarget();

            if (entityliving != null && EntityFox.isPathClear(EntityFox.this, entityliving)) {
                EntityFox.this.setIsInterested(true);
                EntityFox.this.setIsCrouching(true);
                EntityFox.this.getNavigation().stop();
                EntityFox.this.getLookControl().setLookAt(entityliving, (float) EntityFox.this.getMaxHeadYRot(), (float) EntityFox.this.getMaxHeadXRot());
            } else {
                EntityFox.this.setIsInterested(false);
                EntityFox.this.setIsCrouching(false);
            }

        }

        @Override
        public void tick() {
            EntityLiving entityliving = EntityFox.this.getTarget();

            if (entityliving != null) {
                EntityFox.this.getLookControl().setLookAt(entityliving, (float) EntityFox.this.getMaxHeadYRot(), (float) EntityFox.this.getMaxHeadXRot());
                if (EntityFox.this.distanceToSqr((Entity) entityliving) <= 36.0D) {
                    EntityFox.this.setIsInterested(true);
                    EntityFox.this.setIsCrouching(true);
                    EntityFox.this.getNavigation().stop();
                } else {
                    EntityFox.this.getNavigation().moveTo((Entity) entityliving, 1.5D);
                }

            }
        }
    }

    public class o extends PathfinderGoalWaterJumpAbstract {

        public o() {}

        @Override
        public boolean canUse() {
            if (!EntityFox.this.isFullyCrouched()) {
                return false;
            } else {
                EntityLiving entityliving = EntityFox.this.getTarget();

                if (entityliving != null && entityliving.isAlive()) {
                    if (entityliving.getMotionDirection() != entityliving.getDirection()) {
                        return false;
                    } else {
                        boolean flag = EntityFox.isPathClear(EntityFox.this, entityliving);

                        if (!flag) {
                            EntityFox.this.getNavigation().createPath((Entity) entityliving, 0);
                            EntityFox.this.setIsCrouching(false);
                            EntityFox.this.setIsInterested(false);
                        }

                        return flag;
                    }
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            EntityLiving entityliving = EntityFox.this.getTarget();

            if (entityliving != null && entityliving.isAlive()) {
                double d0 = EntityFox.this.getDeltaMovement().y;

                return (d0 * d0 >= 0.05000000074505806D || Math.abs(EntityFox.this.getXRot()) >= 15.0F || !EntityFox.this.onGround) && !EntityFox.this.isFaceplanted();
            } else {
                return false;
            }
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void start() {
            EntityFox.this.setJumping(true);
            EntityFox.this.setIsPouncing(true);
            EntityFox.this.setIsInterested(false);
            EntityLiving entityliving = EntityFox.this.getTarget();

            if (entityliving != null) {
                EntityFox.this.getLookControl().setLookAt(entityliving, 60.0F, 30.0F);
                Vec3D vec3d = (new Vec3D(entityliving.getX() - EntityFox.this.getX(), entityliving.getY() - EntityFox.this.getY(), entityliving.getZ() - EntityFox.this.getZ())).normalize();

                EntityFox.this.setDeltaMovement(EntityFox.this.getDeltaMovement().add(vec3d.x * 0.8D, 0.9D, vec3d.z * 0.8D));
            }

            EntityFox.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            EntityFox.this.setIsCrouching(false);
            EntityFox.this.crouchAmount = 0.0F;
            EntityFox.this.crouchAmountO = 0.0F;
            EntityFox.this.setIsInterested(false);
            EntityFox.this.setIsPouncing(false);
        }

        @Override
        public void tick() {
            EntityLiving entityliving = EntityFox.this.getTarget();

            if (entityliving != null) {
                EntityFox.this.getLookControl().setLookAt(entityliving, 60.0F, 30.0F);
            }

            if (!EntityFox.this.isFaceplanted()) {
                Vec3D vec3d = EntityFox.this.getDeltaMovement();

                if (vec3d.y * vec3d.y < 0.029999999329447746D && EntityFox.this.getXRot() != 0.0F) {
                    EntityFox.this.setXRot(MathHelper.rotlerp(EntityFox.this.getXRot(), 0.0F, 0.2F));
                } else {
                    double d0 = vec3d.horizontalDistance();
                    double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.length()) * 57.2957763671875D;

                    EntityFox.this.setXRot((float) d1);
                }
            }

            if (entityliving != null && EntityFox.this.distanceTo(entityliving) <= 2.0F) {
                EntityFox.this.doHurtTarget(entityliving);
            } else if (EntityFox.this.getXRot() > 0.0F && EntityFox.this.onGround && (float) EntityFox.this.getDeltaMovement().y != 0.0F && EntityFox.this.level.getBlockState(EntityFox.this.blockPosition()).is(Blocks.SNOW)) {
                EntityFox.this.setXRot(60.0F);
                EntityFox.this.setTarget((EntityLiving) null);
                EntityFox.this.setFaceplanted(true);
            }

        }
    }

    private class s extends PathfinderGoalFleeSun {

        private int interval = reducedTickDelay(100);

        public s(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public boolean canUse() {
            if (!EntityFox.this.isSleeping() && this.mob.getTarget() == null) {
                if (EntityFox.this.level.isThundering()) {
                    return true;
                } else if (this.interval > 0) {
                    --this.interval;
                    return false;
                } else {
                    this.interval = 100;
                    BlockPosition blockposition = this.mob.blockPosition();

                    return EntityFox.this.level.isDay() && EntityFox.this.level.canSeeSky(blockposition) && !((WorldServer) EntityFox.this.level).isVillage(blockposition) && this.setWantedPos();
                }
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            EntityFox.this.clearStates();
            super.start();
        }
    }

    private class l extends PathfinderGoalMeleeAttack {

        public l(double d0, boolean flag) {
            super(EntityFox.this, d0, flag);
        }

        @Override
        protected void checkAndPerformAttack(EntityLiving entityliving, double d0) {
            double d1 = this.getAttackReachSqr(entityliving);

            if (d0 <= d1 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(entityliving);
                EntityFox.this.playSound(SoundEffects.FOX_BITE, 1.0F, 1.0F);
            }

        }

        @Override
        public void start() {
            EntityFox.this.setIsInterested(false);
            super.start();
        }

        @Override
        public boolean canUse() {
            return !EntityFox.this.isSitting() && !EntityFox.this.isSleeping() && !EntityFox.this.isCrouching() && !EntityFox.this.isFaceplanted() && super.canUse();
        }
    }

    private class t extends EntityFox.d {

        private static final int WAIT_TIME_BEFORE_SLEEP = reducedTickDelay(140);
        private int countdown;

        public t() {
            super();
            this.countdown = EntityFox.this.random.nextInt(EntityFox.t.WAIT_TIME_BEFORE_SLEEP);
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
        }

        @Override
        public boolean canUse() {
            return EntityFox.this.xxa == 0.0F && EntityFox.this.yya == 0.0F && EntityFox.this.zza == 0.0F ? this.canSleep() || EntityFox.this.isSleeping() : false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canSleep();
        }

        private boolean canSleep() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            } else {
                return EntityFox.this.level.isDay() && this.hasShelter() && !this.alertable() && !EntityFox.this.isInPowderSnow;
            }
        }

        @Override
        public void stop() {
            this.countdown = EntityFox.this.random.nextInt(EntityFox.t.WAIT_TIME_BEFORE_SLEEP);
            EntityFox.this.clearStates();
        }

        @Override
        public void start() {
            EntityFox.this.setSitting(false);
            EntityFox.this.setIsCrouching(false);
            EntityFox.this.setIsInterested(false);
            EntityFox.this.setJumping(false);
            EntityFox.this.setSleeping(true);
            EntityFox.this.getNavigation().stop();
            EntityFox.this.getMoveControl().setWantedPosition(EntityFox.this.getX(), EntityFox.this.getY(), EntityFox.this.getZ(), 0.0D);
        }
    }

    private class h extends PathfinderGoalFollowParent {

        private final EntityFox fox;

        public h(EntityFox entityfox, double d0) {
            super(entityfox, d0);
            this.fox = entityfox;
        }

        @Override
        public boolean canUse() {
            return !this.fox.isDefending() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.fox.isDefending() && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.fox.clearStates();
            super.start();
        }
    }

    private class q extends PathfinderGoalNearestVillage {

        public q(int i, int j) {
            super(EntityFox.this, j);
        }

        @Override
        public void start() {
            EntityFox.this.clearStates();
            super.start();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.canFoxMove();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.canFoxMove();
        }

        private boolean canFoxMove() {
            return !EntityFox.this.isSleeping() && !EntityFox.this.isSitting() && !EntityFox.this.isDefending() && EntityFox.this.getTarget() == null;
        }
    }

    public class f extends PathfinderGoalGotoTarget {

        private static final int WAIT_TICKS = 40;
        protected int ticksWaited;

        public f(double d0, int i, int j) {
            super(EntityFox.this, d0, i, j);
        }

        @Override
        public double acceptedDistance() {
            return 2.0D;
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 100 == 0;
        }

        @Override
        protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
            IBlockData iblockdata = iworldreader.getBlockState(blockposition);

            return iblockdata.is(Blocks.SWEET_BERRY_BUSH) && (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) >= 2 || CaveVines.hasGlowBerries(iblockdata);
        }

        @Override
        public void tick() {
            if (this.isReachedTarget()) {
                if (this.ticksWaited >= 40) {
                    this.onReachedTarget();
                } else {
                    ++this.ticksWaited;
                }
            } else if (!this.isReachedTarget() && EntityFox.this.random.nextFloat() < 0.05F) {
                EntityFox.this.playSound(SoundEffects.FOX_SNIFF, 1.0F, 1.0F);
            }

            super.tick();
        }

        protected void onReachedTarget() {
            if (EntityFox.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                IBlockData iblockdata = EntityFox.this.level.getBlockState(this.blockPos);

                if (iblockdata.is(Blocks.SWEET_BERRY_BUSH)) {
                    this.pickSweetBerries(iblockdata);
                } else if (CaveVines.hasGlowBerries(iblockdata)) {
                    this.pickGlowBerry(iblockdata);
                }

            }
        }

        private void pickGlowBerry(IBlockData iblockdata) {
            CaveVines.use(iblockdata, EntityFox.this.level, this.blockPos);
        }

        private void pickSweetBerries(IBlockData iblockdata) {
            int i = (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE);

            iblockdata.setValue(BlockSweetBerryBush.AGE, 1);
            int j = 1 + EntityFox.this.level.random.nextInt(2) + (i == 3 ? 1 : 0);
            ItemStack itemstack = EntityFox.this.getItemBySlot(EnumItemSlot.MAINHAND);

            if (itemstack.isEmpty()) {
                EntityFox.this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                --j;
            }

            if (j > 0) {
                Block.popResource(EntityFox.this.level, this.blockPos, new ItemStack(Items.SWEET_BERRIES, j));
            }

            EntityFox.this.playSound(SoundEffects.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
            EntityFox.this.level.setBlock(this.blockPos, (IBlockData) iblockdata.setValue(BlockSweetBerryBush.AGE, 1), 2);
        }

        @Override
        public boolean canUse() {
            return !EntityFox.this.isSleeping() && super.canUse();
        }

        @Override
        public void start() {
            this.ticksWaited = 0;
            EntityFox.this.setSitting(false);
            super.start();
        }
    }

    private class p extends PathfinderGoal {

        public p() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!EntityFox.this.getItemBySlot(EnumItemSlot.MAINHAND).isEmpty()) {
                return false;
            } else if (EntityFox.this.getTarget() == null && EntityFox.this.getLastHurtByMob() == null) {
                if (!EntityFox.this.canMove()) {
                    return false;
                } else if (EntityFox.this.getRandom().nextInt(reducedTickDelay(10)) != 0) {
                    return false;
                } else {
                    List<EntityItem> list = EntityFox.this.level.getEntitiesOfClass(EntityItem.class, EntityFox.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), EntityFox.ALLOWED_ITEMS);

                    return !list.isEmpty() && EntityFox.this.getItemBySlot(EnumItemSlot.MAINHAND).isEmpty();
                }
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            List<EntityItem> list = EntityFox.this.level.getEntitiesOfClass(EntityItem.class, EntityFox.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), EntityFox.ALLOWED_ITEMS);
            ItemStack itemstack = EntityFox.this.getItemBySlot(EnumItemSlot.MAINHAND);

            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityFox.this.getNavigation().moveTo((Entity) list.get(0), 1.2000000476837158D);
            }

        }

        @Override
        public void start() {
            List<EntityItem> list = EntityFox.this.level.getEntitiesOfClass(EntityItem.class, EntityFox.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), EntityFox.ALLOWED_ITEMS);

            if (!list.isEmpty()) {
                EntityFox.this.getNavigation().moveTo((Entity) list.get(0), 1.2000000476837158D);
            }

        }
    }

    private class j extends PathfinderGoalLookAtPlayer {

        public j(EntityInsentient entityinsentient, Class oclass, float f) {
            super(entityinsentient, oclass, f);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !EntityFox.this.isFaceplanted() && !EntityFox.this.isInterested();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !EntityFox.this.isFaceplanted() && !EntityFox.this.isInterested();
        }
    }

    private class r extends EntityFox.d {

        private double relX;
        private double relZ;
        private int lookTime;
        private int looksRemaining;

        public r() {
            super();
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityFox.this.getLastHurtByMob() == null && EntityFox.this.getRandom().nextFloat() < 0.02F && !EntityFox.this.isSleeping() && EntityFox.this.getTarget() == null && EntityFox.this.getNavigation().isDone() && !this.alertable() && !EntityFox.this.isPouncing() && !EntityFox.this.isCrouching();
        }

        @Override
        public boolean canContinueToUse() {
            return this.looksRemaining > 0;
        }

        @Override
        public void start() {
            this.resetLook();
            this.looksRemaining = 2 + EntityFox.this.getRandom().nextInt(3);
            EntityFox.this.setSitting(true);
            EntityFox.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            EntityFox.this.setSitting(false);
        }

        @Override
        public void tick() {
            --this.lookTime;
            if (this.lookTime <= 0) {
                --this.looksRemaining;
                this.resetLook();
            }

            EntityFox.this.getLookControl().setLookAt(EntityFox.this.getX() + this.relX, EntityFox.this.getEyeY(), EntityFox.this.getZ() + this.relZ, (float) EntityFox.this.getMaxHeadYRot(), (float) EntityFox.this.getMaxHeadXRot());
        }

        private void resetLook() {
            double d0 = 6.283185307179586D * EntityFox.this.getRandom().nextDouble();

            this.relX = Math.cos(d0);
            this.relZ = Math.sin(d0);
            this.lookTime = this.adjustedTickDelay(80 + EntityFox.this.getRandom().nextInt(20));
        }
    }

    private class a extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        @Nullable
        private EntityLiving trustedLastHurtBy;
        @Nullable
        private EntityLiving trustedLastHurt;
        private int timestamp;

        public a(Class oclass, boolean flag, boolean flag1, @Nullable Predicate predicate) {
            super(EntityFox.this, oclass, 10, flag, flag1, predicate);
        }

        @Override
        public boolean canUse() {
            if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
                return false;
            } else {
                Iterator iterator = EntityFox.this.getTrustedUUIDs().iterator();

                while (iterator.hasNext()) {
                    UUID uuid = (UUID) iterator.next();

                    if (uuid != null && EntityFox.this.level instanceof WorldServer) {
                        Entity entity = ((WorldServer) EntityFox.this.level).getEntity(uuid);

                        if (entity instanceof EntityLiving) {
                            EntityLiving entityliving = (EntityLiving) entity;

                            this.trustedLastHurt = entityliving;
                            this.trustedLastHurtBy = entityliving.getLastHurtByMob();
                            int i = entityliving.getLastHurtByMobTimestamp();

                            return i != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public void start() {
            this.setTarget(this.trustedLastHurtBy);
            this.target = this.trustedLastHurtBy;
            if (this.trustedLastHurt != null) {
                this.timestamp = this.trustedLastHurt.getLastHurtByMobTimestamp();
            }

            EntityFox.this.playSound(SoundEffects.FOX_AGGRO, 1.0F, 1.0F);
            EntityFox.this.setDefending(true);
            EntityFox.this.wakeUp();
            super.start();
        }
    }

    public static enum Type {

        RED(0, "red"), SNOW(1, "snow");

        private static final EntityFox.Type[] BY_ID = (EntityFox.Type[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EntityFox.Type::getId)).toArray((i) -> {
            return new EntityFox.Type[i];
        });
        private static final Map<String, EntityFox.Type> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(EntityFox.Type::getName, (entityfox_type) -> {
            return entityfox_type;
        }));
        private final int id;
        private final String name;

        private Type(int i, String s) {
            this.id = i;
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static EntityFox.Type byName(String s) {
            return (EntityFox.Type) EntityFox.Type.BY_NAME.getOrDefault(s, EntityFox.Type.RED);
        }

        public static EntityFox.Type byId(int i) {
            if (i < 0 || i > EntityFox.Type.BY_ID.length) {
                i = 0;
            }

            return EntityFox.Type.BY_ID[i];
        }

        public static EntityFox.Type byBiome(BiomeBase biomebase) {
            return biomebase.getPrecipitation() == BiomeBase.Precipitation.SNOW ? EntityFox.Type.SNOW : EntityFox.Type.RED;
        }
    }

    public static class i extends EntityAgeable.a {

        public final EntityFox.Type type;

        public i(EntityFox.Type entityfox_type) {
            super(false);
            this.type = entityfox_type;
        }
    }

    private abstract class d extends PathfinderGoal {

        private final PathfinderTargetCondition alertableTargeting = PathfinderTargetCondition.forCombat().range(12.0D).ignoreLineOfSight().selector(EntityFox.this.new c());

        d() {}

        protected boolean hasShelter() {
            BlockPosition blockposition = new BlockPosition(EntityFox.this.getX(), EntityFox.this.getBoundingBox().maxY, EntityFox.this.getZ());

            return !EntityFox.this.level.canSeeSky(blockposition) && EntityFox.this.getWalkTargetValue(blockposition) >= 0.0F;
        }

        protected boolean alertable() {
            return !EntityFox.this.level.getNearbyEntities(EntityLiving.class, this.alertableTargeting, EntityFox.this, EntityFox.this.getBoundingBox().inflate(12.0D, 6.0D, 12.0D)).isEmpty();
        }
    }

    public class c implements Predicate<EntityLiving> {

        public c() {}

        public boolean test(EntityLiving entityliving) {
            return entityliving instanceof EntityFox ? false : (!(entityliving instanceof EntityChicken) && !(entityliving instanceof EntityRabbit) && !(entityliving instanceof EntityMonster) ? (entityliving instanceof EntityTameableAnimal ? !((EntityTameableAnimal) entityliving).isTame() : (entityliving instanceof EntityHuman && (entityliving.isSpectator() || ((EntityHuman) entityliving).isCreative()) ? false : (EntityFox.this.trusts(entityliving.getUUID()) ? false : !entityliving.isSleeping() && !entityliving.isDiscrete()))) : true);
        }
    }
}
