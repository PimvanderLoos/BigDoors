package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
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
import net.minecraft.tags.Tag;
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
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.food.FoodMetaData;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerPlayer;
import net.minecraft.world.inventory.InventoryEnderChest;
import net.minecraft.world.item.ItemAxe;
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
import net.minecraft.world.level.ICollisionAccess;
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
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;

public abstract class EntityHuman extends EntityLiving {

    public static final String UUID_PREFIX_OFFLINE_PLAYER = "OfflinePlayer:";
    public static final int MAX_NAME_LENGTH = 16;
    public static final int MAX_HEALTH = 20;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;
    public static final int ENDER_SLOT_OFFSET = 200;
    public static final float CROUCH_BB_HEIGHT = 1.5F;
    public static final float SWIMMING_BB_WIDTH = 0.6F;
    public static final float SWIMMING_BB_HEIGHT = 0.6F;
    public static final float DEFAULT_EYE_HEIGHT = 1.62F;
    public static final EntitySize STANDING_DIMENSIONS = EntitySize.b(0.6F, 1.8F);
    private static final Map<EntityPose, EntitySize> POSES = ImmutableMap.builder().put(EntityPose.STANDING, EntityHuman.STANDING_DIMENSIONS).put(EntityPose.SLEEPING, EntityHuman.SLEEPING_DIMENSIONS).put(EntityPose.FALL_FLYING, EntitySize.b(0.6F, 0.6F)).put(EntityPose.SWIMMING, EntitySize.b(0.6F, 0.6F)).put(EntityPose.SPIN_ATTACK, EntitySize.b(0.6F, 0.6F)).put(EntityPose.CROUCHING, EntitySize.b(0.6F, 1.5F)).put(EntityPose.DYING, EntitySize.c(0.2F, 0.2F)).build();
    private static final int FLY_ACHIEVEMENT_SPEED = 25;
    private static final DataWatcherObject<Float> DATA_PLAYER_ABSORPTION_ID = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_SCORE_ID = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.INT);
    protected static final DataWatcherObject<Byte> DATA_PLAYER_MODE_CUSTOMISATION = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.BYTE);
    protected static final DataWatcherObject<Byte> DATA_PLAYER_MAIN_HAND = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.BYTE);
    protected static final DataWatcherObject<NBTTagCompound> DATA_SHOULDER_LEFT = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.COMPOUND_TAG);
    protected static final DataWatcherObject<NBTTagCompound> DATA_SHOULDER_RIGHT = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.COMPOUND_TAG);
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
    protected int enchantmentSeed;
    protected final float defaultFlySpeed = 0.02F;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand;
    private final ItemCooldown cooldowns;
    @Nullable
    public EntityFishingHook fishing;

    public EntityHuman(World world, BlockPosition blockposition, float f, GameProfile gameprofile) {
        super(EntityTypes.PLAYER, world);
        this.lastItemInMainHand = ItemStack.EMPTY;
        this.cooldowns = this.j();
        this.a_(a(gameprofile));
        this.gameProfile = gameprofile;
        this.inventoryMenu = new ContainerPlayer(this.inventory, !world.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D, f, 0.0F);
        this.rotOffs = 180.0F;
    }

    public boolean a(World world, BlockPosition blockposition, EnumGamemode enumgamemode) {
        if (!enumgamemode.e()) {
            return false;
        } else if (enumgamemode == EnumGamemode.SPECTATOR) {
            return true;
        } else if (this.fv()) {
            return false;
        } else {
            ItemStack itemstack = this.getItemInMainHand();

            return itemstack.isEmpty() || !itemstack.a(world.r(), new ShapeDetectorBlock(world, blockposition, false));
        }
    }

    public static AttributeProvider.Builder eY() {
        return EntityLiving.dq().a(GenericAttributes.ATTACK_DAMAGE, 1.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.10000000149011612D).a(GenericAttributes.ATTACK_SPEED).a(GenericAttributes.LUCK);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityHuman.DATA_PLAYER_ABSORPTION_ID, 0.0F);
        this.entityData.register(EntityHuman.DATA_SCORE_ID, 0);
        this.entityData.register(EntityHuman.DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0);
        this.entityData.register(EntityHuman.DATA_PLAYER_MAIN_HAND, (byte) 1);
        this.entityData.register(EntityHuman.DATA_SHOULDER_LEFT, new NBTTagCompound());
        this.entityData.register(EntityHuman.DATA_SHOULDER_RIGHT, new NBTTagCompound());
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
                this.wakeup(false, true);
            }
        } else if (this.sleepCounter > 0) {
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }

        this.fc();
        super.tick();
        if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.canUse(this)) {
            this.closeInventory();
            this.containerMenu = this.inventoryMenu;
        }

        this.q();
        if (!this.level.isClientSide) {
            this.foodData.a(this);
            this.a(StatisticList.PLAY_TIME);
            this.a(StatisticList.TOTAL_WORLD_TIME);
            if (this.isAlive()) {
                this.a(StatisticList.TIME_SINCE_DEATH);
            }

            if (this.bG()) {
                this.a(StatisticList.CROUCH_TIME);
            }

            if (!this.isSleeping()) {
                this.a(StatisticList.TIME_SINCE_REST);
            }
        }

        int i = 29999999;
        double d0 = MathHelper.a(this.locX(), -2.9999999E7D, 2.9999999E7D);
        double d1 = MathHelper.a(this.locZ(), -2.9999999E7D, 2.9999999E7D);

        if (d0 != this.locX() || d1 != this.locZ()) {
            this.setPosition(d0, this.locY(), d1);
        }

        ++this.attackStrengthTicker;
        ItemStack itemstack = this.getItemInMainHand();

        if (!ItemStack.matches(this.lastItemInMainHand, itemstack)) {
            if (!ItemStack.d(this.lastItemInMainHand, itemstack)) {
                this.resetAttackCooldown();
            }

            this.lastItemInMainHand = itemstack.cloneItemStack();
        }

        this.p();
        this.cooldowns.a();
        this.fd();
    }

    public boolean eZ() {
        return this.isSneaking();
    }

    protected boolean fa() {
        return this.isSneaking();
    }

    protected boolean fb() {
        return this.isSneaking();
    }

    protected boolean fc() {
        this.wasUnderwater = this.a((Tag) TagsFluid.WATER);
        return this.wasUnderwater;
    }

    private void p() {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

        if (itemstack.a(Items.TURTLE_HELMET) && !this.a((Tag) TagsFluid.WATER)) {
            this.addEffect(new MobEffect(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
        }

    }

    protected ItemCooldown j() {
        return new ItemCooldown();
    }

    private void q() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.locX() - this.xCloak;
        double d1 = this.locY() - this.yCloak;
        double d2 = this.locZ() - this.zCloak;
        double d3 = 10.0D;

        if (d0 > 10.0D) {
            this.xCloak = this.locX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0D) {
            this.zCloak = this.locZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0D) {
            this.yCloak = this.locY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0D) {
            this.xCloak = this.locX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0D) {
            this.zCloak = this.locZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0D) {
            this.yCloak = this.locY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    protected void fd() {
        if (this.c(EntityPose.SWIMMING)) {
            EntityPose entitypose;

            if (this.isGliding()) {
                entitypose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entitypose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                entitypose = EntityPose.SWIMMING;
            } else if (this.isRiptiding()) {
                entitypose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !this.abilities.flying) {
                entitypose = EntityPose.CROUCHING;
            } else {
                entitypose = EntityPose.STANDING;
            }

            EntityPose entitypose1;

            if (!this.isSpectator() && !this.isPassenger() && !this.c(entitypose)) {
                if (this.c(EntityPose.CROUCHING)) {
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
    public int am() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.PLAYER_SWIM;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.PLAYER_SPLASH;
    }

    @Override
    protected SoundEffect getSoundSplashHighSpeed() {
        return SoundEffects.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDefaultPortalCooldown() {
        return 10;
    }

    @Override
    public void playSound(SoundEffect soundeffect, float f, float f1) {
        this.level.playSound(this, this.locX(), this.locY(), this.locZ(), soundeffect, this.getSoundCategory(), f, f1);
    }

    public void a(SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    public int getMaxFireTicks() {
        return 20;
    }

    @Override
    public void a(byte b0) {
        if (b0 == 9) {
            this.s();
        } else if (b0 == 23) {
            this.reducedDebugInfo = false;
        } else if (b0 == 22) {
            this.reducedDebugInfo = true;
        } else if (b0 == 43) {
            this.a((ParticleParam) Particles.CLOUD);
        } else {
            super.a(b0);
        }

    }

    private void a(ParticleParam particleparam) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particleparam, this.d(1.0D), this.da() + 1.0D, this.g(1.0D), d0, d1, d2);
        }

    }

    public void closeInventory() {
        this.containerMenu = this.inventoryMenu;
    }

    @Override
    public void passengerTick() {
        if (!this.level.isClientSide && this.fa() && this.isPassenger()) {
            this.stopRiding();
            this.setSneaking(false);
        } else {
            double d0 = this.locX();
            double d1 = this.locY();
            double d2 = this.locZ();

            super.passengerTick();
            this.oBob = this.bob;
            this.bob = 0.0F;
            this.r(this.locX() - d0, this.locY() - d1, this.locZ() - d2);
        }
    }

    @Override
    protected void doTick() {
        super.doTick();
        this.ei();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public void movementTick() {
        if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }

        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.foodData.c() && this.tickCount % 10 == 0) {
                this.foodData.a(this.foodData.getFoodLevel() + 1);
            }
        }

        this.inventory.j();
        this.oBob = this.bob;
        super.movementTick();
        this.flyingSpeed = 0.02F;
        if (this.isSprinting()) {
            this.flyingSpeed = (float) ((double) this.flyingSpeed + 0.005999999865889549D);
        }

        this.r((float) this.b(GenericAttributes.MOVEMENT_SPEED));
        float f;

        if (this.onGround && !this.dV() && !this.isSwimming()) {
            f = Math.min(0.1F, (float) this.getMot().h());
        } else {
            f = 0.0F;
        }

        this.bob += (f - this.bob) * 0.4F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;

            if (this.isPassenger() && !this.getVehicle().isRemoved()) {
                axisalignedbb = this.getBoundingBox().b(this.getVehicle().getBoundingBox()).grow(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.getBoundingBox().grow(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.level.getEntities(this, axisalignedbb);
            List<Entity> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity.getEntityType() == EntityTypes.EXPERIENCE_ORB) {
                    list1.add(entity);
                } else if (!entity.isRemoved()) {
                    this.c(entity);
                }
            }

            if (!list1.isEmpty()) {
                this.c((Entity) SystemUtils.a((List) list1, this.random));
            }
        }

        this.c(this.getShoulderEntityLeft());
        this.c(this.getShoulderEntityRight());
        if (!this.level.isClientSide && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
            this.releaseShoulderEntities();
        }

    }

    private void c(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && (!nbttagcompound.hasKey("Silent") || !nbttagcompound.getBoolean("Silent")) && this.level.random.nextInt(200) == 0) {
            String s = nbttagcompound.getString("id");

            EntityTypes.a(s).filter((entitytypes) -> {
                return entitytypes == EntityTypes.PARROT;
            }).ifPresent((entitytypes) -> {
                if (!EntityParrot.a(this.level, (Entity) this)) {
                    this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), EntityParrot.a(this.level, this.level.random), this.getSoundCategory(), 1.0F, EntityParrot.a(this.level.random));
                }

            });
        }

    }

    private void c(Entity entity) {
        entity.pickup(this);
    }

    public int getScore() {
        return (Integer) this.entityData.get(EntityHuman.DATA_SCORE_ID);
    }

    public void setScore(int i) {
        this.entityData.set(EntityHuman.DATA_SCORE_ID, i);
    }

    public void addScore(int i) {
        int j = this.getScore();

        this.entityData.set(EntityHuman.DATA_SCORE_ID, j + i);
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.ah();
        if (!this.isSpectator()) {
            this.f(damagesource);
        }

        if (damagesource != null) {
            this.setMot((double) (-MathHelper.cos((this.hurtDir + this.getYRot()) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double) (-MathHelper.sin((this.hurtDir + this.getYRot()) * 0.017453292F) * 0.1F));
        } else {
            this.setMot(0.0D, 0.1D, 0.0D);
        }

        this.a(StatisticList.DEATHS);
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_DEATH));
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST));
        this.extinguish();
        this.a_(false);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.removeCursedItems();
            this.inventory.dropContents();
        }

    }

    protected void removeCursedItems() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && EnchantmentManager.shouldNotDrop(itemstack)) {
                this.inventory.splitWithoutUpdate(i);
            }
        }

    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return damagesource == DamageSource.ON_FIRE ? SoundEffects.PLAYER_HURT_ON_FIRE : (damagesource == DamageSource.DROWN ? SoundEffects.PLAYER_HURT_DROWN : (damagesource == DamageSource.SWEET_BERRY_BUSH ? SoundEffects.PLAYER_HURT_SWEET_BERRY_BUSH : (damagesource == DamageSource.FREEZE ? SoundEffects.PLAYER_HURT_FREEZE : SoundEffects.PLAYER_HURT)));
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PLAYER_DEATH;
    }

    @Nullable
    public EntityItem drop(ItemStack itemstack, boolean flag) {
        return this.a(itemstack, false, flag);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack, boolean flag, boolean flag1) {
        if (itemstack.isEmpty()) {
            return null;
        } else {
            if (this.level.isClientSide) {
                this.swingHand(EnumHand.MAIN_HAND);
            }

            double d0 = this.getHeadY() - 0.30000001192092896D;
            EntityItem entityitem = new EntityItem(this.level, this.locX(), d0, this.locZ(), itemstack);

            entityitem.setPickupDelay(40);
            if (flag1) {
                entityitem.setThrower(this.getUniqueID());
            }

            float f;
            float f1;

            if (flag) {
                f = this.random.nextFloat() * 0.5F;
                f1 = this.random.nextFloat() * 6.2831855F;
                entityitem.setMot((double) (-MathHelper.sin(f1) * f), 0.20000000298023224D, (double) (MathHelper.cos(f1) * f));
            } else {
                f = 0.3F;
                f1 = MathHelper.sin(this.getXRot() * 0.017453292F);
                float f2 = MathHelper.cos(this.getXRot() * 0.017453292F);
                float f3 = MathHelper.sin(this.getYRot() * 0.017453292F);
                float f4 = MathHelper.cos(this.getYRot() * 0.017453292F);
                float f5 = this.random.nextFloat() * 6.2831855F;
                float f6 = 0.02F * this.random.nextFloat();

                entityitem.setMot((double) (-f3 * f2 * 0.3F) + Math.cos((double) f5) * (double) f6, (double) (-f1 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (f4 * f2 * 0.3F) + Math.sin((double) f5) * (double) f6);
            }

            return entityitem;
        }
    }

    public float c(IBlockData iblockdata) {
        float f = this.inventory.a(iblockdata);

        if (f > 1.0F) {
            int i = EnchantmentManager.getDigSpeedEnchantmentLevel(this);
            ItemStack itemstack = this.getItemInMainHand();

            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (MobEffectUtil.a(this)) {
            f *= 1.0F + (float) (MobEffectUtil.b(this) + 1) * 0.2F;
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

        if (this.a((Tag) TagsFluid.WATER) && !EnchantmentManager.h((EntityLiving) this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean hasBlock(IBlockData iblockdata) {
        return !iblockdata.isRequiresSpecialTool() || this.inventory.getItemInHand().canDestroySpecialBlock(iblockdata);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.a_(a(this.gameProfile));
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        this.inventory.b(nbttaglist);
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
        this.foodData.a(nbttagcompound);
        this.abilities.b(nbttagcompound);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double) this.abilities.b());
        if (nbttagcompound.hasKeyOfType("EnderItems", 9)) {
            this.enderChestInventory.a(nbttagcompound.getList("EnderItems", 10));
        }

        if (nbttagcompound.hasKeyOfType("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(nbttagcompound.getCompound("ShoulderEntityLeft"));
        }

        if (nbttagcompound.hasKeyOfType("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(nbttagcompound.getCompound("ShoulderEntityRight"));
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbttagcompound.set("Inventory", this.inventory.a(new NBTTagList()));
        nbttagcompound.setInt("SelectedItemSlot", this.inventory.selected);
        nbttagcompound.setShort("SleepTimer", (short) this.sleepCounter);
        nbttagcompound.setFloat("XpP", this.experienceProgress);
        nbttagcompound.setInt("XpLevel", this.experienceLevel);
        nbttagcompound.setInt("XpTotal", this.totalExperience);
        nbttagcompound.setInt("XpSeed", this.enchantmentSeed);
        nbttagcompound.setInt("Score", this.getScore());
        this.foodData.b(nbttagcompound);
        this.abilities.a(nbttagcompound);
        nbttagcompound.set("EnderItems", this.enderChestInventory.g());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            nbttagcompound.set("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            nbttagcompound.set("ShoulderEntityRight", this.getShoulderEntityRight());
        }

    }

    @Override
    public boolean isInvulnerable(DamageSource damagesource) {
        return super.isInvulnerable(damagesource) ? true : (damagesource == DamageSource.DROWN ? !this.level.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE) : (damagesource.z() ? !this.level.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE) : (damagesource.isFire() ? !this.level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE) : (damagesource == DamageSource.FREEZE ? !this.level.getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE) : false))));
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.abilities.invulnerable && !damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.dV()) {
                return false;
            } else {
                this.releaseShoulderEntities();
                if (damagesource.w()) {
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

                return f == 0.0F ? false : super.damageEntity(damagesource, f);
            }
        }
    }

    @Override
    protected void shieldBlock(EntityLiving entityliving) {
        super.shieldBlock(entityliving);
        if (entityliving.getItemInMainHand().getItem() instanceof ItemAxe) {
            this.r(true);
        }

    }

    @Override
    public boolean dN() {
        return !this.getAbilities().invulnerable && super.dN();
    }

    public boolean a(EntityHuman entityhuman) {
        ScoreboardTeamBase scoreboardteambase = this.getScoreboardTeam();
        ScoreboardTeamBase scoreboardteambase1 = entityhuman.getScoreboardTeam();

        return scoreboardteambase == null ? true : (!scoreboardteambase.isAlly(scoreboardteambase1) ? true : scoreboardteambase.allowFriendlyFire());
    }

    @Override
    protected void damageArmor(DamageSource damagesource, float f) {
        this.inventory.a(damagesource, f, PlayerInventory.ALL_ARMOR_SLOTS);
    }

    @Override
    protected void damageHelmet(DamageSource damagesource, float f) {
        this.inventory.a(damagesource, f, PlayerInventory.HELMET_SLOT_ONLY);
    }

    @Override
    protected void damageShield(float f) {
        if (this.useItem.a(Items.SHIELD)) {
            if (!this.level.isClientSide) {
                this.b(StatisticList.ITEM_USED.b(this.useItem.getItem()));
            }

            if (f >= 3.0F) {
                int i = 1 + MathHelper.d(f);
                EnumHand enumhand = this.getRaisedHand();

                this.useItem.damage(i, this, (entityhuman) -> {
                    entityhuman.broadcastItemBreak(enumhand);
                });
                if (this.useItem.isEmpty()) {
                    if (enumhand == EnumHand.MAIN_HAND) {
                        this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setSlot(EnumItemSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.useItem = ItemStack.EMPTY;
                    this.playSound(SoundEffects.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    @Override
    protected void damageEntity0(DamageSource damagesource, float f) {
        if (!this.isInvulnerable(damagesource)) {
            f = this.applyArmorModifier(damagesource, f);
            f = this.applyMagicModifier(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionHearts(), 0.0F);
            this.setAbsorptionHearts(this.getAbsorptionHearts() - (f1 - f));
            float f2 = f1 - f;

            if (f2 > 0.0F && f2 < 3.4028235E37F) {
                this.a(StatisticList.DAMAGE_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f != 0.0F) {
                this.applyExhaustion(damagesource.getExhaustionCost());
                float f3 = this.getHealth();

                this.setHealth(this.getHealth() - f);
                this.getCombatTracker().trackDamage(damagesource, f3, f);
                if (f < 3.4028235E37F) {
                    this.a(StatisticList.DAMAGE_TAKEN, Math.round(f * 10.0F));
                }

            }
        }
    }

    @Override
    protected boolean du() {
        return !this.abilities.flying && super.du();
    }

    public void openSign(TileEntitySign tileentitysign) {}

    public void a(CommandBlockListenerAbstract commandblocklistenerabstract) {}

    public void a(TileEntityCommand tileentitycommand) {}

    public void a(TileEntityStructure tileentitystructure) {}

    public void a(TileEntityJigsaw tileentityjigsaw) {}

    public void openHorseInventory(EntityHorseAbstract entityhorseabstract, IInventory iinventory) {}

    public OptionalInt openContainer(@Nullable ITileInventory itileinventory) {
        return OptionalInt.empty();
    }

    public void openTrade(int i, MerchantRecipeList merchantrecipelist, int j, int k, boolean flag, boolean flag1) {}

    public void openBook(ItemStack itemstack, EnumHand enumhand) {}

    public EnumInteractionResult a(Entity entity, EnumHand enumhand) {
        if (this.isSpectator()) {
            if (entity instanceof ITileInventory) {
                this.openContainer((ITileInventory) entity);
            }

            return EnumInteractionResult.PASS;
        } else {
            ItemStack itemstack = this.b(enumhand);
            ItemStack itemstack1 = itemstack.cloneItemStack();
            EnumInteractionResult enuminteractionresult = entity.a(this, enumhand);

            if (enuminteractionresult.a()) {
                if (this.abilities.instabuild && itemstack == this.b(enumhand) && itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                return enuminteractionresult;
            } else {
                if (!itemstack.isEmpty() && entity instanceof EntityLiving) {
                    if (this.abilities.instabuild) {
                        itemstack = itemstack1;
                    }

                    EnumInteractionResult enuminteractionresult1 = itemstack.a(this, (EntityLiving) entity, enumhand);

                    if (enuminteractionresult1.a()) {
                        if (itemstack.isEmpty() && !this.abilities.instabuild) {
                            this.a(enumhand, ItemStack.EMPTY);
                        }

                        return enuminteractionresult1;
                    }
                }

                return EnumInteractionResult.PASS;
            }
        }
    }

    @Override
    public double bk() {
        return -0.35D;
    }

    @Override
    public void bo() {
        super.bo();
        this.boardingCooldown = 0;
    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() || this.isSleeping();
    }

    @Override
    public boolean dA() {
        return !this.abilities.flying;
    }

    @Override
    protected Vec3D a(Vec3D vec3d, EnumMoveType enummovetype) {
        if (!this.abilities.flying && (enummovetype == EnumMoveType.SELF || enummovetype == EnumMoveType.PLAYER) && this.fb() && this.v()) {
            double d0 = vec3d.x;
            double d1 = vec3d.z;
            double d2 = 0.05D;

            while (d0 != 0.0D && this.level.getCubes(this, this.getBoundingBox().d(d0, (double) (-this.maxUpStep), 0.0D))) {
                if (d0 < 0.05D && d0 >= -0.05D) {
                    d0 = 0.0D;
                } else if (d0 > 0.0D) {
                    d0 -= 0.05D;
                } else {
                    d0 += 0.05D;
                }
            }

            while (d1 != 0.0D && this.level.getCubes(this, this.getBoundingBox().d(0.0D, (double) (-this.maxUpStep), d1))) {
                if (d1 < 0.05D && d1 >= -0.05D) {
                    d1 = 0.0D;
                } else if (d1 > 0.0D) {
                    d1 -= 0.05D;
                } else {
                    d1 += 0.05D;
                }
            }

            while (d0 != 0.0D && d1 != 0.0D && this.level.getCubes(this, this.getBoundingBox().d(d0, (double) (-this.maxUpStep), d1))) {
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

    private boolean v() {
        return this.onGround || this.fallDistance < this.maxUpStep && !this.level.getCubes(this, this.getBoundingBox().d(0.0D, (double) (this.fallDistance - this.maxUpStep), 0.0D));
    }

    public void attack(Entity entity) {
        if (entity.ca()) {
            if (!entity.r(this)) {
                float f = (float) this.b(GenericAttributes.ATTACK_DAMAGE);
                float f1;

                if (entity instanceof EntityLiving) {
                    f1 = EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
                } else {
                    f1 = EnchantmentManager.a(this.getItemInMainHand(), EnumMonsterType.UNDEFINED);
                }

                float f2 = this.getAttackCooldown(0.5F);

                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                this.resetAttackCooldown();
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    byte b0 = 0;
                    int i = b0 + EnchantmentManager.b((EntityLiving) this);

                    if (this.isSprinting() && flag) {
                        this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.isClimbing() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && entity instanceof EntityLiving;

                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.walkDist - this.walkDistO);

                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.ew()) {
                        ItemStack itemstack = this.b(EnumHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f3 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

                    if (entity instanceof EntityLiving) {
                        f3 = ((EntityLiving) entity).getHealth();
                        if (j > 0 && !entity.isBurning()) {
                            flag4 = true;
                            entity.setOnFire(1);
                        }
                    }

                    Vec3D vec3d = entity.getMot();
                    boolean flag5 = entity.damageEntity(DamageSource.playerAttack(this), f);

                    if (flag5) {
                        if (i > 0) {
                            if (entity instanceof EntityLiving) {
                                ((EntityLiving) entity).p((double) ((float) i * 0.5F), (double) MathHelper.sin(this.getYRot() * 0.017453292F), (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)));
                            } else {
                                entity.i((double) (-MathHelper.sin(this.getYRot() * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.getYRot() * 0.017453292F) * (float) i * 0.5F));
                            }

                            this.setMot(this.getMot().d(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (flag3) {
                            float f4 = 1.0F + EnchantmentManager.a((EntityLiving) this) * f;
                            List<EntityLiving> list = this.level.a(EntityLiving.class, entity.getBoundingBox().grow(1.0D, 0.25D, 1.0D));
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                EntityLiving entityliving = (EntityLiving) iterator.next();

                                if (entityliving != this && entityliving != entity && !this.p(entityliving) && (!(entityliving instanceof EntityArmorStand) || !((EntityArmorStand) entityliving).isMarker()) && this.f((Entity) entityliving) < 9.0D) {
                                    entityliving.p(0.4000000059604645D, (double) MathHelper.sin(this.getYRot() * 0.017453292F), (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)));
                                    entityliving.damageEntity(DamageSource.playerAttack(this), f4);
                                }
                            }

                            this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                            this.fg();
                        }

                        if (entity instanceof EntityPlayer && entity.hurtMarked) {
                            ((EntityPlayer) entity).connection.sendPacket(new PacketPlayOutEntityVelocity(entity));
                            entity.hurtMarked = false;
                            entity.setMot(vec3d);
                        }

                        if (flag2) {
                            this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                            this.a(entity);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                                this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.b(entity);
                        }

                        this.x(entity);
                        if (entity instanceof EntityLiving) {
                            EnchantmentManager.a((EntityLiving) entity, (Entity) this);
                        }

                        EnchantmentManager.b((EntityLiving) this, entity);
                        ItemStack itemstack1 = this.getItemInMainHand();
                        Object object = entity;

                        if (entity instanceof EntityComplexPart) {
                            object = ((EntityComplexPart) entity).parentMob;
                        }

                        if (!this.level.isClientSide && !itemstack1.isEmpty() && object instanceof EntityLiving) {
                            itemstack1.a((EntityLiving) object, this);
                            if (itemstack1.isEmpty()) {
                                this.a(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (entity instanceof EntityLiving) {
                            float f5 = f3 - ((EntityLiving) entity).getHealth();

                            this.a(StatisticList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                entity.setOnFire(j * 4);
                            }

                            if (this.level instanceof WorldServer && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);

                                ((WorldServer) this.level).a(Particles.DAMAGE_INDICATOR, entity.locX(), entity.e(0.5D), entity.locZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.applyExhaustion(0.1F);
                    } else {
                        this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
                        if (flag4) {
                            entity.extinguish();
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void g(EntityLiving entityliving) {
        this.attack(entityliving);
    }

    public void r(boolean flag) {
        float f = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

        if (flag) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getCooldownTracker().setCooldown(Items.SHIELD, 100);
            this.clearActiveItem();
            this.level.broadcastEntityEffect(this, (byte) 30);
        }

    }

    public void a(Entity entity) {}

    public void b(Entity entity) {}

    public void fg() {
        double d0 = (double) (-MathHelper.sin(this.getYRot() * 0.017453292F));
        double d1 = (double) MathHelper.cos(this.getYRot() * 0.017453292F);

        if (this.level instanceof WorldServer) {
            ((WorldServer) this.level).a(Particles.SWEEP_ATTACK, this.locX() + d0, this.e(0.5D), this.locZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }

    }

    public void fh() {}

    @Override
    public void a(Entity.RemovalReason entity_removalreason) {
        super.a(entity_removalreason);
        this.inventoryMenu.b(this);
        if (this.containerMenu != null) {
            this.containerMenu.b(this);
        }

    }

    public boolean fi() {
        return false;
    }

    public GameProfile getProfile() {
        return this.gameProfile;
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    public void a(ItemStack itemstack, ItemStack itemstack1, ClickAction clickaction) {}

    public Either<EntityHuman.EnumBedResult, Unit> sleep(BlockPosition blockposition) {
        this.entitySleep(blockposition);
        this.sleepCounter = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void wakeup(boolean flag, boolean flag1) {
        super.entityWakeup();
        if (this.level instanceof WorldServer && flag1) {
            ((WorldServer) this.level).everyoneSleeping();
        }

        this.sleepCounter = flag ? 0 : 100;
    }

    @Override
    public void entityWakeup() {
        this.wakeup(true, true);
    }

    public static Optional<Vec3D> getBed(WorldServer worldserver, BlockPosition blockposition, float f, boolean flag, boolean flag1) {
        IBlockData iblockdata = worldserver.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block instanceof BlockRespawnAnchor && (Integer) iblockdata.get(BlockRespawnAnchor.CHARGE) > 0 && BlockRespawnAnchor.a((World) worldserver)) {
            Optional<Vec3D> optional = BlockRespawnAnchor.a(EntityTypes.PLAYER, (ICollisionAccess) worldserver, blockposition);

            if (!flag1 && optional.isPresent()) {
                worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRespawnAnchor.CHARGE, (Integer) iblockdata.get(BlockRespawnAnchor.CHARGE) - 1), 3);
            }

            return optional;
        } else if (block instanceof BlockBed && BlockBed.a((World) worldserver)) {
            return BlockBed.a(EntityTypes.PLAYER, worldserver, blockposition, f);
        } else if (!flag) {
            return Optional.empty();
        } else {
            boolean flag2 = block.W_();
            boolean flag3 = worldserver.getType(blockposition.up()).getBlock().W_();

            return flag2 && flag3 ? Optional.of(new Vec3D((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D)) : Optional.empty();
        }
    }

    public boolean isDeeplySleeping() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int fn() {
        return this.sleepCounter;
    }

    public void a(IChatBaseComponent ichatbasecomponent, boolean flag) {}

    public void a(MinecraftKey minecraftkey) {
        this.b(StatisticList.CUSTOM.b(minecraftkey));
    }

    public void a(MinecraftKey minecraftkey, int i) {
        this.a(StatisticList.CUSTOM.b(minecraftkey), i);
    }

    public void b(Statistic<?> statistic) {
        this.a(statistic, 1);
    }

    public void a(Statistic<?> statistic, int i) {}

    public void a(Statistic<?> statistic) {}

    public int discoverRecipes(Collection<IRecipe<?>> collection) {
        return 0;
    }

    public void a(MinecraftKey[] aminecraftkey) {}

    public int undiscoverRecipes(Collection<IRecipe<?>> collection) {
        return 0;
    }

    @Override
    public void jump() {
        super.jump();
        this.a(StatisticList.JUMP);
        if (this.isSprinting()) {
            this.applyExhaustion(0.2F);
        } else {
            this.applyExhaustion(0.05F);
        }

    }

    @Override
    public void g(Vec3D vec3d) {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();
        double d3;

        if (this.isSwimming() && !this.isPassenger()) {
            d3 = this.getLookDirection().y;
            double d4 = d3 < -0.2D ? 0.085D : 0.06D;

            if (d3 <= 0.0D || this.jumping || !this.level.getType(new BlockPosition(this.locX(), this.locY() + 1.0D - 0.1D, this.locZ())).getFluid().isEmpty()) {
                Vec3D vec3d1 = this.getMot();

                this.setMot(vec3d1.add(0.0D, (d3 - vec3d1.y) * d4, 0.0D));
            }
        }

        if (this.abilities.flying && !this.isPassenger()) {
            d3 = this.getMot().y;
            float f = this.flyingSpeed;

            this.flyingSpeed = this.abilities.a() * (float) (this.isSprinting() ? 2 : 1);
            super.g(vec3d);
            Vec3D vec3d2 = this.getMot();

            this.setMot(vec3d2.x, d3 * 0.6D, vec3d2.z);
            this.flyingSpeed = f;
            this.fallDistance = 0.0F;
            this.setFlag(7, false);
        } else {
            super.g(vec3d);
        }

        this.checkMovement(this.locX() - d0, this.locY() - d1, this.locZ() - d2);
    }

    @Override
    public void aQ() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.aQ();
        }

    }

    protected boolean f(BlockPosition blockposition) {
        return !this.level.getType(blockposition).o(this.level, blockposition);
    }

    @Override
    public float ew() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED);
    }

    public void checkMovement(double d0, double d1, double d2) {
        if (!this.isPassenger()) {
            int i;

            if (this.isSwimming()) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.SWIM_ONE_CM, i);
                    this.applyExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.a((Tag) TagsFluid.WATER)) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.WALK_UNDER_WATER_ONE_CM, i);
                    this.applyExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.isInWater()) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.WALK_ON_WATER_ONE_CM, i);
                    this.applyExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.isClimbing()) {
                if (d1 > 0.0D) {
                    this.a(StatisticList.CLIMB_ONE_CM, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    if (this.isSprinting()) {
                        this.a(StatisticList.SPRINT_ONE_CM, i);
                        this.applyExhaustion(0.1F * (float) i * 0.01F);
                    } else if (this.isCrouching()) {
                        this.a(StatisticList.CROUCH_ONE_CM, i);
                        this.applyExhaustion(0.0F * (float) i * 0.01F);
                    } else {
                        this.a(StatisticList.WALK_ONE_CM, i);
                        this.applyExhaustion(0.0F * (float) i * 0.01F);
                    }
                }
            } else if (this.isGliding()) {
                i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                this.a(StatisticList.AVIATE_ONE_CM, i);
            } else {
                i = Math.round((float) Math.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.a(StatisticList.FLY_ONE_CM, i);
                }
            }

        }
    }

    private void r(double d0, double d1, double d2) {
        if (this.isPassenger()) {
            int i = Math.round((float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                Entity entity = this.getVehicle();

                if (entity instanceof EntityMinecartAbstract) {
                    this.a(StatisticList.MINECART_ONE_CM, i);
                } else if (entity instanceof EntityBoat) {
                    this.a(StatisticList.BOAT_ONE_CM, i);
                } else if (entity instanceof EntityPig) {
                    this.a(StatisticList.PIG_ONE_CM, i);
                } else if (entity instanceof EntityHorseAbstract) {
                    this.a(StatisticList.HORSE_ONE_CM, i);
                } else if (entity instanceof EntityStrider) {
                    this.a(StatisticList.STRIDER_ONE_CM, i);
                }
            }
        }

    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        if (this.abilities.mayfly) {
            return false;
        } else {
            if (f >= 2.0F) {
                this.a(StatisticList.FALL_ONE_CM, (int) Math.round((double) f * 100.0D));
            }

            return super.a(f, f1, damagesource);
        }
    }

    public boolean fo() {
        if (!this.onGround && !this.isGliding() && !this.isInWater() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.CHEST);

            if (itemstack.a(Items.ELYTRA) && ItemElytra.d(itemstack)) {
                this.startGliding();
                return true;
            }
        }

        return false;
    }

    public void startGliding() {
        this.setFlag(7, true);
    }

    public void stopGliding() {
        this.setFlag(7, true);
        this.setFlag(7, false);
    }

    @Override
    protected void aT() {
        if (!this.isSpectator()) {
            super.aT();
        }

    }

    @Override
    protected SoundEffect getSoundFall(int i) {
        return i > 4 ? SoundEffects.PLAYER_BIG_FALL : SoundEffects.PLAYER_SMALL_FALL;
    }

    @Override
    public void a(WorldServer worldserver, EntityLiving entityliving) {
        this.b(StatisticList.ENTITY_KILLED.b(entityliving.getEntityType()));
    }

    @Override
    public void a(IBlockData iblockdata, Vec3D vec3d) {
        if (!this.abilities.flying) {
            super.a(iblockdata, vec3d);
        }

    }

    public void giveExp(int i) {
        this.addScore(i);
        this.experienceProgress += (float) i / (float) this.getExpToLevel();
        this.totalExperience = MathHelper.clamp(this.totalExperience + i, 0, Integer.MAX_VALUE);

        while (this.experienceProgress < 0.0F) {
            float f = this.experienceProgress * (float) this.getExpToLevel();

            if (this.experienceLevel > 0) {
                this.levelDown(-1);
                this.experienceProgress = 1.0F + f / (float) this.getExpToLevel();
            } else {
                this.levelDown(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while (this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * (float) this.getExpToLevel();
            this.levelDown(1);
            this.experienceProgress /= (float) this.getExpToLevel();
        }

    }

    public int fr() {
        return this.enchantmentSeed;
    }

    public void enchantDone(ItemStack itemstack, int i) {
        this.experienceLevel -= i;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        this.enchantmentSeed = this.random.nextInt();
    }

    public void levelDown(int i) {
        this.experienceLevel += i;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        if (i > 0 && this.experienceLevel % 5 == 0 && (float) this.lastLevelUpTime < (float) this.tickCount - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float) this.experienceLevel / 30.0F;

            this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
            this.lastLevelUpTime = this.tickCount;
        }

    }

    public int getExpToLevel() {
        return this.experienceLevel >= 30 ? 112 + (this.experienceLevel - 30) * 9 : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
    }

    public void applyExhaustion(float f) {
        if (!this.abilities.invulnerable) {
            if (!this.level.isClientSide) {
                this.foodData.a(f);
            }

        }
    }

    public FoodMetaData getFoodData() {
        return this.foodData;
    }

    public boolean s(boolean flag) {
        return this.abilities.invulnerable || flag || this.foodData.c();
    }

    public boolean fu() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean fv() {
        return this.abilities.mayBuild;
    }

    public boolean a(BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
            ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(this.level, blockposition1, false);

            return itemstack.b(this.level.r(), shapedetectorblock);
        }
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
            int i = this.experienceLevel * 7;

            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    @Override
    protected boolean alwaysGivesExp() {
        return true;
    }

    @Override
    public boolean cn() {
        return true;
    }

    @Override
    protected Entity.MovementEmission aI() {
        return !this.abilities.flying && (!this.onGround || !this.bG()) ? Entity.MovementEmission.ALL : Entity.MovementEmission.NONE;
    }

    public void updateAbilities() {}

    @Override
    public IChatBaseComponent getDisplayName() {
        return new ChatComponentText(this.gameProfile.getName());
    }

    public InventoryEnderChest getEnderChest() {
        return this.enderChestInventory;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.inventory.getItemInHand() : (enumitemslot == EnumItemSlot.OFFHAND ? (ItemStack) this.inventory.offhand.get(0) : (enumitemslot.a() == EnumItemSlot.Function.ARMOR ? (ItemStack) this.inventory.armor.get(enumitemslot.b()) : ItemStack.EMPTY));
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.f(itemstack);
        if (enumitemslot == EnumItemSlot.MAINHAND) {
            this.playEquipSound(itemstack);
            this.inventory.items.set(this.inventory.selected, itemstack);
        } else if (enumitemslot == EnumItemSlot.OFFHAND) {
            this.playEquipSound(itemstack);
            this.inventory.offhand.set(0, itemstack);
        } else if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
            this.playEquipSound(itemstack);
            this.inventory.armor.set(enumitemslot.b(), itemstack);
        }

    }

    public boolean j(ItemStack itemstack) {
        this.playEquipSound(itemstack);
        return this.inventory.pickup(itemstack);
    }

    @Override
    public Iterable<ItemStack> bw() {
        return Lists.newArrayList(new ItemStack[]{this.getItemInMainHand(), this.getItemInOffHand()});
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    public boolean h(NBTTagCompound nbttagcompound) {
        if (!this.isPassenger() && this.onGround && !this.isInWater() && !this.isInPowderSnow) {
            if (this.getShoulderEntityLeft().isEmpty()) {
                this.setShoulderEntityLeft(nbttagcompound);
                this.timeEntitySatOnShoulder = this.level.getTime();
                return true;
            } else if (this.getShoulderEntityRight().isEmpty()) {
                this.setShoulderEntityRight(nbttagcompound);
                this.timeEntitySatOnShoulder = this.level.getTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void releaseShoulderEntities() {
        if (this.timeEntitySatOnShoulder + 20L < this.level.getTime()) {
            this.spawnEntityFromShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new NBTTagCompound());
            this.spawnEntityFromShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new NBTTagCompound());
        }

    }

    private void spawnEntityFromShoulder(NBTTagCompound nbttagcompound) {
        if (!this.level.isClientSide && !nbttagcompound.isEmpty()) {
            EntityTypes.a(nbttagcompound, this.level).ifPresent((entity) -> {
                if (entity instanceof EntityTameableAnimal) {
                    ((EntityTameableAnimal) entity).setOwnerUUID(this.uuid);
                }

                entity.setPosition(this.locX(), this.locY() + 0.699999988079071D, this.locZ());
                ((WorldServer) this.level).addEntitySerialized(entity);
            });
        }

    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean ck() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.level.getScoreboard();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        IChatMutableComponent ichatmutablecomponent = ScoreboardTeam.a(this.getScoreboardTeam(), this.getDisplayName());

        return this.a(ichatmutablecomponent);
    }

    private IChatMutableComponent a(IChatMutableComponent ichatmutablecomponent) {
        String s = this.getProfile().getName();

        return ichatmutablecomponent.format((chatmodifier) -> {
            return chatmodifier.setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/tell " + s + " ")).setChatHoverable(this.cq()).setInsertion(s);
        });
    }

    @Override
    public String getName() {
        return this.getProfile().getName();
    }

    @Override
    public float b(EntityPose entitypose, EntitySize entitysize) {
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
    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.getDataWatcher().set(EntityHuman.DATA_PLAYER_ABSORPTION_ID, f);
    }

    @Override
    public float getAbsorptionHearts() {
        return (Float) this.getDataWatcher().get(EntityHuman.DATA_PLAYER_ABSORPTION_ID);
    }

    public static UUID a(GameProfile gameprofile) {
        UUID uuid = gameprofile.getId();

        if (uuid == null) {
            uuid = getOfflineUUID(gameprofile.getName());
        }

        return uuid;
    }

    public static UUID getOfflineUUID(String s) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + s).getBytes(StandardCharsets.UTF_8));
    }

    public boolean a(PlayerModelPart playermodelpart) {
        return ((Byte) this.getDataWatcher().get(EntityHuman.DATA_PLAYER_MODE_CUSTOMISATION) & playermodelpart.a()) == playermodelpart.a();
    }

    @Override
    public SlotAccess k(int i) {
        if (i >= 0 && i < this.inventory.items.size()) {
            return SlotAccess.a(this.inventory, i);
        } else {
            int j = i - 200;

            return j >= 0 && j < this.enderChestInventory.getSize() ? SlotAccess.a(this.enderChestInventory, j) : super.k(i);
        }
    }

    public boolean fz() {
        return this.reducedDebugInfo;
    }

    public void t(boolean flag) {
        this.reducedDebugInfo = flag;
    }

    @Override
    public void setFireTicks(int i) {
        super.setFireTicks(this.abilities.invulnerable ? Math.min(i, 1) : i);
    }

    @Override
    public EnumMainHand getMainHand() {
        return (Byte) this.entityData.get(EntityHuman.DATA_PLAYER_MAIN_HAND) == 0 ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public void a(EnumMainHand enummainhand) {
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

    public float fC() {
        return (float) (1.0D / this.b(GenericAttributes.ATTACK_SPEED) * 20.0D);
    }

    public float getAttackCooldown(float f) {
        return MathHelper.a(((float) this.attackStrengthTicker + f) / this.fC(), 0.0F, 1.0F);
    }

    public void resetAttackCooldown() {
        this.attackStrengthTicker = 0;
    }

    public ItemCooldown getCooldownTracker() {
        return this.cooldowns;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return !this.abilities.flying && !this.isGliding() ? super.getBlockSpeedFactor() : 1.0F;
    }

    public float fF() {
        return (float) this.b(GenericAttributes.LUCK);
    }

    public boolean isCreativeAndOp() {
        return this.abilities.instabuild && this.y() >= 2;
    }

    @Override
    public boolean g(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return this.getEquipment(enumitemslot).isEmpty();
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return (EntitySize) EntityHuman.POSES.getOrDefault(entitypose, EntityHuman.STANDING_DIMENSIONS);
    }

    @Override
    public ImmutableList<EntityPose> eS() {
        return ImmutableList.of(EntityPose.STANDING, EntityPose.CROUCHING, EntityPose.SWIMMING);
    }

    @Override
    public ItemStack h(ItemStack itemstack) {
        if (!(itemstack.getItem() instanceof ItemProjectileWeapon)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((ItemProjectileWeapon) itemstack.getItem()).e();
            ItemStack itemstack1 = ItemProjectileWeapon.a((EntityLiving) this, predicate);

            if (!itemstack1.isEmpty()) {
                return itemstack1;
            } else {
                predicate = ((ItemProjectileWeapon) itemstack.getItem()).b();

                for (int i = 0; i < this.inventory.getSize(); ++i) {
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
    public ItemStack a(World world, ItemStack itemstack) {
        this.getFoodData().a(itemstack.getItem(), itemstack);
        this.b(StatisticList.ITEM_USED.b(itemstack.getItem()));
        world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        if (this instanceof EntityPlayer) {
            CriterionTriggers.CONSUME_ITEM.a((EntityPlayer) this, itemstack);
        }

        return super.a(world, itemstack);
    }

    @Override
    protected boolean b(IBlockData iblockdata) {
        return this.abilities.flying || super.b(iblockdata);
    }

    @Override
    public Vec3D n(float f) {
        double d0 = 0.22D * (this.getMainHand() == EnumMainHand.RIGHT ? -1.0D : 1.0D);
        float f1 = MathHelper.h(f * 0.5F, this.getXRot(), this.xRotO) * 0.017453292F;
        float f2 = MathHelper.h(f, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
        double d1;

        if (!this.isGliding() && !this.isRiptiding()) {
            if (this.bL()) {
                return this.k(f).e((new Vec3D(d0, 0.2D, -0.15D)).a(-f1).b(-f2));
            } else {
                double d2 = this.getBoundingBox().c() - 1.0D;

                d1 = this.isCrouching() ? -0.2D : 0.07D;
                return this.k(f).e((new Vec3D(d0, d2, d1)).b(-f2));
            }
        } else {
            Vec3D vec3d = this.e(f);
            Vec3D vec3d1 = this.getMot();

            d1 = vec3d1.i();
            double d3 = vec3d.i();
            float f3;

            if (d1 > 0.0D && d3 > 0.0D) {
                double d4 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / Math.sqrt(d1 * d3);
                double d5 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;

                f3 = (float) (Math.signum(d5) * Math.acos(d4));
            } else {
                f3 = 0.0F;
            }

            return this.k(f).e((new Vec3D(d0, -0.11D, 0.85D)).c(-f3).a(-f1).b(-f2));
        }
    }

    @Override
    public boolean dn() {
        return true;
    }

    public boolean fH() {
        return this.isHandRaised() && this.getActiveItem().a(Items.SPYGLASS);
    }

    @Override
    public boolean dm() {
        return false;
    }

    public static enum EnumBedResult {

        NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW(new ChatMessage("block.minecraft.bed.no_sleep")), TOO_FAR_AWAY(new ChatMessage("block.minecraft.bed.too_far_away")), OBSTRUCTED(new ChatMessage("block.minecraft.bed.obstructed")), OTHER_PROBLEM, NOT_SAFE(new ChatMessage("block.minecraft.bed.not_safe"));

        @Nullable
        private final IChatBaseComponent message;

        private EnumBedResult() {
            this.message = null;
        }

        private EnumBedResult(IChatBaseComponent ichatbasecomponent) {
            this.message = ichatbasecomponent;
        }

        @Nullable
        public IChatBaseComponent a() {
            return this.message;
        }
    }
}
