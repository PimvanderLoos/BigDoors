package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreakDoor;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveThroughVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRemoveBlock;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.util.PathfinderGoalUtil;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityZombie extends EntityMonster {

    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(EntityZombie.SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final DataWatcherObject<Boolean> DATA_BABY_ID = DataWatcher.defineId(EntityZombie.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_SPECIAL_TYPE_ID = DataWatcher.defineId(EntityZombie.class, DataWatcherRegistry.INT);
    public static final DataWatcherObject<Boolean> DATA_DROWNED_CONVERSION_ID = DataWatcher.defineId(EntityZombie.class, DataWatcherRegistry.BOOLEAN);
    public static final float ZOMBIE_LEADER_CHANCE = 0.05F;
    public static final int REINFORCEMENT_ATTEMPTS = 50;
    public static final int REINFORCEMENT_RANGE_MAX = 40;
    public static final int REINFORCEMENT_RANGE_MIN = 7;
    private static final float BREAK_DOOR_CHANCE = 0.1F;
    private static final Predicate<EnumDifficulty> DOOR_BREAKING_PREDICATE = (enumdifficulty) -> {
        return enumdifficulty == EnumDifficulty.HARD;
    };
    private final PathfinderGoalBreakDoor breakDoorGoal;
    private boolean canBreakDoors;
    private int inWaterTime;
    public int conversionTime;

    public EntityZombie(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.breakDoorGoal = new PathfinderGoalBreakDoor(this, EntityZombie.DOOR_BREAKING_PREDICATE);
    }

    public EntityZombie(World world) {
        this(EntityTypes.ZOMBIE, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new EntityZombie.a(this, 1.0D, 3));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.addGoal(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).setAlertOthers(EntityPigZombie.class));
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.addGoal(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.FOLLOW_RANGE, 35.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).add(GenericAttributes.ATTACK_DAMAGE, 3.0D).add(GenericAttributes.ARMOR, 2.0D).add(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(EntityZombie.DATA_BABY_ID, false);
        this.getEntityData().define(EntityZombie.DATA_SPECIAL_TYPE_ID, 0);
        this.getEntityData().define(EntityZombie.DATA_DROWNED_CONVERSION_ID, false);
    }

    public boolean isUnderWaterConverting() {
        return (Boolean) this.getEntityData().get(EntityZombie.DATA_DROWNED_CONVERSION_ID);
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean flag) {
        if (this.supportsBreakDoorGoal() && PathfinderGoalUtil.hasGroundPathNavigation(this)) {
            if (this.canBreakDoors != flag) {
                this.canBreakDoors = flag;
                ((Navigation) this.getNavigation()).setCanOpenDoors(flag);
                if (flag) {
                    this.goalSelector.addGoal(1, this.breakDoorGoal);
                } else {
                    this.goalSelector.removeGoal(this.breakDoorGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.removeGoal(this.breakDoorGoal);
            this.canBreakDoors = false;
        }

    }

    protected boolean supportsBreakDoorGoal() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getEntityData().get(EntityZombie.DATA_BABY_ID);
    }

    @Override
    protected int getExperienceReward(EntityHuman entityhuman) {
        if (this.isBaby()) {
            this.xpReward = (int) ((float) this.xpReward * 2.5F);
        }

        return super.getExperienceReward(entityhuman);
    }

    @Override
    public void setBaby(boolean flag) {
        this.getEntityData().set(EntityZombie.DATA_BABY_ID, flag);
        if (this.level != null && !this.level.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttribute(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityZombie.SPEED_MODIFIER_BABY);
            if (flag) {
                attributemodifiable.addTransientModifier(EntityZombie.SPEED_MODIFIER_BABY);
            }
        }

    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityZombie.DATA_BABY_ID.equals(datawatcherobject)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    protected boolean convertsInWater() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
            if (this.isUnderWaterConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.doUnderWaterConversion();
                }
            } else if (this.convertsInWater()) {
                if (this.isEyeInFluid(TagsFluid.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.startUnderWaterConversion(300);
                    }
                } else {
                    this.inWaterTime = -1;
                }
            }
        }

        super.tick();
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = this.isSunSensitive() && this.isSunBurnTick();

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
        }

        super.aiStep();
    }

    public void startUnderWaterConversion(int i) {
        this.conversionTime = i;
        this.getEntityData().set(EntityZombie.DATA_DROWNED_CONVERSION_ID, true);
    }

    protected void doUnderWaterConversion() {
        this.convertToZombieType(EntityTypes.DROWNED);
        if (!this.isSilent()) {
            this.level.levelEvent((EntityHuman) null, 1040, this.blockPosition(), 0);
        }

    }

    protected void convertToZombieType(EntityTypes<? extends EntityZombie> entitytypes) {
        EntityZombie entityzombie = (EntityZombie) this.convertTo(entitytypes, true);

        if (entityzombie != null) {
            entityzombie.handleAttributes(entityzombie.level.getCurrentDifficultyAt(entityzombie.blockPosition()).getSpecialMultiplier());
            entityzombie.setCanBreakDoors(entityzombie.supportsBreakDoorGoal() && this.canBreakDoors());
        }

    }

    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (!super.hurt(damagesource, f)) {
            return false;
        } else if (!(this.level instanceof WorldServer)) {
            return false;
        } else {
            WorldServer worldserver = (WorldServer) this.level;
            EntityLiving entityliving = this.getTarget();

            if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
                entityliving = (EntityLiving) damagesource.getEntity();
            }

            if (entityliving != null && this.level.getDifficulty() == EnumDifficulty.HARD && (double) this.random.nextFloat() < this.getAttributeValue(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                int i = MathHelper.floor(this.getX());
                int j = MathHelper.floor(this.getY());
                int k = MathHelper.floor(this.getZ());
                EntityZombie entityzombie = new EntityZombie(this.level);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPosition blockposition = new BlockPosition(i1, j1, k1);
                    EntityTypes<?> entitytypes = entityzombie.getType();
                    EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.getPlacementType(entitytypes);

                    if (SpawnerCreature.isSpawnPositionOk(entitypositiontypes_surface, this.level, blockposition, entitytypes) && EntityPositionTypes.checkSpawnRules(entitytypes, worldserver, EnumMobSpawn.REINFORCEMENT, blockposition, this.level.random)) {
                        entityzombie.setPos((double) i1, (double) j1, (double) k1);
                        if (!this.level.hasNearbyAlivePlayer((double) i1, (double) j1, (double) k1, 7.0D) && this.level.isUnobstructed(entityzombie) && this.level.noCollision((Entity) entityzombie) && !this.level.containsAnyLiquid(entityzombie.getBoundingBox())) {
                            entityzombie.setTarget(entityliving);
                            entityzombie.finalizeSpawn(worldserver, this.level.getCurrentDifficultyAt(entityzombie.blockPosition()), EnumMobSpawn.REINFORCEMENT, (GroupDataEntity) null, (NBTTagCompound) null);
                            worldserver.addFreshEntityWithPassengers(entityzombie);
                            this.getAttribute(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            entityzombie.getAttribute(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);

        if (flag) {
            float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();

            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                entity.setSecondsOnFire(2 * (int) f);
            }
        }

        return flag;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ZOMBIE_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.ZOMBIE_DEATH;
    }

    protected SoundEffect getStepSound() {
        return SoundEffects.ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyDamageScaler difficultydamagescaler) {
        super.populateDefaultEquipmentSlots(difficultydamagescaler);
        if (this.random.nextFloat() < (this.level.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);

            if (i == 0) {
                this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("IsBaby", this.isBaby());
        nbttagcompound.putBoolean("CanBreakDoors", this.canBreakDoors());
        nbttagcompound.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
        nbttagcompound.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setBaby(nbttagcompound.getBoolean("IsBaby"));
        this.setCanBreakDoors(nbttagcompound.getBoolean("CanBreakDoors"));
        this.inWaterTime = nbttagcompound.getInt("InWaterTime");
        if (nbttagcompound.contains("DrownedConversionTime", 99) && nbttagcompound.getInt("DrownedConversionTime") > -1) {
            this.startUnderWaterConversion(nbttagcompound.getInt("DrownedConversionTime"));
        }

    }

    @Override
    public void killed(WorldServer worldserver, EntityLiving entityliving) {
        super.killed(worldserver, entityliving);
        if ((worldserver.getDifficulty() == EnumDifficulty.NORMAL || worldserver.getDifficulty() == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
            if (worldserver.getDifficulty() != EnumDifficulty.HARD && this.random.nextBoolean()) {
                return;
            }

            EntityVillager entityvillager = (EntityVillager) entityliving;
            EntityZombieVillager entityzombievillager = (EntityZombieVillager) entityvillager.convertTo(EntityTypes.ZOMBIE_VILLAGER, false);

            entityzombievillager.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entityzombievillager.blockPosition()), EnumMobSpawn.CONVERSION, new EntityZombie.GroupDataZombie(false, true), (NBTTagCompound) null);
            entityzombievillager.setVillagerData(entityvillager.getVillagerData());
            entityzombievillager.setGossips((NBTBase) entityvillager.getGossips().store(DynamicOpsNBT.INSTANCE).getValue());
            entityzombievillager.setTradeOffers(entityvillager.getOffers().createTag());
            entityzombievillager.setVillagerXp(entityvillager.getVillagerXp());
            if (!this.isSilent()) {
                worldserver.levelEvent((EntityHuman) null, 1026, this.blockPosition(), 0);
            }
        }

    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public boolean canHoldItem(ItemStack itemstack) {
        return itemstack.is(Items.EGG) && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(itemstack);
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return itemstack.is(Items.GLOW_INK_SAC) ? false : super.wantsToPickUp(itemstack);
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        float f = difficultydamagescaler.getSpecialMultiplier();

        this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
        if (object == null) {
            object = new EntityZombie.GroupDataZombie(getSpawnAsBabyOdds(worldaccess.getRandom()), true);
        }

        if (object instanceof EntityZombie.GroupDataZombie) {
            EntityZombie.GroupDataZombie entityzombie_groupdatazombie = (EntityZombie.GroupDataZombie) object;

            if (entityzombie_groupdatazombie.isBaby) {
                this.setBaby(true);
                if (entityzombie_groupdatazombie.canSpawnJockey) {
                    if ((double) worldaccess.getRandom().nextFloat() < 0.05D) {
                        List<EntityChicken> list = worldaccess.getEntitiesOfClass(EntityChicken.class, this.getBoundingBox().inflate(5.0D, 3.0D, 5.0D), IEntitySelector.ENTITY_NOT_BEING_RIDDEN);

                        if (!list.isEmpty()) {
                            EntityChicken entitychicken = (EntityChicken) list.get(0);

                            entitychicken.setChickenJockey(true);
                            this.startRiding(entitychicken);
                        }
                    } else if ((double) worldaccess.getRandom().nextFloat() < 0.05D) {
                        EntityChicken entitychicken1 = (EntityChicken) EntityTypes.CHICKEN.create(this.level);

                        entitychicken1.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        entitychicken1.finalizeSpawn(worldaccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, (GroupDataEntity) null, (NBTTagCompound) null);
                        entitychicken1.setChickenJockey(true);
                        this.startRiding(entitychicken1);
                        worldaccess.addFreshEntity(entitychicken1);
                    }
                }
            }

            this.setCanBreakDoors(this.supportsBreakDoorGoal() && this.random.nextFloat() < f * 0.1F);
            this.populateDefaultEquipmentSlots(difficultydamagescaler);
            this.populateDefaultEquipmentEnchantments(difficultydamagescaler);
        }

        if (this.getItemBySlot(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setItemSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EnumItemSlot.HEAD.getIndex()] = 0.0F;
            }
        }

        this.handleAttributes(f);
        return (GroupDataEntity) object;
    }

    public static boolean getSpawnAsBabyOdds(Random random) {
        return random.nextFloat() < 0.05F;
    }

    protected void handleAttributes(float f) {
        this.randomizeReinforcementsChance();
        this.getAttribute(GenericAttributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double) f;

        if (d0 > 1.0D) {
            this.getAttribute(GenericAttributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.random.nextFloat() < f * 0.05F) {
            this.getAttribute(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttribute(GenericAttributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setCanBreakDoors(this.supportsBreakDoorGoal());
        }

    }

    protected void randomizeReinforcementsChance() {
        this.getAttribute(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.10000000149011612D);
    }

    @Override
    public double getMyRidingOffset() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropCustomDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canDropMobsSkull()) {
                ItemStack itemstack = this.getSkull();

                if (!itemstack.isEmpty()) {
                    entitycreeper.increaseDroppedSkulls();
                    this.spawnAtLocation(itemstack);
                }
            }
        }

    }

    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    private class a extends PathfinderGoalRemoveBlock {

        a(EntityCreature entitycreature, double d0, int i) {
            super(Blocks.TURTLE_EGG, entitycreature, d0, i);
        }

        @Override
        public void playDestroyProgressSound(GeneratorAccess generatoraccess, BlockPosition blockposition) {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.random.nextFloat() * 0.2F);
        }

        @Override
        public void playBreakSound(World world, BlockPosition blockposition) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        @Override
        public double acceptedDistance() {
            return 1.14D;
        }
    }

    public static class GroupDataZombie implements GroupDataEntity {

        public final boolean isBaby;
        public final boolean canSpawnJockey;

        public GroupDataZombie(boolean flag, boolean flag1) {
            this.isBaby = flag;
            this.canSpawnJockey = flag1;
        }
    }
}
