package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityStrider;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.food.FoodMetaData;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerPlayer;
import net.minecraft.world.inventory.InventoryEnderChest;
import net.minecraft.world.item.ItemCooldown;
import net.minecraft.world.item.ItemElytra;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemSword;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.CommandBlockListenerAbstract;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.BlockRespawnAnchor;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.slf4j.Logger;

public abstract class EntityHuman extends EntityLiving {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MAX_NAME_LENGTH = 16;
    public static final int MAX_HEALTH = 20;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;
    public static final int ENDER_SLOT_OFFSET = 200;
    public static final float CROUCH_BB_HEIGHT = 1.5F;
    public static final float SWIMMING_BB_WIDTH = 0.6F;
    public static final float SWIMMING_BB_HEIGHT = 0.6F;
    public static final float DEFAULT_EYE_HEIGHT = 1.62F;
    public static final EntitySize STANDING_DIMENSIONS = EntitySize.scalable(0.6F, 1.8F);
    private static final Map<EntityPose, EntitySize> POSES = ImmutableMap.builder().put(EntityPose.STANDING, EntityHuman.STANDING_DIMENSIONS).put(EntityPose.SLEEPING, EntityHuman.SLEEPING_DIMENSIONS).put(EntityPose.FALL_FLYING, EntitySize.scalable(0.6F, 0.6F)).put(EntityPose.SWIMMING, EntitySize.scalable(0.6F, 0.6F)).put(EntityPose.SPIN_ATTACK, EntitySize.scalable(0.6F, 0.6F)).put(EntityPose.CROUCHING, EntitySize.scalable(0.6F, 1.5F)).put(EntityPose.DYING, EntitySize.fixed(0.2F, 0.2F)).build();
    private static final int FLY_ACHIEVEMENT_SPEED = 25;
    private static final DataWatcherObject<Float> DATA_PLAYER_ABSORPTION_ID = DataWatcher.defineId(EntityHuman.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_SCORE_ID = DataWatcher.defineId(EntityHuman.class, DataWatcherRegistry.INT);
    protected static final DataWatcherObject<Byte> DATA_PLAYER_MODE_CUSTOMISATION = DataWatcher.defineId(EntityHuman.class, DataWatcherRegistry.BYTE);
    protected static final DataWatcherObject<Byte> DATA_PLAYER_MAIN_HAND = DataWatcher.defineId(EntityHuman.class, DataWatcherRegistry.BYTE);
    protected static final DataWatcherObject<NBTTagCompound> DATA_SHOULDER_LEFT = DataWatcher.defineId(EntityHuman.class, DataWatcherRegistry.COMPOUND_TAG);
    protected static final DataWatcherObject<NBTTagCompound> DATA_SHOULDER_RIGHT = DataWatcher.defineId(EntityHuman.class, DataWatcherRegistry.COMPOUND_TAG);
    private long timeEntitySatOnShoulder;
    private final PlayerInventory inventory = new PlayerInventory(this);
    protected InventoryEnderChest enderChestInventory = new InventoryEnderChest();
    public final ContainerPlayer inventoryMenu;
    public Container containerMenu;
    protected FoodMetaData foodData = new FoodMetaData();
    protected int jumpTriggerTime;
    public float oBob;
    public float bob;
    public int takeXpDelay;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    public int sleepCounter;
    protected boolean wasUnderwater;
    private final PlayerAbilities abilities = new PlayerAbilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    public int enchantmentSeed;
    protected final float defaultFlySpeed = 0.02F;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand;
    private final ItemCooldown cooldowns;
    private Optional<GlobalPos> lastDeathLocation;
    @Nullable
    public EntityFishingHook fishing;
    protected float hurtDir;

    public EntityHuman(World world, BlockPosition blockposition, float f, GameProfile gameprofile) {
        super(EntityTypes.PLAYER, world);
        this.lastItemInMainHand = ItemStack.EMPTY;
        this.cooldowns = this.createItemCooldowns();
        this.lastDeathLocation = Optional.empty();
        this.setUUID(UUIDUtil.getOrCreatePlayerUUID(gameprofile));
        this.gameProfile = gameprofile;
        this.inventoryMenu = new ContainerPlayer(this.inventory, !world.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
        this.moveTo((double) blockposition.getX() + 0.5D, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D, f, 0.0F);
        this.rotOffs = 180.0F;
    }

    public boolean blockActionRestricted(World world, BlockPosition blockposition, EnumGamemode enumgamemode) {
        if (!enumgamemode.isBlockPlacingRestricted()) {
            return false;
        } else if (enumgamemode == EnumGamemode.SPECTATOR) {
            return true;
        } else if (this.mayBuild()) {
            return false;
        } else {
            ItemStack itemstack = this.getMainHandItem();

            return itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(world.registryAccess().registryOrThrow(Registries.BLOCK), new ShapeDetectorBlock(world, blockposition, false));
        }
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityLiving.createLivingAttributes().add(GenericAttributes.ATTACK_DAMAGE, 1.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.10000000149011612D).add(GenericAttributes.ATTACK_SPEED).add(GenericAttributes.LUCK);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityHuman.DATA_PLAYER_ABSORPTION_ID, 0.0F);
        this.entityData.define(EntityHuman.DATA_SCORE_ID, 0);
        this.entityData.define(EntityHuman.DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0);
        this.entityData.define(EntityHuman.DATA_PLAYER_MAIN_HAND, (byte) 1);
        this.entityData.define(EntityHuman.DATA_SHOULDER_LEFT, new NBTTagCompound());
        this.entityData.define(EntityHuman.DATA_SHOULDER_RIGHT, new NBTTagCompound());
    }

    @Override
    public void tick() {
        this.noPhysics = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.takeXpDelay > 0) {
            --this.takeXpDelay;
        }

        if (this.isSleeping()) {
            ++this.sleepCounter;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }

            if (!this.level.isClientSide && this.level.isDay()) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepCounter > 0) {
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }

        this.updateIsUnderwater();
        super.tick();
        if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }

        this.moveCloak();
        if (!this.level.isClientSide) {
            this.foodData.tick(this);
            this.awardStat(StatisticList.PLAY_TIME);
            this.awardStat(StatisticList.TOTAL_WORLD_TIME);
            if (this.isAlive()) {
                this.awardStat(StatisticList.TIME_SINCE_DEATH);
            }

            if (this.isDiscrete()) {
                this.awardStat(StatisticList.CROUCH_TIME);
            }

            if (!this.isSleeping()) {
                this.awardStat(StatisticList.TIME_SINCE_REST);
            }
        }

        int i = 29999999;
        double d0 = MathHelper.clamp(this.getX(), -2.9999999E7D, 2.9999999E7D);
        double d1 = MathHelper.clamp(this.getZ(), -2.9999999E7D, 2.9999999E7D);

        if (d0 != this.getX() || d1 != this.getZ()) {
            this.setPos(d0, this.getY(), d1);
        }

        ++this.attackStrengthTicker;
        ItemStack itemstack = this.getMainHandItem();

        if (!ItemStack.matches(this.lastItemInMainHand, itemstack)) {
            if (!ItemStack.isSame(this.lastItemInMainHand, itemstack)) {
                this.resetAttackStrengthTicker();
            }

            this.lastItemInMainHand = itemstack.copy();
        }

        this.turtleHelmetTick();
        this.cooldowns.tick();
        this.updatePlayerPose();
    }

    public boolean isSecondaryUseActive() {
        return this.isShiftKeyDown();
    }

    protected boolean wantsToStopRiding() {
        return this.isShiftKeyDown();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isShiftKeyDown();
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(TagsFluid.WATER);
        return this.wasUnderwater;
    }

    private void turtleHelmetTick() {
        ItemStack itemstack = this.getItemBySlot(EnumItemSlot.HEAD);

        if (itemstack.is(Items.TURTLE_HELMET) && !this.isEyeInFluid(TagsFluid.WATER)) {
            this.addEffect(new MobEffect(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
        }

    }

    protected ItemCooldown createItemCooldowns() {
        return new ItemCooldown();
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0D;

        if (d0 > 10.0D) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0D) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0D) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0D) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0D) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0D) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    protected void updatePlayerPose() {
        if (this.canEnterPose(EntityPose.SWIMMING)) {
            EntityPose entitypose;

            if (this.isFallFlying()) {
                entitypose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entitypose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                entitypose = EntityPose.SWIMMING;
            } else if (this.isAutoSpinAttack()) {
                entitypose = EntityPose.SPIN_ATTACK;
            } else if (this.isShiftKeyDown() && !this.abilities.flying) {
                entitypose = EntityPose.CROUCHING;
            } else {
                entitypose = EntityPose.STANDING;
            }

            EntityPose entitypose1;

            if (!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(entitypose)) {
                if (this.canEnterPose(EntityPose.CROUCHING)) {
                    entitypose1 = EntityPose.CROUCHING;
                } else {
                    entitypose1 = EntityPose.SWIMMING;
                }
            } else {
                entitypose1 = entitypose;
            }

            this.setPose(entitypose1);
        }
    }

    @Override
    public int getPortalWaitTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    @Override
    protected SoundEffect getSwimSound() {
        return SoundEffects.PLAYER_SWIM;
    }

    @Override
    protected SoundEffect getSwimSplashSound() {
        return SoundEffects.PLAYER_SPLASH;
    }

    @Override
    protected SoundEffect getSwimHighSpeedSplashSound() {
        return SoundEffects.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    @Override
    public void playSound(SoundEffect soundeffect, float f, float f1) {
        this.level.playSound(this, this.getX(), this.getY(), this.getZ(), soundeffect, this.getSoundSource(), f, f1);
    }

    public void playNotifySound(SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.PLAYERS;
    }

    @Override
    public int getFireImmuneTicks() {
        return 20;
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 9) {
            this.completeUsingItem();
        } else if (b0 == 23) {
            this.reducedDebugInfo = false;
        } else if (b0 == 22) {
            this.reducedDebugInfo = true;
        } else if (b0 == 43) {
            this.addParticlesAroundSelf(Particles.CLOUD);
        } else {
            super.handleEntityEvent(b0);
        }

    }

    private void addParticlesAroundSelf(ParticleParam particleparam) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particleparam, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    public void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }

    protected void doCloseContainer() {}

    @Override
    public void rideTick() {
        if (!this.level.isClientSide && this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setShiftKeyDown(false);
        } else {
            double d0 = this.getX();
            double d1 = this.getY();
            double d2 = this.getZ();

            super.rideTick();
            this.oBob = this.bob;
            this.bob = 0.0F;
            this.checkRidingStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
        }
    }

    @Override
    protected void serverAiStep() {
        super.serverAiStep();
        this.updateSwingTime();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public void aiStep() {
        if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }

        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
                this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
            }
        }

        this.inventory.tick();
        this.oBob = this.bob;
        super.aiStep();
        this.setSpeed((float) this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));
        float f;

        if (this.onGround && !this.isDeadOrDying() && !this.isSwimming()) {
            f = Math.min(0.1F, (float) this.getDeltaMovement().horizontalDistance());
        } else {
            f = 0.0F;
        }

        this.bob += (f - this.bob) * 0.4F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;

            if (this.isPassenger() && !this.getVehicle().isRemoved()) {
                axisalignedbb = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.level.getEntities(this, axisalignedbb);
            List<Entity> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity.getType() == EntityTypes.EXPERIENCE_ORB) {
                    list1.add(entity);
                } else if (!entity.isRemoved()) {
                    this.touch(entity);
                }
            }

            if (!list1.isEmpty()) {
                this.touch((Entity) SystemUtils.getRandom((List) list1, this.random));
            }
        }

        this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
        this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
        if (!this.level.isClientSide && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
            this.removeEntitiesOnShoulder();
        }

    }

    private void playShoulderEntityAmbientSound(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && (!nbttagcompound.contains("Silent") || !nbttagcompound.getBoolean("Silent")) && this.level.random.nextInt(200) == 0) {
            String s = nbttagcompound.getString("id");

            EntityTypes.byString(s).filter((entitytypes) -> {
                return entitytypes == EntityTypes.PARROT;
            }).ifPresent((entitytypes) -> {
                if (!EntityParrot.imitateNearbyMobs(this.level, this)) {
                    this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), EntityParrot.getAmbient(this.level, this.level.random), this.getSoundSource(), 1.0F, EntityParrot.getPitch(this.level.random));
                }

            });
        }

    }

    private void touch(Entity entity) {
        entity.playerTouch(this);
    }

    public int getScore() {
        return (Integer) this.entityData.get(EntityHuman.DATA_SCORE_ID);
    }

    public void setScore(int i) {
        this.entityData.set(EntityHuman.DATA_SCORE_ID, i);
    }

    public void increaseScore(int i) {
        int j = this.getScore();

        this.entityData.set(EntityHuman.DATA_SCORE_ID, j + i);
    }

    public void startAutoSpinAttack(int i) {
        this.autoSpinAttackTicks = i;
        if (!this.level.isClientSide) {
            this.removeEntitiesOnShoulder();
            this.setLivingEntityFlag(4, true);
        }

    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.reapplyPosition();
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(damagesource);
        }

        if (damagesource != null) {
            this.setDeltaMovement((double) (-MathHelper.cos((this.getHurtDir() + this.getYRot()) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double) (-MathHelper.sin((this.getHurtDir() + this.getYRot()) * 0.017453292F) * 0.1F));
        } else {
            this.setDeltaMovement(0.0D, 0.1D, 0.0D);
        }

        this.awardStat(StatisticList.DEATHS);
        this.resetStat(StatisticList.CUSTOM.get(StatisticList.TIME_SINCE_DEATH));
        this.resetStat(StatisticList.CUSTOM.get(StatisticList.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlagOnFire(false);
        this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level.dimension(), this.blockPosition())));
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }

    }

    protected void destroyVanishingCursedItems() {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && EnchantmentManager.hasVanishingCurse(itemstack)) {
                this.inventory.removeItemNoUpdate(i);
            }
        }

    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return damagesource.type().effects().sound();
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.PLAYER_DEATH;
    }

    @Nullable
    public EntityItem drop(ItemStack itemstack, boolean flag) {
        return this.drop(itemstack, false, flag);
    }

    @Nullable
    public EntityItem drop(ItemStack itemstack, boolean flag, boolean flag1) {
        if (itemstack.isEmpty()) {
            return null;
        } else {
            if (this.level.isClientSide) {
                this.swing(EnumHand.MAIN_HAND);
            }

            double d0 = this.getEyeY() - 0.30000001192092896D;
            EntityItem entityitem = new EntityItem(this.level, this.getX(), d0, this.getZ(), itemstack);

            entityitem.setPickUpDelay(40);
            if (flag1) {
                entityitem.setThrower(this.getUUID());
            }

            float f;
            float f1;

            if (flag) {
                f = this.random.nextFloat() * 0.5F;
                f1 = this.random.nextFloat() * 6.2831855F;
                entityitem.setDeltaMovement((double) (-MathHelper.sin(f1) * f), 0.20000000298023224D, (double) (MathHelper.cos(f1) * f));
            } else {
                f = 0.3F;
                f1 = MathHelper.sin(this.getXRot() * 0.017453292F);
                float f2 = MathHelper.cos(this.getXRot() * 0.017453292F);
                float f3 = MathHelper.sin(this.getYRot() * 0.017453292F);
                float f4 = MathHelper.cos(this.getYRot() * 0.017453292F);
                float f5 = this.random.nextFloat() * 6.2831855F;
                float f6 = 0.02F * this.random.nextFloat();

                entityitem.setDeltaMovement((double) (-f3 * f2 * 0.3F) + Math.cos((double) f5) * (double) f6, (double) (-f1 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (f4 * f2 * 0.3F) + Math.sin((double) f5) * (double) f6);
            }

            return entityitem;
        }
    }

    public float getDestroySpeed(IBlockData iblockdata) {
        float f = this.inventory.getDestroySpeed(iblockdata);

        if (f > 1.0F) {
            int i = EnchantmentManager.getBlockEfficiency(this);
            ItemStack itemstack = this.getMainHandItem();

            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(this)) {
            f *= 1.0F + (float) (MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;

            switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (this.isEyeInFluid(TagsFluid.WATER) && !EnchantmentManager.hasAquaAffinity(this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean hasCorrectToolForDrops(IBlockData iblockdata) {
        return !iblockdata.requiresCorrectToolForDrops() || this.inventory.getSelected().isCorrectToolForDrops(iblockdata);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setUUID(UUIDUtil.getOrCreatePlayerUUID(this.gameProfile));
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        this.inventory.load(nbttaglist);
        this.inventory.selected = nbttagcompound.getInt("SelectedItemSlot");
        this.sleepCounter = nbttagcompound.getShort("SleepTimer");
        this.experienceProgress = nbttagcompound.getFloat("XpP");
        this.experienceLevel = nbttagcompound.getInt("XpLevel");
        this.totalExperience = nbttagcompound.getInt("XpTotal");
        this.enchantmentSeed = nbttagcompound.getInt("XpSeed");
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }

        this.setScore(nbttagcompound.getInt("Score"));
        this.foodData.readAdditionalSaveData(nbttagcompound);
        this.abilities.loadSaveData(nbttagcompound);
        this.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue((double) this.abilities.getWalkingSpeed());
        if (nbttagcompound.contains("EnderItems", 9)) {
            this.enderChestInventory.fromTag(nbttagcompound.getList("EnderItems", 10));
        }

        if (nbttagcompound.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(nbttagcompound.getCompound("ShoulderEntityLeft"));
        }

        if (nbttagcompound.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(nbttagcompound.getCompound("ShoulderEntityRight"));
        }

        if (nbttagcompound.contains("LastDeathLocation", 10)) {
            DataResult dataresult = GlobalPos.CODEC.parse(DynamicOpsNBT.INSTANCE, nbttagcompound.get("LastDeathLocation"));
            Logger logger = EntityHuman.LOGGER;

            Objects.requireNonNull(logger);
            this.setLastDeathLocation(dataresult.resultOrPartial(logger::error));
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        GameProfileSerializer.addCurrentDataVersion(nbttagcompound);
        nbttagcompound.put("Inventory", this.inventory.save(new NBTTagList()));
        nbttagcompound.putInt("SelectedItemSlot", this.inventory.selected);
        nbttagcompound.putShort("SleepTimer", (short) this.sleepCounter);
        nbttagcompound.putFloat("XpP", this.experienceProgress);
        nbttagcompound.putInt("XpLevel", this.experienceLevel);
        nbttagcompound.putInt("XpTotal", this.totalExperience);
        nbttagcompound.putInt("XpSeed", this.enchantmentSeed);
        nbttagcompound.putInt("Score", this.getScore());
        this.foodData.addAdditionalSaveData(nbttagcompound);
        this.abilities.addSaveData(nbttagcompound);
        nbttagcompound.put("EnderItems", this.enderChestInventory.createTag());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            nbttagcompound.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            nbttagcompound.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }

        this.getLastDeathLocation().flatMap((globalpos) -> {
            DataResult dataresult = GlobalPos.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, globalpos);
            Logger logger = EntityHuman.LOGGER;

            Objects.requireNonNull(logger);
            return dataresult.resultOrPartial(logger::error);
        }).ifPresent((nbtbase) -> {
            nbttagcompound.put("LastDeathLocation", nbtbase);
        });
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damagesource) {
        return super.isInvulnerableTo(damagesource) ? true : (damagesource.is(DamageTypeTags.IS_DROWNING) ? !this.level.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE) : (damagesource.is(DamageTypeTags.IS_FALL) ? !this.level.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE) : (damagesource.is(DamageTypeTags.IS_FIRE) ? !this.level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE) : (damagesource.is(DamageTypeTags.IS_FREEZING) ? !this.level.getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE) : false))));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (this.abilities.invulnerable && !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                if (!this.level.isClientSide) {
                    this.removeEntitiesOnShoulder();
                }

                if (damagesource.scalesWithDifficulty()) {
                    if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL) {
                        f = 0.0F;
                    }

                    if (this.level.getDifficulty() == EnumDifficulty.EASY) {
                        f = Math.min(f / 2.0F + 1.0F, f);
                    }

                    if (this.level.getDifficulty() == EnumDifficulty.HARD) {
                        f = f * 3.0F / 2.0F;
                    }
                }

                return f == 0.0F ? false : super.hurt(damagesource, f);
            }
        }
    }

    @Override
    protected void blockUsingShield(EntityLiving entityliving) {
        super.blockUsingShield(entityliving);
        if (entityliving.canDisableShield()) {
            this.disableShield(true);
        }

    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
    }

    public boolean canHarmPlayer(EntityHuman entityhuman) {
        ScoreboardTeamBase scoreboardteambase = this.getTeam();
        ScoreboardTeamBase scoreboardteambase1 = entityhuman.getTeam();

        return scoreboardteambase == null ? true : (!scoreboardteambase.isAlliedTo(scoreboardteambase1) ? true : scoreboardteambase.isAllowFriendlyFire());
    }

    @Override
    protected void hurtArmor(DamageSource damagesource, float f) {
        this.inventory.hurtArmor(damagesource, f, PlayerInventory.ALL_ARMOR_SLOTS);
    }

    @Override
    protected void hurtHelmet(DamageSource damagesource, float f) {
        this.inventory.hurtArmor(damagesource, f, PlayerInventory.HELMET_SLOT_ONLY);
    }

    @Override
    protected void hurtCurrentlyUsedShield(float f) {
        if (this.useItem.is(Items.SHIELD)) {
            if (!this.level.isClientSide) {
                this.awardStat(StatisticList.ITEM_USED.get(this.useItem.getItem()));
            }

            if (f >= 3.0F) {
                int i = 1 + MathHelper.floor(f);
                EnumHand enumhand = this.getUsedItemHand();

                this.useItem.hurtAndBreak(i, this, (entityhuman) -> {
                    entityhuman.broadcastBreakEvent(enumhand);
                });
                if (this.useItem.isEmpty()) {
                    if (enumhand == EnumHand.MAIN_HAND) {
                        this.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setItemSlot(EnumItemSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.useItem = ItemStack.EMPTY;
                    this.playSound(SoundEffects.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    @Override
    protected void actuallyHurt(DamageSource damagesource, float f) {
        if (!this.isInvulnerableTo(damagesource)) {
            f = this.getDamageAfterArmorAbsorb(damagesource, f);
            f = this.getDamageAfterMagicAbsorb(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - f));
            float f2 = f1 - f;

            if (f2 > 0.0F && f2 < 3.4028235E37F) {
                this.awardStat(StatisticList.DAMAGE_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f != 0.0F) {
                this.causeFoodExhaustion(damagesource.getFoodExhaustion());
                float f3 = this.getHealth();

                this.getCombatTracker().recordDamage(damagesource, f3, f);
                this.setHealth(this.getHealth() - f);
                if (f < 3.4028235E37F) {
                    this.awardStat(StatisticList.DAMAGE_TAKEN, Math.round(f * 10.0F));
                }

            }
        }
    }

    @Override
    protected boolean onSoulSpeedBlock() {
        return !this.abilities.flying && super.onSoulSpeedBlock();
    }

    public boolean isTextFilteringEnabled() {
        return false;
    }

    public void openTextEdit(TileEntitySign tileentitysign) {}

    public void openMinecartCommandBlock(CommandBlockListenerAbstract commandblocklistenerabstract) {}

    public void openCommandBlock(TileEntityCommand tileentitycommand) {}

    public void openStructureBlock(TileEntityStructure tileentitystructure) {}

    public void openJigsawBlock(TileEntityJigsaw tileentityjigsaw) {}

    public void openHorseInventory(EntityHorseAbstract entityhorseabstract, IInventory iinventory) {}

    public OptionalInt openMenu(@Nullable ITileInventory itileinventory) {
        return OptionalInt.empty();
    }

    public void sendMerchantOffers(int i, MerchantRecipeList merchantrecipelist, int j, int k, boolean flag, boolean flag1) {}

    public void openItemGui(ItemStack itemstack, EnumHand enumhand) {}

    public EnumInteractionResult interactOn(Entity entity, EnumHand enumhand) {
        if (this.isSpectator()) {
            if (entity instanceof ITileInventory) {
                this.openMenu((ITileInventory) entity);
            }

            return EnumInteractionResult.PASS;
        } else {
            ItemStack itemstack = this.getItemInHand(enumhand);
            ItemStack itemstack1 = itemstack.copy();
            EnumInteractionResult enuminteractionresult = entity.interact(this, enumhand);

            if (enuminteractionresult.consumesAction()) {
                if (this.abilities.instabuild && itemstack == this.getItemInHand(enumhand) && itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                return enuminteractionresult;
            } else {
                if (!itemstack.isEmpty() && entity instanceof EntityLiving) {
                    if (this.abilities.instabuild) {
                        itemstack = itemstack1;
                    }

                    EnumInteractionResult enuminteractionresult1 = itemstack.interactLivingEntity(this, (EntityLiving) entity, enumhand);

                    if (enuminteractionresult1.consumesAction()) {
                        this.level.gameEvent(GameEvent.ENTITY_INTERACT, entity.position(), GameEvent.a.of((Entity) this));
                        if (itemstack.isEmpty() && !this.abilities.instabuild) {
                            this.setItemInHand(enumhand, ItemStack.EMPTY);
                        }

                        return enuminteractionresult1;
                    }
                }

                return EnumInteractionResult.PASS;
            }
        }
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35D;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.boardingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    @Override
    public boolean isAffectedByFluids() {
        return !this.abilities.flying;
    }

    @Override
    protected Vec3D maybeBackOffFromEdge(Vec3D vec3d, EnumMoveType enummovetype) {
        if (!this.abilities.flying && vec3d.y <= 0.0D && (enummovetype == EnumMoveType.SELF || enummovetype == EnumMoveType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
            double d0 = vec3d.x;
            double d1 = vec3d.z;
            double d2 = 0.05D;

            while (d0 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(d0, (double) (-this.maxUpStep()), 0.0D))) {
                if (d0 < 0.05D && d0 >= -0.05D) {
                    d0 = 0.0D;
                } else if (d0 > 0.0D) {
                    d0 -= 0.05D;
                } else {
                    d0 += 0.05D;
                }
            }

            while (d1 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double) (-this.maxUpStep()), d1))) {
                if (d1 < 0.05D && d1 >= -0.05D) {
                    d1 = 0.0D;
                } else if (d1 > 0.0D) {
                    d1 -= 0.05D;
                } else {
                    d1 += 0.05D;
                }
            }

            while (d0 != 0.0D && d1 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(d0, (double) (-this.maxUpStep()), d1))) {
                if (d0 < 0.05D && d0 >= -0.05D) {
                    d0 = 0.0D;
                } else if (d0 > 0.0D) {
                    d0 -= 0.05D;
                } else {
                    d0 += 0.05D;
                }

                if (d1 < 0.05D && d1 >= -0.05D) {
                    d1 = 0.0D;
                } else if (d1 > 0.0D) {
                    d1 -= 0.05D;
                } else {
                    d1 += 0.05D;
                }
            }

            vec3d = new Vec3D(d0, vec3d.y, d1);
        }

        return vec3d;
    }

    private boolean isAboveGround() {
        return this.onGround || this.fallDistance < this.maxUpStep() && !this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double) (this.fallDistance - this.maxUpStep()), 0.0D));
    }

    public void attack(Entity entity) {
        if (entity.isAttackable()) {
            if (!entity.skipAttackInteraction(this)) {
                float f = (float) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE);
                float f1;

                if (entity instanceof EntityLiving) {
                    f1 = EnchantmentManager.getDamageBonus(this.getMainHandItem(), ((EntityLiving) entity).getMobType());
                } else {
                    f1 = EnchantmentManager.getDamageBonus(this.getMainHandItem(), EnumMonsterType.UNDEFINED);
                }

                float f2 = this.getAttackStrengthScale(0.5F);

                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                this.resetAttackStrengthTicker();
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    byte b0 = 0;
                    int i = b0 + EnchantmentManager.getKnockbackBonus(this);

                    if (this.isSprinting() && flag) {
                        this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && entity instanceof EntityLiving;

                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.walkDist - this.walkDistO);

                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.getSpeed()) {
                        ItemStack itemstack = this.getItemInHand(EnumHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f3 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentManager.getFireAspect(this);

                    if (entity instanceof EntityLiving) {
                        f3 = ((EntityLiving) entity).getHealth();
                        if (j > 0 && !entity.isOnFire()) {
                            flag4 = true;
                            entity.setSecondsOnFire(1);
                        }
                    }

                    Vec3D vec3d = entity.getDeltaMovement();
                    boolean flag5 = entity.hurt(this.damageSources().playerAttack(this), f);

                    if (flag5) {
                        if (i > 0) {
                            if (entity instanceof EntityLiving) {
                                ((EntityLiving) entity).knockback((double) ((float) i * 0.5F), (double) MathHelper.sin(this.getYRot() * 0.017453292F), (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)));
                            } else {
                                entity.push((double) (-MathHelper.sin(this.getYRot() * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.getYRot() * 0.017453292F) * (float) i * 0.5F));
                            }

                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (flag3) {
                            float f4 = 1.0F + EnchantmentManager.getSweepingDamageRatio(this) * f;
                            List<EntityLiving> list = this.level.getEntitiesOfClass(EntityLiving.class, entity.getBoundingBox().inflate(1.0D, 0.25D, 1.0D));
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                EntityLiving entityliving = (EntityLiving) iterator.next();

                                if (entityliving != this && entityliving != entity && !this.isAlliedTo((Entity) entityliving) && (!(entityliving instanceof EntityArmorStand) || !((EntityArmorStand) entityliving).isMarker()) && this.distanceToSqr((Entity) entityliving) < 9.0D) {
                                    entityliving.knockback(0.4000000059604645D, (double) MathHelper.sin(this.getYRot() * 0.017453292F), (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)));
                                    entityliving.hurt(this.damageSources().playerAttack(this), f4);
                                }
                            }

                            this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                            this.sweepAttack();
                        }

                        if (entity instanceof EntityPlayer && entity.hurtMarked) {
                            ((EntityPlayer) entity).connection.send(new PacketPlayOutEntityVelocity(entity));
                            entity.hurtMarked = false;
                            entity.setDeltaMovement(vec3d);
                        }

                        if (flag2) {
                            this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                            this.crit(entity);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                                this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.magicCrit(entity);
                        }

                        this.setLastHurtMob(entity);
                        if (entity instanceof EntityLiving) {
                            EnchantmentManager.doPostHurtEffects((EntityLiving) entity, this);
                        }

                        EnchantmentManager.doPostDamageEffects(this, entity);
                        ItemStack itemstack1 = this.getMainHandItem();
                        Object object = entity;

                        if (entity instanceof EntityComplexPart) {
                            object = ((EntityComplexPart) entity).parentMob;
                        }

                        if (!this.level.isClientSide && !itemstack1.isEmpty() && object instanceof EntityLiving) {
                            itemstack1.hurtEnemy((EntityLiving) object, this);
                            if (itemstack1.isEmpty()) {
                                this.setItemInHand(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (entity instanceof EntityLiving) {
                            float f5 = f3 - ((EntityLiving) entity).getHealth();

                            this.awardStat(StatisticList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                entity.setSecondsOnFire(j * 4);
                            }

                            if (this.level instanceof WorldServer && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);

                                ((WorldServer) this.level).sendParticles(Particles.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5D), entity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.causeFoodExhaustion(0.1F);
                    } else {
                        this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                        if (flag4) {
                            entity.clearFire();
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void doAutoAttackOnTouch(EntityLiving entityliving) {
        this.attack(entityliving);
    }

    public void disableShield(boolean flag) {
        float f = 0.25F + (float) EnchantmentManager.getBlockEfficiency(this) * 0.05F;

        if (flag) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getCooldowns().addCooldown(Items.SHIELD, 100);
            this.stopUsingItem();
            this.level.broadcastEntityEvent(this, (byte) 30);
        }

    }

    public void crit(Entity entity) {}

    public void magicCrit(Entity entity) {}

    public void sweepAttack() {
        double d0 = (double) (-MathHelper.sin(this.getYRot() * 0.017453292F));
        double d1 = (double) MathHelper.cos(this.getYRot() * 0.017453292F);

        if (this.level instanceof WorldServer) {
            ((WorldServer) this.level).sendParticles(Particles.SWEEP_ATTACK, this.getX() + d0, this.getY(0.5D), this.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }

    }

    public void respawn() {}

    @Override
    public void remove(Entity.RemovalReason entity_removalreason) {
        super.remove(entity_removalreason);
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null && this.hasContainerOpen()) {
            this.doCloseContainer();
        }

    }

    public boolean isLocalPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    public void updateTutorialInventoryAction(ItemStack itemstack, ItemStack itemstack1, ClickAction clickaction) {}

    public boolean hasContainerOpen() {
        return this.containerMenu != this.inventoryMenu;
    }

    public Either<EntityHuman.EnumBedResult, Unit> startSleepInBed(BlockPosition blockposition) {
        this.startSleeping(blockposition);
        this.sleepCounter = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void stopSleepInBed(boolean flag, boolean flag1) {
        super.stopSleeping();
        if (this.level instanceof WorldServer && flag1) {
            ((WorldServer) this.level).updateSleepingPlayerList();
        }

        this.sleepCounter = flag ? 0 : 100;
    }

    @Override
    public void stopSleeping() {
        this.stopSleepInBed(true, true);
    }

    public static Optional<Vec3D> findRespawnPositionAndUseSpawnBlock(WorldServer worldserver, BlockPosition blockposition, float f, boolean flag, boolean flag1) {
        IBlockData iblockdata = worldserver.getBlockState(blockposition);
        Block block = iblockdata.getBlock();

        if (block instanceof BlockRespawnAnchor && (flag || (Integer) iblockdata.getValue(BlockRespawnAnchor.CHARGE) > 0) && BlockRespawnAnchor.canSetSpawn(worldserver)) {
            Optional<Vec3D> optional = BlockRespawnAnchor.findStandUpPosition(EntityTypes.PLAYER, worldserver, blockposition);

            if (!flag && !flag1 && optional.isPresent()) {
                worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockRespawnAnchor.CHARGE, (Integer) iblockdata.getValue(BlockRespawnAnchor.CHARGE) - 1), 3);
            }

            return optional;
        } else if (block instanceof BlockBed && BlockBed.canSetSpawn(worldserver)) {
            return BlockBed.findStandUpPosition(EntityTypes.PLAYER, worldserver, blockposition, (EnumDirection) iblockdata.getValue(BlockBed.FACING), f);
        } else if (!flag) {
            return Optional.empty();
        } else {
            boolean flag2 = block.isPossibleToRespawnInThis();
            boolean flag3 = worldserver.getBlockState(blockposition.above()).getBlock().isPossibleToRespawnInThis();

            return flag2 && flag3 ? Optional.of(new Vec3D((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D)) : Optional.empty();
        }
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int getSleepTimer() {
        return this.sleepCounter;
    }

    public void displayClientMessage(IChatBaseComponent ichatbasecomponent, boolean flag) {}

    public void awardStat(MinecraftKey minecraftkey) {
        this.awardStat(StatisticList.CUSTOM.get(minecraftkey));
    }

    public void awardStat(MinecraftKey minecraftkey, int i) {
        this.awardStat(StatisticList.CUSTOM.get(minecraftkey), i);
    }

    public void awardStat(Statistic<?> statistic) {
        this.awardStat(statistic, 1);
    }

    public void awardStat(Statistic<?> statistic, int i) {}

    public void resetStat(Statistic<?> statistic) {}

    public int awardRecipes(Collection<IRecipe<?>> collection) {
        return 0;
    }

    public void awardRecipesByKey(MinecraftKey[] aminecraftkey) {}

    public int resetRecipes(Collection<IRecipe<?>> collection) {
        return 0;
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(StatisticList.JUMP);
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2F);
        } else {
            this.causeFoodExhaustion(0.05F);
        }

    }

    @Override
    public void travel(Vec3D vec3d) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        double d3;

        if (this.isSwimming() && !this.isPassenger()) {
            d3 = this.getLookAngle().y;
            double d4 = d3 < -0.2D ? 0.085D : 0.06D;

            if (d3 <= 0.0D || this.jumping || !this.level.getBlockState(BlockPosition.containing(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
                Vec3D vec3d1 = this.getDeltaMovement();

                this.setDeltaMovement(vec3d1.add(0.0D, (d3 - vec3d1.y) * d4, 0.0D));
            }
        }

        if (this.abilities.flying && !this.isPassenger()) {
            d3 = this.getDeltaMovement().y;
            super.travel(vec3d);
            Vec3D vec3d2 = this.getDeltaMovement();

            this.setDeltaMovement(vec3d2.x, d3 * 0.6D, vec3d2.z);
            this.resetFallDistance();
            this.setSharedFlag(7, false);
        } else {
            super.travel(vec3d);
        }

        this.checkMovementStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }

    }

    protected boolean freeAt(BlockPosition blockposition) {
        return !this.level.getBlockState(blockposition).isSuffocating(this.level, blockposition);
    }

    @Override
    public float getSpeed() {
        return (float) this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED);
    }

    public void checkMovementStatistics(double d0, double d1, double d2) {
        if (!this.isPassenger()) {
            int i;

            if (this.isSwimming()) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.awardStat(StatisticList.SWIM_ONE_CM, i);
                    this.causeFoodExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.isEyeInFluid(TagsFluid.WATER)) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.awardStat(StatisticList.WALK_UNDER_WATER_ONE_CM, i);
                    this.causeFoodExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.isInWater()) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.awardStat(StatisticList.WALK_ON_WATER_ONE_CM, i);
                    this.causeFoodExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.onClimbable()) {
                if (d1 > 0.0D) {
                    this.awardStat(StatisticList.CLIMB_ONE_CM, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    if (this.isSprinting()) {
                        this.awardStat(StatisticList.SPRINT_ONE_CM, i);
                        this.causeFoodExhaustion(0.1F * (float) i * 0.01F);
                    } else if (this.isCrouching()) {
                        this.awardStat(StatisticList.CROUCH_ONE_CM, i);
                        this.causeFoodExhaustion(0.0F * (float) i * 0.01F);
                    } else {
                        this.awardStat(StatisticList.WALK_ONE_CM, i);
                        this.causeFoodExhaustion(0.0F * (float) i * 0.01F);
                    }
                }
            } else if (this.isFallFlying()) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                this.awardStat(StatisticList.AVIATE_ONE_CM, i);
            } else {
                i = Math.round((float) Math.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.awardStat(StatisticList.FLY_ONE_CM, i);
                }
            }

        }
    }

    private void checkRidingStatistics(double d0, double d1, double d2) {
        if (this.isPassenger()) {
            int i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                Entity entity = this.getVehicle();

                if (entity instanceof EntityMinecartAbstract) {
                    this.awardStat(StatisticList.MINECART_ONE_CM, i);
                } else if (entity instanceof EntityBoat) {
                    this.awardStat(StatisticList.BOAT_ONE_CM, i);
                } else if (entity instanceof EntityPig) {
                    this.awardStat(StatisticList.PIG_ONE_CM, i);
                } else if (entity instanceof EntityHorseAbstract) {
                    this.awardStat(StatisticList.HORSE_ONE_CM, i);
                } else if (entity instanceof EntityStrider) {
                    this.awardStat(StatisticList.STRIDER_ONE_CM, i);
                }
            }
        }

    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        if (this.abilities.mayfly) {
            return false;
        } else {
            if (f >= 2.0F) {
                this.awardStat(StatisticList.FALL_ONE_CM, (int) Math.round((double) f * 100.0D));
            }

            return super.causeFallDamage(f, f1, damagesource);
        }
    }

    public boolean tryToStartFallFlying() {
        if (!this.onGround && !this.isFallFlying() && !this.isInWater() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack itemstack = this.getItemBySlot(EnumItemSlot.CHEST);

            if (itemstack.is(Items.ELYTRA) && ItemElytra.isFlyEnabled(itemstack)) {
                this.startFallFlying();
                return true;
            }
        }

        return false;
    }

    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }

    public void stopFallFlying() {
        this.setSharedFlag(7, true);
        this.setSharedFlag(7, false);
    }

    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }

    }

    @Override
    public EntityLiving.a getFallSounds() {
        return new EntityLiving.a(SoundEffects.PLAYER_SMALL_FALL, SoundEffects.PLAYER_BIG_FALL);
    }

    @Override
    public boolean wasKilled(WorldServer worldserver, EntityLiving entityliving) {
        this.awardStat(StatisticList.ENTITY_KILLED.get(entityliving.getType()));
        return true;
    }

    @Override
    public void makeStuckInBlock(IBlockData iblockdata, Vec3D vec3d) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock(iblockdata, vec3d);
        }

    }

    public void giveExperiencePoints(int i) {
        this.increaseScore(i);
        this.experienceProgress += (float) i / (float) this.getXpNeededForNextLevel();
        this.totalExperience = MathHelper.clamp(this.totalExperience + i, 0, Integer.MAX_VALUE);

        while (this.experienceProgress < 0.0F) {
            float f = this.experienceProgress * (float) this.getXpNeededForNextLevel();

            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0F + f / (float) this.getXpNeededForNextLevel();
            } else {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while (this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * (float) this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress /= (float) this.getXpNeededForNextLevel();
        }

    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }

    public void onEnchantmentPerformed(ItemStack itemstack, int i) {
        this.experienceLevel -= i;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        this.enchantmentSeed = this.random.nextInt();
    }

    public void giveExperienceLevels(int i) {
        this.experienceLevel += i;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        if (i > 0 && this.experienceLevel % 5 == 0 && (float) this.lastLevelUpTime < (float) this.tickCount - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float) this.experienceLevel / 30.0F;

            this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_LEVELUP, this.getSoundSource(), f * 0.75F, 1.0F);
            this.lastLevelUpTime = this.tickCount;
        }

    }

    public int getXpNeededForNextLevel() {
        return this.experienceLevel >= 30 ? 112 + (this.experienceLevel - 30) * 9 : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
    }

    public void causeFoodExhaustion(float f) {
        if (!this.abilities.invulnerable) {
            if (!this.level.isClientSide) {
                this.foodData.addExhaustion(f);
            }

        }
    }

    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.empty();
    }

    public FoodMetaData getFoodData() {
        return this.foodData;
    }

    public boolean canEat(boolean flag) {
        return this.abilities.invulnerable || flag || this.foodData.needsFood();
    }

    public boolean isHurt() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }

    public boolean mayUseItemAt(BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.relative(enumdirection.getOpposite());
            ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(this.level, blockposition1, false);

            return itemstack.hasAdventureModePlaceTagForBlock(this.level.registryAccess().registryOrThrow(Registries.BLOCK), shapedetectorblock);
        }
    }

    @Override
    public int getExperienceReward() {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
            int i = this.experienceLevel * 7;

            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return !this.abilities.flying && (!this.onGround || !this.isDiscrete()) ? Entity.MovementEmission.ALL : Entity.MovementEmission.NONE;
    }

    public void onUpdateAbilities() {}

    @Override
    public IChatBaseComponent getName() {
        return IChatBaseComponent.literal(this.gameProfile.getName());
    }

    public InventoryEnderChest getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    public ItemStack getItemBySlot(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.inventory.getSelected() : (enumitemslot == EnumItemSlot.OFFHAND ? (ItemStack) this.inventory.offhand.get(0) : (enumitemslot.getType() == EnumItemSlot.Function.ARMOR ? (ItemStack) this.inventory.armor.get(enumitemslot.getIndex()) : ItemStack.EMPTY));
    }

    @Override
    protected boolean doesEmitEquipEvent(EnumItemSlot enumitemslot) {
        return enumitemslot.getType() == EnumItemSlot.Function.ARMOR;
    }

    @Override
    public void setItemSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.verifyEquippedItem(itemstack);
        if (enumitemslot == EnumItemSlot.MAINHAND) {
            this.onEquipItem(enumitemslot, (ItemStack) this.inventory.items.set(this.inventory.selected, itemstack), itemstack);
        } else if (enumitemslot == EnumItemSlot.OFFHAND) {
            this.onEquipItem(enumitemslot, (ItemStack) this.inventory.offhand.set(0, itemstack), itemstack);
        } else if (enumitemslot.getType() == EnumItemSlot.Function.ARMOR) {
            this.onEquipItem(enumitemslot, (ItemStack) this.inventory.armor.set(enumitemslot.getIndex(), itemstack), itemstack);
        }

    }

    public boolean addItem(ItemStack itemstack) {
        return this.inventory.add(itemstack);
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return Lists.newArrayList(new ItemStack[]{this.getMainHandItem(), this.getOffhandItem()});
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.inventory.armor;
    }

    public boolean setEntityOnShoulder(NBTTagCompound nbttagcompound) {
        if (!this.isPassenger() && this.onGround && !this.isInWater() && !this.isInPowderSnow) {
            if (this.getShoulderEntityLeft().isEmpty()) {
                this.setShoulderEntityLeft(nbttagcompound);
                this.timeEntitySatOnShoulder = this.level.getGameTime();
                return true;
            } else if (this.getShoulderEntityRight().isEmpty()) {
                this.setShoulderEntityRight(nbttagcompound);
                this.timeEntitySatOnShoulder = this.level.getGameTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new NBTTagCompound());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new NBTTagCompound());
        }

    }

    private void respawnEntityOnShoulder(NBTTagCompound nbttagcompound) {
        if (!this.level.isClientSide && !nbttagcompound.isEmpty()) {
            EntityTypes.create(nbttagcompound, this.level).ifPresent((entity) -> {
                if (entity instanceof EntityTameableAnimal) {
                    ((EntityTameableAnimal) entity).setOwnerUUID(this.uuid);
                }

                entity.setPos(this.getX(), this.getY() + 0.699999988079071D, this.getZ());
                ((WorldServer) this.level).addWithUUID(entity);
            });
        }

    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean canBeHitByProjectile() {
        return !this.isSpectator() && super.canBeHitByProjectile();
    }

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean isPushedByFluid() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.level.getScoreboard();
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        IChatMutableComponent ichatmutablecomponent = ScoreboardTeam.formatNameForTeam(this.getTeam(), this.getName());

        return this.decorateDisplayNameComponent(ichatmutablecomponent);
    }

    private IChatMutableComponent decorateDisplayNameComponent(IChatMutableComponent ichatmutablecomponent) {
        String s = this.getGameProfile().getName();

        return ichatmutablecomponent.withStyle((chatmodifier) -> {
            return chatmodifier.withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/tell " + s + " ")).withHoverEvent(this.createHoverEvent()).withInsertion(s);
        });
    }

    @Override
    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    @Override
    public float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        switch (entitypose) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;
            case CROUCHING:
                return 1.27F;
            default:
                return 1.62F;
        }
    }

    @Override
    public void setAbsorptionAmount(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.getEntityData().set(EntityHuman.DATA_PLAYER_ABSORPTION_ID, f);
    }

    @Override
    public float getAbsorptionAmount() {
        return (Float) this.getEntityData().get(EntityHuman.DATA_PLAYER_ABSORPTION_ID);
    }

    public boolean isModelPartShown(PlayerModelPart playermodelpart) {
        return ((Byte) this.getEntityData().get(EntityHuman.DATA_PLAYER_MODE_CUSTOMISATION) & playermodelpart.getMask()) == playermodelpart.getMask();
    }

    @Override
    public SlotAccess getSlot(int i) {
        if (i >= 0 && i < this.inventory.items.size()) {
            return SlotAccess.forContainer(this.inventory, i);
        } else {
            int j = i - 200;

            return j >= 0 && j < this.enderChestInventory.getContainerSize() ? SlotAccess.forContainer(this.enderChestInventory, j) : super.getSlot(i);
        }
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(boolean flag) {
        this.reducedDebugInfo = flag;
    }

    @Override
    public void setRemainingFireTicks(int i) {
        super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(i, 1) : i);
    }

    @Override
    public EnumMainHand getMainArm() {
        return (Byte) this.entityData.get(EntityHuman.DATA_PLAYER_MAIN_HAND) == 0 ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public void setMainArm(EnumMainHand enummainhand) {
        this.entityData.set(EntityHuman.DATA_PLAYER_MAIN_HAND, (byte) (enummainhand == EnumMainHand.LEFT ? 0 : 1));
    }

    public NBTTagCompound getShoulderEntityLeft() {
        return (NBTTagCompound) this.entityData.get(EntityHuman.DATA_SHOULDER_LEFT);
    }

    public void setShoulderEntityLeft(NBTTagCompound nbttagcompound) {
        this.entityData.set(EntityHuman.DATA_SHOULDER_LEFT, nbttagcompound);
    }

    public NBTTagCompound getShoulderEntityRight() {
        return (NBTTagCompound) this.entityData.get(EntityHuman.DATA_SHOULDER_RIGHT);
    }

    public void setShoulderEntityRight(NBTTagCompound nbttagcompound) {
        this.entityData.set(EntityHuman.DATA_SHOULDER_RIGHT, nbttagcompound);
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float) (1.0D / this.getAttributeValue(GenericAttributes.ATTACK_SPEED) * 20.0D);
    }

    public float getAttackStrengthScale(float f) {
        return MathHelper.clamp(((float) this.attackStrengthTicker + f) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public ItemCooldown getCooldowns() {
        return this.cooldowns;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
    }

    public float getLuck() {
        return (float) this.getAttributeValue(GenericAttributes.LUCK);
    }

    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.getPermissionLevel() >= 2;
    }

    @Override
    public boolean canTakeItem(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return this.getItemBySlot(enumitemslot).isEmpty();
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return (EntitySize) EntityHuman.POSES.getOrDefault(entitypose, EntityHuman.STANDING_DIMENSIONS);
    }

    @Override
    public ImmutableList<EntityPose> getDismountPoses() {
        return ImmutableList.of(EntityPose.STANDING, EntityPose.CROUCHING, EntityPose.SWIMMING);
    }

    @Override
    public ItemStack getProjectile(ItemStack itemstack) {
        if (!(itemstack.getItem() instanceof ItemProjectileWeapon)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((ItemProjectileWeapon) itemstack.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack1 = ItemProjectileWeapon.getHeldProjectile(this, predicate);

            if (!itemstack1.isEmpty()) {
                return itemstack1;
            } else {
                predicate = ((ItemProjectileWeapon) itemstack.getItem()).getAllSupportedProjectiles();

                for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                    ItemStack itemstack2 = this.inventory.getItem(i);

                    if (predicate.test(itemstack2)) {
                        return itemstack2;
                    }
                }

                return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
            }
        }
    }

    @Override
    public ItemStack eat(World world, ItemStack itemstack) {
        this.getFoodData().eat(itemstack.getItem(), itemstack);
        this.awardStat(StatisticList.ITEM_USED.get(itemstack.getItem()));
        world.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        if (this instanceof EntityPlayer) {
            CriterionTriggers.CONSUME_ITEM.trigger((EntityPlayer) this, itemstack);
        }

        return super.eat(world, itemstack);
    }

    @Override
    protected boolean shouldRemoveSoulSpeed(IBlockData iblockdata) {
        return this.abilities.flying || super.shouldRemoveSoulSpeed(iblockdata);
    }

    @Override
    public Vec3D getRopeHoldPosition(float f) {
        double d0 = 0.22D * (this.getMainArm() == EnumMainHand.RIGHT ? -1.0D : 1.0D);
        float f1 = MathHelper.lerp(f * 0.5F, this.getXRot(), this.xRotO) * 0.017453292F;
        float f2 = MathHelper.lerp(f, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
        double d1;

        if (!this.isFallFlying() && !this.isAutoSpinAttack()) {
            if (this.isVisuallySwimming()) {
                return this.getPosition(f).add((new Vec3D(d0, 0.2D, -0.15D)).xRot(-f1).yRot(-f2));
            } else {
                double d2 = this.getBoundingBox().getYsize() - 1.0D;

                d1 = this.isCrouching() ? -0.2D : 0.07D;
                return this.getPosition(f).add((new Vec3D(d0, d2, d1)).yRot(-f2));
            }
        } else {
            Vec3D vec3d = this.getViewVector(f);
            Vec3D vec3d1 = this.getDeltaMovement();

            d1 = vec3d1.horizontalDistanceSqr();
            double d3 = vec3d.horizontalDistanceSqr();
            float f3;

            if (d1 > 0.0D && d3 > 0.0D) {
                double d4 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / Math.sqrt(d1 * d3);
                double d5 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;

                f3 = (float) (Math.signum(d5) * Math.acos(d4));
            } else {
                f3 = 0.0F;
            }

            return this.getPosition(f).add((new Vec3D(d0, -0.11D, 0.85D)).zRot(-f3).xRot(-f1).yRot(-f2));
        }
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    public boolean isScoping() {
        return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(Optional<GlobalPos> optional) {
        this.lastDeathLocation = optional;
    }

    @Override
    public float getHurtDir() {
        return this.hurtDir;
    }

    @Override
    public void animateHurt(float f) {
        super.animateHurt(f);
        this.hurtDir = f;
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    protected float getFlyingSpeed() {
        return this.abilities.flying && !this.isPassenger() ? (this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0F : this.abilities.getFlyingSpeed()) : (this.isSprinting() ? 0.025999999F : 0.02F);
    }

    public static enum EnumBedResult {

        NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW(IChatBaseComponent.translatable("block.minecraft.bed.no_sleep")), TOO_FAR_AWAY(IChatBaseComponent.translatable("block.minecraft.bed.too_far_away")), OBSTRUCTED(IChatBaseComponent.translatable("block.minecraft.bed.obstructed")), OTHER_PROBLEM, NOT_SAFE(IChatBaseComponent.translatable("block.minecraft.bed.not_safe"));

        @Nullable
        private final IChatBaseComponent message;

        private EnumBedResult() {
            this.message = null;
        }

        private EnumBedResult(IChatBaseComponent ichatbasecomponent) {
            this.message = ichatbasecomponent;
        }

        @Nullable
        public IChatBaseComponent getMessage() {
            return this.message;
        }
    }
}
