package net.minecraft.world.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsEntity;
import net.minecraft.tags.TagsFluid;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.damagesource.CombatMath;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityBird;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.food.FoodInfo;
import net.minecraft.world.item.EnumAnimation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemElytra;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.enchantment.EnchantmentFrostWalker;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.BlockHoney;
import net.minecraft.world.level.block.BlockLadder;
import net.minecraft.world.level.block.BlockSkullAbstract;
import net.minecraft.world.level.block.BlockTrapdoor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardTeam;
import org.apache.logging.log4j.Logger;

public abstract class EntityLiving extends Entity {

    private static final UUID SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final UUID SPEED_MODIFIER_SOUL_SPEED_UUID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
    private static final UUID SPEED_MODIFIER_POWDER_SNOW_UUID = UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce");
    private static final AttributeModifier SPEED_MODIFIER_SPRINTING = new AttributeModifier(EntityLiving.SPEED_MODIFIER_SPRINTING_UUID, "Sprinting speed boost", 0.30000001192092896D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    public static final int HAND_SLOTS = 2;
    public static final int ARMOR_SLOTS = 4;
    public static final int EQUIPMENT_SLOT_OFFSET = 98;
    public static final int ARMOR_SLOT_OFFSET = 100;
    public static final int SWING_DURATION = 6;
    public static final int PLAYER_HURT_EXPERIENCE_TIME = 100;
    private static final int DAMAGE_SOURCE_TIMEOUT = 40;
    public static final double MIN_MOVEMENT_DISTANCE = 0.003D;
    public static final double DEFAULT_BASE_GRAVITY = 0.08D;
    public static final int DEATH_DURATION = 20;
    private static final int WAIT_TICKS_BEFORE_ITEM_USE_EFFECTS = 7;
    private static final int TICKS_PER_ELYTRA_FREE_FALL_EVENT = 10;
    private static final int FREE_FALL_EVENTS_PER_ELYTRA_BREAK = 2;
    public static final int USE_ITEM_INTERVAL = 4;
    private static final double MAX_LINE_OF_SIGHT_TEST_RANGE = 128.0D;
    protected static final int LIVING_ENTITY_FLAG_IS_USING = 1;
    protected static final int LIVING_ENTITY_FLAG_OFF_HAND = 2;
    protected static final int LIVING_ENTITY_FLAG_SPIN_ATTACK = 4;
    protected static final DataWatcherObject<Byte> DATA_LIVING_ENTITY_FLAGS = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.BYTE);
    public static final DataWatcherObject<Float> DATA_HEALTH_ID = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_EFFECT_COLOR_ID = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_EFFECT_AMBIENCE_ID = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.BOOLEAN);
    public static final DataWatcherObject<Integer> DATA_ARROW_COUNT_ID = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_STINGER_COUNT_ID = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Optional<BlockPosition>> SLEEPING_POS_ID = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.OPTIONAL_BLOCK_POS);
    protected static final float DEFAULT_EYE_HEIGHT = 1.74F;
    protected static final EntitySize SLEEPING_DIMENSIONS = EntitySize.c(0.2F, 0.2F);
    public static final float EXTRA_RENDER_CULLING_SIZE_WITH_BIG_HAT = 0.5F;
    private final AttributeMapBase attributes;
    public CombatTracker combatTracker = new CombatTracker(this);
    public final Map<MobEffectList, MobEffect> activeEffects = Maps.newHashMap();
    private final NonNullList<ItemStack> lastHandItemStacks;
    private final NonNullList<ItemStack> lastArmorItemStacks;
    public boolean swinging;
    private boolean discardFriction;
    public EnumHand swingingArm;
    public int swingTime;
    public int removeArrowTime;
    public int removeStingerTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir;
    public int deathTime;
    public float oAttackAnim;
    public float attackAnim;
    protected int attackStrengthTicker;
    public float animationSpeedOld;
    public float animationSpeed;
    public float animationPosition;
    public int invulnerableDuration;
    public final float timeOffs;
    public final float rotA;
    public float yBodyRot;
    public float yBodyRotO;
    public float yHeadRot;
    public float yHeadRotO;
    public float flyingSpeed;
    @Nullable
    public EntityHuman lastHurtByPlayer;
    protected int lastHurtByPlayerTime;
    protected boolean dead;
    protected int noActionTime;
    protected float oRun;
    protected float run;
    protected float animStep;
    protected float animStepO;
    protected float rotOffs;
    protected int deathScore;
    public float lastHurt;
    protected boolean jumping;
    public float xxa;
    public float yya;
    public float zza;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    protected double lyHeadRot;
    protected int lerpHeadSteps;
    public boolean effectsDirty;
    @Nullable
    public EntityLiving lastHurtByMob;
    public int lastHurtByMobTimestamp;
    private EntityLiving lastHurtMob;
    private int lastHurtMobTimestamp;
    private float speed;
    private int noJumpDelay;
    private float absorptionAmount;
    protected ItemStack useItem;
    protected int useItemRemaining;
    protected int fallFlyTicks;
    private BlockPosition lastPos;
    private Optional<BlockPosition> lastClimbablePos;
    @Nullable
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    protected int autoSpinAttackTicks;
    private float swimAmount;
    private float swimAmountO;
    protected BehaviorController<?> brain;

    protected EntityLiving(EntityTypes<? extends EntityLiving> entitytypes, World world) {
        super(entitytypes, world);
        this.lastHandItemStacks = NonNullList.a(2, ItemStack.EMPTY);
        this.lastArmorItemStacks = NonNullList.a(4, ItemStack.EMPTY);
        this.discardFriction = false;
        this.invulnerableDuration = 20;
        this.flyingSpeed = 0.02F;
        this.effectsDirty = true;
        this.useItem = ItemStack.EMPTY;
        this.lastClimbablePos = Optional.empty();
        this.attributes = new AttributeMapBase(AttributeDefaults.a(entitytypes));
        this.setHealth(this.getMaxHealth());
        this.blocksBuilding = true;
        this.rotA = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.ah();
        this.timeOffs = (float) Math.random() * 12398.0F;
        this.setYRot((float) (Math.random() * 6.2831854820251465D));
        this.yHeadRot = this.getYRot();
        this.maxUpStep = 0.6F;
        DynamicOpsNBT dynamicopsnbt = DynamicOpsNBT.INSTANCE;

        this.brain = this.a(new Dynamic(dynamicopsnbt, (NBTBase) dynamicopsnbt.createMap((Map) ImmutableMap.of(dynamicopsnbt.createString("memories"), (NBTBase) dynamicopsnbt.emptyMap()))));
    }

    public BehaviorController<?> getBehaviorController() {
        return this.brain;
    }

    protected BehaviorController.b<?> dp() {
        return BehaviorController.a((Collection) ImmutableList.of(), (Collection) ImmutableList.of());
    }

    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return this.dp().a(dynamic);
    }

    @Override
    public void killEntity() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean a(EntityTypes<?> entitytypes) {
        return true;
    }

    @Override
    protected void initDatawatcher() {
        this.entityData.register(EntityLiving.DATA_LIVING_ENTITY_FLAGS, (byte) 0);
        this.entityData.register(EntityLiving.DATA_EFFECT_COLOR_ID, 0);
        this.entityData.register(EntityLiving.DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.register(EntityLiving.DATA_ARROW_COUNT_ID, 0);
        this.entityData.register(EntityLiving.DATA_STINGER_COUNT_ID, 0);
        this.entityData.register(EntityLiving.DATA_HEALTH_ID, 1.0F);
        this.entityData.register(EntityLiving.SLEEPING_POS_ID, Optional.empty());
    }

    public static AttributeProvider.Builder dq() {
        return AttributeProvider.a().a(GenericAttributes.MAX_HEALTH).a(GenericAttributes.KNOCKBACK_RESISTANCE).a(GenericAttributes.MOVEMENT_SPEED).a(GenericAttributes.ARMOR).a(GenericAttributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (!this.isInWater()) {
            this.aS();
        }

        if (!this.level.isClientSide && flag && this.fallDistance > 0.0F) {
            this.dv();
            this.dw();
        }

        if (!this.level.isClientSide && this.fallDistance > 3.0F && flag) {
            float f = (float) MathHelper.f(this.fallDistance - 3.0F);

            if (!iblockdata.isAir()) {
                double d1 = Math.min((double) (0.2F + f / 15.0F), 2.5D);
                int i = (int) (150.0D * d1);

                ((WorldServer) this.level).a(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX(), this.locY(), this.locZ(), i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            }
        }

        super.a(d0, flag, iblockdata, blockposition);
    }

    public boolean dr() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    public float a(float f) {
        return MathHelper.h(f, this.swimAmountO, this.swimAmount);
    }

    @Override
    public void entityBaseTick() {
        this.oAttackAnim = this.attackAnim;
        if (this.firstTick) {
            this.getBedPosition().ifPresent(this::a);
        }

        if (this.ds()) {
            this.dt();
        }

        super.entityBaseTick();
        this.level.getMethodProfiler().enter("livingEntityBaseTick");
        boolean flag = this instanceof EntityHuman;

        if (this.isAlive()) {
            if (this.inBlock()) {
                this.damageEntity(DamageSource.IN_WALL, 1.0F);
            } else if (flag && !this.level.getWorldBorder().a(this.getBoundingBox())) {
                double d0 = this.level.getWorldBorder().a((Entity) this) + this.level.getWorldBorder().getDamageBuffer();

                if (d0 < 0.0D) {
                    double d1 = this.level.getWorldBorder().getDamageAmount();

                    if (d1 > 0.0D) {
                        this.damageEntity(DamageSource.IN_WALL, (float) Math.max(1, MathHelper.floor(-d0 * d1)));
                    }
                }
            }
        }

        if (this.isFireProof() || this.level.isClientSide) {
            this.extinguish();
        }

        boolean flag1 = flag && ((EntityHuman) this).getAbilities().invulnerable;

        if (this.isAlive()) {
            if (this.a((Tag) TagsFluid.WATER) && !this.level.getType(new BlockPosition(this.locX(), this.getHeadY(), this.locZ())).a(Blocks.BUBBLE_COLUMN)) {
                if (!this.dr() && !MobEffectUtil.c(this) && !flag1) {
                    this.setAirTicks(this.m(this.getAirTicks()));
                    if (this.getAirTicks() == -20) {
                        this.setAirTicks(0);
                        Vec3D vec3d = this.getMot();

                        for (int i = 0; i < 8; ++i) {
                            double d2 = this.random.nextDouble() - this.random.nextDouble();
                            double d3 = this.random.nextDouble() - this.random.nextDouble();
                            double d4 = this.random.nextDouble() - this.random.nextDouble();

                            this.level.addParticle(Particles.BUBBLE, this.locX() + d2, this.locY() + d3, this.locZ() + d4, vec3d.x, vec3d.y, vec3d.z);
                        }

                        this.damageEntity(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.level.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().bC()) {
                    this.stopRiding();
                }
            } else if (this.getAirTicks() < this.bS()) {
                this.setAirTicks(this.n(this.getAirTicks()));
            }

            if (!this.level.isClientSide) {
                BlockPosition blockposition = this.getChunkCoordinates();

                if (!Objects.equal(this.lastPos, blockposition)) {
                    this.lastPos = blockposition;
                    this.c(blockposition);
                }
            }
        }

        if (this.isAlive() && (this.aN() || this.isInPowderSnow)) {
            if (!this.level.isClientSide && this.wasOnFire) {
                this.at();
            }

            this.extinguish();
        }

        if (this.hurtTime > 0) {
            --this.hurtTime;
        }

        if (this.invulnerableTime > 0 && !(this instanceof EntityPlayer)) {
            --this.invulnerableTime;
        }

        if (this.dV()) {
            this.dB();
        }

        if (this.lastHurtByPlayerTime > 0) {
            --this.lastHurtByPlayerTime;
        } else {
            this.lastHurtByPlayer = null;
        }

        if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
            this.lastHurtMob = null;
        }

        if (this.lastHurtByMob != null) {
            if (!this.lastHurtByMob.isAlive()) {
                this.setLastDamager((EntityLiving) null);
            } else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                this.setLastDamager((EntityLiving) null);
            }
        }

        this.tickPotionEffects();
        this.animStepO = this.animStep;
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.level.getMethodProfiler().exit();
    }

    public boolean ds() {
        return this.tickCount % 5 == 0 && this.getMot().x != 0.0D && this.getMot().z != 0.0D && !this.isSpectator() && EnchantmentManager.j(this) && this.du();
    }

    protected void dt() {
        Vec3D vec3d = this.getMot();

        this.level.addParticle(Particles.SOUL, this.locX() + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), this.locY() + 0.1D, this.locZ() + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), vec3d.x * -0.2D, 0.1D, vec3d.z * -0.2D);
        float f = this.random.nextFloat() * 0.4F + this.random.nextFloat() > 0.9F ? 0.6F : 0.0F;

        this.playSound(SoundEffects.SOUL_ESCAPE, f, 0.6F + this.random.nextFloat() * 0.4F);
    }

    protected boolean du() {
        return this.level.getType(this.ay()).a((Tag) TagsBlock.SOUL_SPEED_BLOCKS);
    }

    @Override
    protected float getBlockSpeedFactor() {
        return this.du() && EnchantmentManager.a(Enchantments.SOUL_SPEED, this) > 0 ? 1.0F : super.getBlockSpeedFactor();
    }

    protected boolean b(IBlockData iblockdata) {
        return !iblockdata.isAir() || this.isGliding();
    }

    protected void dv() {
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributemodifiable != null) {
            if (attributemodifiable.a(EntityLiving.SPEED_MODIFIER_SOUL_SPEED_UUID) != null) {
                attributemodifiable.b(EntityLiving.SPEED_MODIFIER_SOUL_SPEED_UUID);
            }

        }
    }

    protected void dw() {
        if (!this.aU().isAir()) {
            int i = EnchantmentManager.a(Enchantments.SOUL_SPEED, this);

            if (i > 0 && this.du()) {
                AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                if (attributemodifiable == null) {
                    return;
                }

                attributemodifiable.b(new AttributeModifier(EntityLiving.SPEED_MODIFIER_SOUL_SPEED_UUID, "Soul speed boost", (double) (0.03F * (1.0F + (float) i * 0.35F)), AttributeModifier.Operation.ADDITION));
                if (this.getRandom().nextFloat() < 0.04F) {
                    ItemStack itemstack = this.getEquipment(EnumItemSlot.FEET);

                    itemstack.damage(1, this, (entityliving) -> {
                        entityliving.broadcastItemBreak(EnumItemSlot.FEET);
                    });
                }
            }
        }

    }

    protected void dx() {
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributemodifiable != null) {
            if (attributemodifiable.a(EntityLiving.SPEED_MODIFIER_POWDER_SNOW_UUID) != null) {
                attributemodifiable.b(EntityLiving.SPEED_MODIFIER_POWDER_SNOW_UUID);
            }

        }
    }

    protected void dy() {
        if (!this.aU().isAir()) {
            int i = this.getTicksFrozen();

            if (i > 0) {
                AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                if (attributemodifiable == null) {
                    return;
                }

                float f = -0.05F * this.bV();

                attributemodifiable.b(new AttributeModifier(EntityLiving.SPEED_MODIFIER_POWDER_SNOW_UUID, "Powder snow slow", (double) f, AttributeModifier.Operation.ADDITION));
            }
        }

    }

    protected void c(BlockPosition blockposition) {
        int i = EnchantmentManager.a(Enchantments.FROST_WALKER, this);

        if (i > 0) {
            EnchantmentFrostWalker.a(this, this.level, blockposition, i);
        }

        if (this.b(this.aU())) {
            this.dv();
        }

        this.dw();
    }

    public boolean isBaby() {
        return false;
    }

    public float dz() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    protected boolean dA() {
        return true;
    }

    @Override
    public boolean bC() {
        return false;
    }

    protected void dB() {
        ++this.deathTime;
        if (this.deathTime == 20 && !this.level.isClientSide()) {
            this.level.broadcastEntityEffect(this, (byte) 60);
            this.a(Entity.RemovalReason.KILLED);
        }

    }

    protected boolean isDropExperience() {
        return !this.isBaby();
    }

    protected boolean dD() {
        return !this.isBaby();
    }

    protected int m(int i) {
        int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

        return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int n(int i) {
        return Math.min(i + 4, this.bS());
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 0;
    }

    protected boolean alwaysGivesExp() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public EntityLiving getLastDamager() {
        return this.lastHurtByMob;
    }

    public int dH() {
        return this.lastHurtByMobTimestamp;
    }

    public void e(@Nullable EntityHuman entityhuman) {
        this.lastHurtByPlayer = entityhuman;
        this.lastHurtByPlayerTime = this.tickCount;
    }

    public void setLastDamager(@Nullable EntityLiving entityliving) {
        this.lastHurtByMob = entityliving;
        this.lastHurtByMobTimestamp = this.tickCount;
    }

    @Nullable
    public EntityLiving dI() {
        return this.lastHurtMob;
    }

    public int dJ() {
        return this.lastHurtMobTimestamp;
    }

    public void x(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.lastHurtMob = (EntityLiving) entity;
        } else {
            this.lastHurtMob = null;
        }

        this.lastHurtMobTimestamp = this.tickCount;
    }

    public int dK() {
        return this.noActionTime;
    }

    public void o(int i) {
        this.noActionTime = i;
    }

    public boolean dL() {
        return this.discardFriction;
    }

    public void p(boolean flag) {
        this.discardFriction = flag;
    }

    protected void playEquipSound(ItemStack itemstack) {
        SoundEffect soundeffect = itemstack.M();

        if (!itemstack.isEmpty() && soundeffect != null && !this.isSpectator()) {
            this.a(GameEvent.EQUIP);
            this.playSound(soundeffect, 1.0F, 1.0F);
        }
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setFloat("Health", this.getHealth());
        nbttagcompound.setShort("HurtTime", (short) this.hurtTime);
        nbttagcompound.setInt("HurtByTimestamp", this.lastHurtByMobTimestamp);
        nbttagcompound.setShort("DeathTime", (short) this.deathTime);
        nbttagcompound.setFloat("AbsorptionAmount", this.getAbsorptionHearts());
        nbttagcompound.set("Attributes", this.getAttributeMap().c());
        if (!this.activeEffects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.activeEffects.values().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("ActiveEffects", nbttaglist);
        }

        nbttagcompound.setBoolean("FallFlying", this.isGliding());
        this.getBedPosition().ifPresent((blockposition) -> {
            nbttagcompound.setInt("SleepingX", blockposition.getX());
            nbttagcompound.setInt("SleepingY", blockposition.getY());
            nbttagcompound.setInt("SleepingZ", blockposition.getZ());
        });
        DataResult<NBTBase> dataresult = this.brain.a((DynamicOps) DynamicOpsNBT.INSTANCE);
        Logger logger = EntityLiving.LOGGER;

        java.util.Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("Brain", nbtbase);
        });
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        this.setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
        if (nbttagcompound.hasKeyOfType("Attributes", 9) && this.level != null && !this.level.isClientSide) {
            this.getAttributeMap().a(nbttagcompound.getList("Attributes", 10));
        }

        if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                MobEffect mobeffect = MobEffect.b(nbttagcompound1);

                if (mobeffect != null) {
                    this.activeEffects.put(mobeffect.getMobEffect(), mobeffect);
                }
            }
        }

        if (nbttagcompound.hasKeyOfType("Health", 99)) {
            this.setHealth(nbttagcompound.getFloat("Health"));
        }

        this.hurtTime = nbttagcompound.getShort("HurtTime");
        this.deathTime = nbttagcompound.getShort("DeathTime");
        this.lastHurtByMobTimestamp = nbttagcompound.getInt("HurtByTimestamp");
        if (nbttagcompound.hasKeyOfType("Team", 8)) {
            String s = nbttagcompound.getString("Team");
            ScoreboardTeam scoreboardteam = this.level.getScoreboard().getTeam(s);
            boolean flag = scoreboardteam != null && this.level.getScoreboard().addPlayerToTeam(this.getUniqueIDString(), scoreboardteam);

            if (!flag) {
                EntityLiving.LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", s);
            }
        }

        if (nbttagcompound.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

        if (nbttagcompound.hasKeyOfType("SleepingX", 99) && nbttagcompound.hasKeyOfType("SleepingY", 99) && nbttagcompound.hasKeyOfType("SleepingZ", 99)) {
            BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("SleepingX"), nbttagcompound.getInt("SleepingY"), nbttagcompound.getInt("SleepingZ"));

            this.e(blockposition);
            this.entityData.set(EntityLiving.DATA_POSE, EntityPose.SLEEPING);
            if (!this.firstTick) {
                this.a(blockposition);
            }
        }

        if (nbttagcompound.hasKeyOfType("Brain", 10)) {
            this.brain = this.a(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Brain")));
        }

    }

    protected void tickPotionEffects() {
        Iterator iterator = this.activeEffects.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                MobEffect mobeffect = (MobEffect) this.activeEffects.get(mobeffectlist);

                if (!mobeffect.tick(this, () -> {
                    this.a(mobeffect, true, (Entity) null);
                })) {
                    if (!this.level.isClientSide) {
                        iterator.remove();
                        this.a(mobeffect);
                    }
                } else if (mobeffect.getDuration() % 600 == 0) {
                    this.a(mobeffect, false, (Entity) null);
                }
            }
        } catch (ConcurrentModificationException concurrentmodificationexception) {
            ;
        }

        if (this.effectsDirty) {
            if (!this.level.isClientSide) {
                this.C();
                this.n();
            }

            this.effectsDirty = false;
        }

        int i = (Integer) this.entityData.get(EntityLiving.DATA_EFFECT_COLOR_ID);
        boolean flag = (Boolean) this.entityData.get(EntityLiving.DATA_EFFECT_AMBIENCE_ID);

        if (i > 0) {
            boolean flag1;

            if (this.isInvisible()) {
                flag1 = this.random.nextInt(15) == 0;
            } else {
                flag1 = this.random.nextBoolean();
            }

            if (flag) {
                flag1 &= this.random.nextInt(5) == 0;
            }

            if (flag1 && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                this.level.addParticle(flag ? Particles.AMBIENT_ENTITY_EFFECT : Particles.ENTITY_EFFECT, this.d(0.5D), this.da(), this.g(0.5D), d0, d1, d2);
            }
        }

    }

    protected void C() {
        if (this.activeEffects.isEmpty()) {
            this.dP();
            this.setInvisible(false);
        } else {
            Collection<MobEffect> collection = this.activeEffects.values();

            this.entityData.set(EntityLiving.DATA_EFFECT_AMBIENCE_ID, c(collection));
            this.entityData.set(EntityLiving.DATA_EFFECT_COLOR_ID, PotionUtil.a(collection));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }

    }

    private void n() {
        boolean flag = this.isCurrentlyGlowing();

        if (this.getFlag(6) != flag) {
            this.setFlag(6, flag);
        }

    }

    public double y(@Nullable Entity entity) {
        double d0 = 1.0D;

        if (this.bG()) {
            d0 *= 0.8D;
        }

        if (this.isInvisible()) {
            float f = this.en();

            if (f < 0.1F) {
                f = 0.1F;
            }

            d0 *= 0.7D * (double) f;
        }

        if (entity != null) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);
            EntityTypes<?> entitytypes = entity.getEntityType();

            if (entitytypes == EntityTypes.SKELETON && itemstack.a(Items.SKELETON_SKULL) || entitytypes == EntityTypes.ZOMBIE && itemstack.a(Items.ZOMBIE_HEAD) || entitytypes == EntityTypes.CREEPER && itemstack.a(Items.CREEPER_HEAD)) {
                d0 *= 0.5D;
            }
        }

        return d0;
    }

    public boolean c(EntityLiving entityliving) {
        return entityliving instanceof EntityHuman && this.level.getDifficulty() == EnumDifficulty.PEACEFUL ? false : entityliving.dN();
    }

    public boolean a(EntityLiving entityliving, PathfinderTargetCondition pathfindertargetcondition) {
        return pathfindertargetcondition.a(this, entityliving);
    }

    public boolean dN() {
        return !this.isInvulnerable() && this.dO();
    }

    public boolean dO() {
        return !this.isSpectator() && this.isAlive();
    }

    public static boolean c(Collection<MobEffect> collection) {
        Iterator iterator = collection.iterator();

        MobEffect mobeffect;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            mobeffect = (MobEffect) iterator.next();
        } while (mobeffect.isAmbient());

        return false;
    }

    protected void dP() {
        this.entityData.set(EntityLiving.DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.set(EntityLiving.DATA_EFFECT_COLOR_ID, 0);
    }

    public boolean removeAllEffects() {
        if (this.level.isClientSide) {
            return false;
        } else {
            Iterator<MobEffect> iterator = this.activeEffects.values().iterator();

            boolean flag;

            for (flag = false; iterator.hasNext(); flag = true) {
                this.a((MobEffect) iterator.next());
                iterator.remove();
            }

            return flag;
        }
    }

    public Collection<MobEffect> getEffects() {
        return this.activeEffects.values();
    }

    public Map<MobEffectList, MobEffect> dS() {
        return this.activeEffects;
    }

    public boolean hasEffect(MobEffectList mobeffectlist) {
        return this.activeEffects.containsKey(mobeffectlist);
    }

    @Nullable
    public MobEffect getEffect(MobEffectList mobeffectlist) {
        return (MobEffect) this.activeEffects.get(mobeffectlist);
    }

    public final boolean addEffect(MobEffect mobeffect) {
        return this.addEffect(mobeffect, (Entity) null);
    }

    public boolean addEffect(MobEffect mobeffect, @Nullable Entity entity) {
        if (!this.c(mobeffect)) {
            return false;
        } else {
            MobEffect mobeffect1 = (MobEffect) this.activeEffects.get(mobeffect.getMobEffect());

            if (mobeffect1 == null) {
                this.activeEffects.put(mobeffect.getMobEffect(), mobeffect);
                this.a(mobeffect, entity);
                return true;
            } else if (mobeffect1.b(mobeffect)) {
                this.a(mobeffect1, true, entity);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean c(MobEffect mobeffect) {
        if (this.getMonsterType() == EnumMonsterType.UNDEAD) {
            MobEffectList mobeffectlist = mobeffect.getMobEffect();

            if (mobeffectlist == MobEffects.REGENERATION || mobeffectlist == MobEffects.POISON) {
                return false;
            }
        }

        return true;
    }

    public void c(MobEffect mobeffect, @Nullable Entity entity) {
        if (this.c(mobeffect)) {
            MobEffect mobeffect1 = (MobEffect) this.activeEffects.put(mobeffect.getMobEffect(), mobeffect);

            if (mobeffect1 == null) {
                this.a(mobeffect, entity);
            } else {
                this.a(mobeffect, true, entity);
            }

        }
    }

    public boolean dT() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    @Nullable
    public MobEffect c(@Nullable MobEffectList mobeffectlist) {
        return (MobEffect) this.activeEffects.remove(mobeffectlist);
    }

    public boolean removeEffect(MobEffectList mobeffectlist) {
        MobEffect mobeffect = this.c(mobeffectlist);

        if (mobeffect != null) {
            this.a(mobeffect);
            return true;
        } else {
            return false;
        }
    }

    protected void a(MobEffect mobeffect, @Nullable Entity entity) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            mobeffect.getMobEffect().b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void a(MobEffect mobeffect, boolean flag, @Nullable Entity entity) {
        this.effectsDirty = true;
        if (flag && !this.level.isClientSide) {
            MobEffectList mobeffectlist = mobeffect.getMobEffect();

            mobeffectlist.a(this, this.getAttributeMap(), mobeffect.getAmplifier());
            mobeffectlist.b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void a(MobEffect mobeffect) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            mobeffect.getMobEffect().a(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    public void heal(float f) {
        float f1 = this.getHealth();

        if (f1 > 0.0F) {
            this.setHealth(f1 + f);
        }

    }

    public float getHealth() {
        return (Float) this.entityData.get(EntityLiving.DATA_HEALTH_ID);
    }

    public void setHealth(float f) {
        this.entityData.set(EntityLiving.DATA_HEALTH_ID, MathHelper.a(f, 0.0F, this.getMaxHealth()));
    }

    public boolean dV() {
        return this.getHealth() <= 0.0F;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.level.isClientSide) {
            return false;
        } else if (this.dV()) {
            return false;
        } else if (damagesource.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (this.isSleeping() && !this.level.isClientSide) {
                this.entityWakeup();
            }

            this.noActionTime = 0;
            float f1 = f;
            boolean flag = false;
            float f2 = 0.0F;

            if (f > 0.0F && this.applyBlockingModifier(damagesource)) {
                this.damageShield(f);
                f2 = f;
                f = 0.0F;
                if (!damagesource.b()) {
                    Entity entity = damagesource.k();

                    if (entity instanceof EntityLiving) {
                        this.shieldBlock((EntityLiving) entity);
                    }
                }

                flag = true;
            }

            this.animationSpeed = 1.5F;
            boolean flag1 = true;

            if ((float) this.invulnerableTime > 10.0F) {
                if (f <= this.lastHurt) {
                    return false;
                }

                this.damageEntity0(damagesource, f - this.lastHurt);
                this.lastHurt = f;
                flag1 = false;
            } else {
                this.lastHurt = f;
                this.invulnerableTime = 20;
                this.damageEntity0(damagesource, f);
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
            }

            if (damagesource.g() && !this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
                this.damageHelmet(damagesource, f);
                f *= 0.75F;
            }

            this.hurtDir = 0.0F;
            Entity entity1 = damagesource.getEntity();

            if (entity1 != null) {
                if (entity1 instanceof EntityLiving && !damagesource.t()) {
                    this.setLastDamager((EntityLiving) entity1);
                }

                if (entity1 instanceof EntityHuman) {
                    this.lastHurtByPlayerTime = 100;
                    this.lastHurtByPlayer = (EntityHuman) entity1;
                } else if (entity1 instanceof EntityWolf) {
                    EntityWolf entitywolf = (EntityWolf) entity1;

                    if (entitywolf.isTamed()) {
                        this.lastHurtByPlayerTime = 100;
                        EntityLiving entityliving = entitywolf.getOwner();

                        if (entityliving != null && entityliving.getEntityType() == EntityTypes.PLAYER) {
                            this.lastHurtByPlayer = (EntityHuman) entityliving;
                        } else {
                            this.lastHurtByPlayer = null;
                        }
                    }
                }
            }

            if (flag1) {
                if (flag) {
                    this.level.broadcastEntityEffect(this, (byte) 29);
                } else if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).E()) {
                    this.level.broadcastEntityEffect(this, (byte) 33);
                } else {
                    byte b0;

                    if (damagesource == DamageSource.DROWN) {
                        b0 = 36;
                    } else if (damagesource.isFire()) {
                        b0 = 37;
                    } else if (damagesource == DamageSource.SWEET_BERRY_BUSH) {
                        b0 = 44;
                    } else if (damagesource == DamageSource.FREEZE) {
                        b0 = 57;
                    } else {
                        b0 = 2;
                    }

                    this.level.broadcastEntityEffect(this, b0);
                }

                if (damagesource != DamageSource.DROWN && (!flag || f > 0.0F)) {
                    this.velocityChanged();
                }

                if (entity1 != null) {
                    double d0 = entity1.locX() - this.locX();

                    double d1;

                    for (d1 = entity1.locZ() - this.locZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.hurtDir = (float) (MathHelper.d(d1, d0) * 57.2957763671875D - (double) this.getYRot());
                    this.p(0.4000000059604645D, d0, d1);
                } else {
                    this.hurtDir = (float) ((int) (Math.random() * 2.0D) * 180);
                }
            }

            if (this.dV()) {
                if (!this.g(damagesource)) {
                    SoundEffect soundeffect = this.getSoundDeath();

                    if (flag1 && soundeffect != null) {
                        this.playSound(soundeffect, this.getSoundVolume(), this.ep());
                    }

                    this.die(damagesource);
                }
            } else if (flag1) {
                this.d(damagesource);
            }

            boolean flag2 = !flag || f > 0.0F;

            if (flag2) {
                this.lastDamageSource = damagesource;
                this.lastDamageStamp = this.level.getTime();
            }

            if (this instanceof EntityPlayer) {
                CriterionTriggers.ENTITY_HURT_PLAYER.a((EntityPlayer) this, damagesource, f1, f, flag);
                if (f2 > 0.0F && f2 < 3.4028235E37F) {
                    ((EntityPlayer) this).a(StatisticList.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f2 * 10.0F));
                }
            }

            if (entity1 instanceof EntityPlayer) {
                CriterionTriggers.PLAYER_HURT_ENTITY.a((EntityPlayer) entity1, this, damagesource, f1, f, flag);
            }

            return flag2;
        }
    }

    protected void shieldBlock(EntityLiving entityliving) {
        entityliving.e(this);
    }

    protected void e(EntityLiving entityliving) {
        entityliving.p(0.5D, entityliving.locX() - this.locX(), entityliving.locZ() - this.locZ());
    }

    private boolean g(DamageSource damagesource) {
        if (damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            ItemStack itemstack = null;
            EnumHand[] aenumhand = EnumHand.values();
            int i = aenumhand.length;

            for (int j = 0; j < i; ++j) {
                EnumHand enumhand = aenumhand[j];
                ItemStack itemstack1 = this.b(enumhand);

                if (itemstack1.a(Items.TOTEM_OF_UNDYING)) {
                    itemstack = itemstack1.cloneItemStack();
                    itemstack1.subtract(1);
                    break;
                }
            }

            if (itemstack != null) {
                if (this instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) this;

                    entityplayer.b(StatisticList.ITEM_USED.b(Items.TOTEM_OF_UNDYING));
                    CriterionTriggers.USED_TOTEM.a(entityplayer, itemstack);
                }

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffect(MobEffects.REGENERATION, 900, 1));
                this.addEffect(new MobEffect(MobEffects.ABSORPTION, 100, 1));
                this.addEffect(new MobEffect(MobEffects.FIRE_RESISTANCE, 800, 0));
                this.level.broadcastEntityEffect(this, (byte) 35);
            }

            return itemstack != null;
        }
    }

    @Nullable
    public DamageSource dW() {
        if (this.level.getTime() - this.lastDamageStamp > 40L) {
            this.lastDamageSource = null;
        }

        return this.lastDamageSource;
    }

    protected void d(DamageSource damagesource) {
        SoundEffect soundeffect = this.getSoundHurt(damagesource);

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.ep());
        }

    }

    public boolean applyBlockingModifier(DamageSource damagesource) {
        Entity entity = damagesource.k();
        boolean flag = false;

        if (entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.getPierceLevel() > 0) {
                flag = true;
            }
        }

        if (!damagesource.ignoresArmor() && this.isBlocking() && !flag) {
            Vec3D vec3d = damagesource.C();

            if (vec3d != null) {
                Vec3D vec3d1 = this.e(1.0F);
                Vec3D vec3d2 = vec3d.a(this.getPositionVector()).d();

                vec3d2 = new Vec3D(vec3d2.x, 0.0D, vec3d2.z);
                if (vec3d2.b(vec3d1) < 0.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    private void j(ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            if (!this.isSilent()) {
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ITEM_BREAK, this.getSoundCategory(), 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F, false);
            }

            this.a(itemstack, 5);
        }

    }

    public void die(DamageSource damagesource) {
        if (!this.isRemoved() && !this.dead) {
            Entity entity = damagesource.getEntity();
            EntityLiving entityliving = this.getKillingEntity();

            if (this.deathScore >= 0 && entityliving != null) {
                entityliving.a(this, this.deathScore, damagesource);
            }

            if (this.isSleeping()) {
                this.entityWakeup();
            }

            if (!this.level.isClientSide && this.hasCustomName()) {
                EntityLiving.LOGGER.info("Named entity {} died: {}", this, this.getCombatTracker().getDeathMessage().getString());
            }

            this.dead = true;
            this.getCombatTracker().g();
            if (this.level instanceof WorldServer) {
                if (entity != null) {
                    entity.a((WorldServer) this.level, this);
                }

                this.f(damagesource);
                this.f(entityliving);
            }

            this.level.broadcastEntityEffect(this, (byte) 3);
            this.setPose(EntityPose.DYING);
        }
    }

    protected void f(@Nullable EntityLiving entityliving) {
        if (!this.level.isClientSide) {
            boolean flag = false;

            if (entityliving instanceof EntityWither) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    BlockPosition blockposition = this.getChunkCoordinates();
                    IBlockData iblockdata = Blocks.WITHER_ROSE.getBlockData();

                    if (this.level.getType(blockposition).isAir() && iblockdata.canPlace(this.level, blockposition)) {
                        this.level.setTypeAndData(blockposition, iblockdata, 3);
                        flag = true;
                    }
                }

                if (!flag) {
                    EntityItem entityitem = new EntityItem(this.level, this.locX(), this.locY(), this.locZ(), new ItemStack(Items.WITHER_ROSE));

                    this.level.addEntity(entityitem);
                }
            }

        }
    }

    protected void f(DamageSource damagesource) {
        Entity entity = damagesource.getEntity();
        int i;

        if (entity instanceof EntityHuman) {
            i = EnchantmentManager.g((EntityLiving) entity);
        } else {
            i = 0;
        }

        boolean flag = this.lastHurtByPlayerTime > 0;

        if (this.dD() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.a(damagesource, flag);
            this.dropDeathLoot(damagesource, i, flag);
        }

        this.dropInventory();
        this.dropExperience();
    }

    protected void dropInventory() {}

    protected void dropExperience() {
        if (this.level instanceof WorldServer && (this.alwaysGivesExp() || this.lastHurtByPlayerTime > 0 && this.isDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            EntityExperienceOrb.a((WorldServer) this.level, this.getPositionVector(), this.getExpValue(this.lastHurtByPlayer));
        }

    }

    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {}

    public MinecraftKey dZ() {
        return this.getEntityType().j();
    }

    protected void a(DamageSource damagesource, boolean flag) {
        MinecraftKey minecraftkey = this.dZ();
        LootTable loottable = this.level.getMinecraftServer().getLootTableRegistry().getLootTable(minecraftkey);
        LootTableInfo.Builder loottableinfo_builder = this.a(flag, damagesource);

        loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.ENTITY), this::b);
    }

    protected LootTableInfo.Builder a(boolean flag, DamageSource damagesource) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.level)).a(this.random).set(LootContextParameters.THIS_ENTITY, this).set(LootContextParameters.ORIGIN, this.getPositionVector()).set(LootContextParameters.DAMAGE_SOURCE, damagesource).setOptional(LootContextParameters.KILLER_ENTITY, damagesource.getEntity()).setOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damagesource.k());

        if (flag && this.lastHurtByPlayer != null) {
            loottableinfo_builder = loottableinfo_builder.set(LootContextParameters.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).a(this.lastHurtByPlayer.fF());
        }

        return loottableinfo_builder;
    }

    public void p(double d0, double d1, double d2) {
        d0 *= 1.0D - this.b(GenericAttributes.KNOCKBACK_RESISTANCE);
        if (d0 > 0.0D) {
            this.hasImpulse = true;
            Vec3D vec3d = this.getMot();
            Vec3D vec3d1 = (new Vec3D(d1, 0.0D, d2)).d().a(d0);

            this.setMot(vec3d.x / 2.0D - vec3d1.x, this.onGround ? Math.min(0.4D, vec3d.y / 2.0D + d0) : vec3d.y, vec3d.z / 2.0D - vec3d1.z);
        }
    }

    @Nullable
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.GENERIC_HURT;
    }

    @Nullable
    protected SoundEffect getSoundDeath() {
        return SoundEffects.GENERIC_DEATH;
    }

    protected SoundEffect getSoundFall(int i) {
        return i > 4 ? SoundEffects.GENERIC_BIG_FALL : SoundEffects.GENERIC_SMALL_FALL;
    }

    protected SoundEffect d(ItemStack itemstack) {
        return itemstack.K();
    }

    public SoundEffect e(ItemStack itemstack) {
        return itemstack.L();
    }

    @Override
    public void setOnGround(boolean flag) {
        super.setOnGround(flag);
        if (flag) {
            this.lastClimbablePos = Optional.empty();
        }

    }

    public Optional<BlockPosition> ea() {
        return this.lastClimbablePos;
    }

    public boolean isClimbing() {
        if (this.isSpectator()) {
            return false;
        } else {
            BlockPosition blockposition = this.getChunkCoordinates();
            IBlockData iblockdata = this.cS();

            if (iblockdata.a((Tag) TagsBlock.CLIMBABLE)) {
                this.lastClimbablePos = Optional.of(blockposition);
                return true;
            } else if (iblockdata.getBlock() instanceof BlockTrapdoor && this.c(blockposition, iblockdata)) {
                this.lastClimbablePos = Optional.of(blockposition);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean c(BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)) {
            IBlockData iblockdata1 = this.level.getType(blockposition.down());

            if (iblockdata1.a(Blocks.LADDER) && iblockdata1.get(BlockLadder.FACING) == iblockdata.get(BlockTrapdoor.FACING)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAlive() {
        return !this.isRemoved() && this.getHealth() > 0.0F;
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        boolean flag = super.a(f, f1, damagesource);
        int i = this.d(f, f1);

        if (i > 0) {
            this.playSound(this.getSoundFall(i), 1.0F, 1.0F);
            this.playBlockStepSound();
            this.damageEntity(damagesource, (float) i);
            return true;
        } else {
            return flag;
        }
    }

    protected int d(float f, float f1) {
        MobEffect mobeffect = this.getEffect(MobEffects.JUMP);
        float f2 = mobeffect == null ? 0.0F : (float) (mobeffect.getAmplifier() + 1);

        return MathHelper.f((f - 3.0F - f2) * f1);
    }

    protected void playBlockStepSound() {
        if (!this.isSilent()) {
            int i = MathHelper.floor(this.locX());
            int j = MathHelper.floor(this.locY() - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ());
            IBlockData iblockdata = this.level.getType(new BlockPosition(i, j, k));

            if (!iblockdata.isAir()) {
                SoundEffectType soundeffecttype = iblockdata.getStepSound();

                this.playSound(soundeffecttype.getFallSound(), soundeffecttype.getVolume() * 0.5F, soundeffecttype.getPitch() * 0.75F);
            }

        }
    }

    @Override
    public void bv() {
        this.hurtDuration = 10;
        this.hurtTime = this.hurtDuration;
        this.hurtDir = 0.0F;
    }

    public int getArmorStrength() {
        return MathHelper.floor(this.b(GenericAttributes.ARMOR));
    }

    protected void damageArmor(DamageSource damagesource, float f) {}

    protected void damageHelmet(DamageSource damagesource, float f) {}

    protected void damageShield(float f) {}

    protected float applyArmorModifier(DamageSource damagesource, float f) {
        if (!damagesource.ignoresArmor()) {
            this.damageArmor(damagesource, f);
            f = CombatMath.a(f, (float) this.getArmorStrength(), (float) this.b(GenericAttributes.ARMOR_TOUGHNESS));
        }

        return f;
    }

    protected float applyMagicModifier(DamageSource damagesource, float f) {
        if (damagesource.isStarvation()) {
            return f;
        } else {
            int i;

            if (this.hasEffect(MobEffects.DAMAGE_RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
                i = (this.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f1 = f * (float) j;
                float f2 = f;

                f = Math.max(f1 / 25.0F, 0.0F);
                float f3 = f2 - f;

                if (f3 > 0.0F && f3 < 3.4028235E37F) {
                    if (this instanceof EntityPlayer) {
                        ((EntityPlayer) this).a(StatisticList.DAMAGE_RESISTED, Math.round(f3 * 10.0F));
                    } else if (damagesource.getEntity() instanceof EntityPlayer) {
                        ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_RESISTED, Math.round(f3 * 10.0F));
                    }
                }
            }

            if (f <= 0.0F) {
                return 0.0F;
            } else {
                i = EnchantmentManager.a(this.getArmorItems(), damagesource);
                if (i > 0) {
                    f = CombatMath.a(f, (float) i);
                }

                return f;
            }
        }
    }

    protected void damageEntity0(DamageSource damagesource, float f) {
        if (!this.isInvulnerable(damagesource)) {
            f = this.applyArmorModifier(damagesource, f);
            f = this.applyMagicModifier(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionHearts(), 0.0F);
            this.setAbsorptionHearts(this.getAbsorptionHearts() - (f1 - f));
            float f2 = f1 - f;

            if (f2 > 0.0F && f2 < 3.4028235E37F && damagesource.getEntity() instanceof EntityPlayer) {
                ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f != 0.0F) {
                float f3 = this.getHealth();

                this.setHealth(f3 - f);
                this.getCombatTracker().trackDamage(damagesource, f3, f);
                this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
                this.a(GameEvent.ENTITY_DAMAGED, damagesource.getEntity());
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public EntityLiving getKillingEntity() {
        return (EntityLiving) (this.combatTracker.c() != null ? this.combatTracker.c() : (this.lastHurtByPlayer != null ? this.lastHurtByPlayer : (this.lastHurtByMob != null ? this.lastHurtByMob : null)));
    }

    public final float getMaxHealth() {
        return (float) this.b(GenericAttributes.MAX_HEALTH);
    }

    public final int getArrowCount() {
        return (Integer) this.entityData.get(EntityLiving.DATA_ARROW_COUNT_ID);
    }

    public final void setArrowCount(int i) {
        this.entityData.set(EntityLiving.DATA_ARROW_COUNT_ID, i);
    }

    public final int eh() {
        return (Integer) this.entityData.get(EntityLiving.DATA_STINGER_COUNT_ID);
    }

    public final void r(int i) {
        this.entityData.set(EntityLiving.DATA_STINGER_COUNT_ID, i);
    }

    private int q() {
        return MobEffectUtil.a(this) ? 6 - (1 + MobEffectUtil.b(this)) : (this.hasEffect(MobEffects.DIG_SLOWDOWN) ? 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : 6);
    }

    public void swingHand(EnumHand enumhand) {
        this.swingHand(enumhand, false);
    }

    public void swingHand(EnumHand enumhand, boolean flag) {
        if (!this.swinging || this.swingTime >= this.q() / 2 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = enumhand;
            if (this.level instanceof WorldServer) {
                PacketPlayOutAnimation packetplayoutanimation = new PacketPlayOutAnimation(this, enumhand == EnumHand.MAIN_HAND ? 0 : 3);
                ChunkProviderServer chunkproviderserver = ((WorldServer) this.level).getChunkProvider();

                if (flag) {
                    chunkproviderserver.broadcastIncludingSelf(this, packetplayoutanimation);
                } else {
                    chunkproviderserver.broadcast(this, packetplayoutanimation);
                }
            }
        }

    }

    @Override
    public void a(byte b0) {
        switch (b0) {
            case 2:
            case 33:
            case 36:
            case 37:
            case 44:
            case 57:
                this.animationSpeed = 1.5F;
                this.invulnerableTime = 20;
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
                this.hurtDir = 0.0F;
                if (b0 == 33) {
                    this.playSound(SoundEffects.THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                DamageSource damagesource;

                if (b0 == 37) {
                    damagesource = DamageSource.ON_FIRE;
                } else if (b0 == 36) {
                    damagesource = DamageSource.DROWN;
                } else if (b0 == 44) {
                    damagesource = DamageSource.SWEET_BERRY_BUSH;
                } else if (b0 == 57) {
                    damagesource = DamageSource.FREEZE;
                } else {
                    damagesource = DamageSource.GENERIC;
                }

                SoundEffect soundeffect = this.getSoundHurt(damagesource);

                if (soundeffect != null) {
                    this.playSound(soundeffect, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                this.damageEntity(DamageSource.GENERIC, 0.0F);
                this.lastDamageSource = damagesource;
                this.lastDamageStamp = this.level.getTime();
                break;
            case 3:
                SoundEffect soundeffect1 = this.getSoundDeath();

                if (soundeffect1 != null) {
                    this.playSound(soundeffect1, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                if (!(this instanceof EntityHuman)) {
                    this.setHealth(0.0F);
                    this.die(DamageSource.GENERIC);
                }
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 31:
            case 32:
            case 34:
            case 35:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 45:
            case 53:
            case 56:
            case 58:
            case 59:
            default:
                super.a(b0);
                break;
            case 29:
                this.playSound(SoundEffects.SHIELD_BLOCK, 1.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
                break;
            case 30:
                this.playSound(SoundEffects.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
                break;
            case 46:
                boolean flag = true;

                for (int i = 0; i < 128; ++i) {
                    double d0 = (double) i / 127.0D;
                    float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                    float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                    float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                    double d1 = MathHelper.d(d0, this.xo, this.locX()) + (this.random.nextDouble() - 0.5D) * (double) this.getWidth() * 2.0D;
                    double d2 = MathHelper.d(d0, this.yo, this.locY()) + this.random.nextDouble() * (double) this.getHeight();
                    double d3 = MathHelper.d(d0, this.zo, this.locZ()) + (this.random.nextDouble() - 0.5D) * (double) this.getWidth() * 2.0D;

                    this.level.addParticle(Particles.PORTAL, d1, d2, d3, (double) f, (double) f1, (double) f2);
                }

                return;
            case 47:
                this.j(this.getEquipment(EnumItemSlot.MAINHAND));
                break;
            case 48:
                this.j(this.getEquipment(EnumItemSlot.OFFHAND));
                break;
            case 49:
                this.j(this.getEquipment(EnumItemSlot.HEAD));
                break;
            case 50:
                this.j(this.getEquipment(EnumItemSlot.CHEST));
                break;
            case 51:
                this.j(this.getEquipment(EnumItemSlot.LEGS));
                break;
            case 52:
                this.j(this.getEquipment(EnumItemSlot.FEET));
                break;
            case 54:
                BlockHoney.b(this);
                break;
            case 55:
                this.w();
                break;
            case 60:
                this.v();
        }

    }

    private void v() {
        for (int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(Particles.POOF, this.d(1.0D), this.da(), this.g(1.0D), d0, d1, d2);
        }

    }

    private void w() {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.OFFHAND);

        this.setSlot(EnumItemSlot.OFFHAND, this.getEquipment(EnumItemSlot.MAINHAND));
        this.setSlot(EnumItemSlot.MAINHAND, itemstack);
    }

    @Override
    protected void aq() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void ei() {
        int i = this.q();

        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= i) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }

        this.attackAnim = (float) this.swingTime / (float) i;
    }

    @Nullable
    public AttributeModifiable getAttributeInstance(AttributeBase attributebase) {
        return this.getAttributeMap().a(attributebase);
    }

    public double b(AttributeBase attributebase) {
        return this.getAttributeMap().c(attributebase);
    }

    public double c(AttributeBase attributebase) {
        return this.getAttributeMap().d(attributebase);
    }

    public AttributeMapBase getAttributeMap() {
        return this.attributes;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEFINED;
    }

    public ItemStack getItemInMainHand() {
        return this.getEquipment(EnumItemSlot.MAINHAND);
    }

    public ItemStack getItemInOffHand() {
        return this.getEquipment(EnumItemSlot.OFFHAND);
    }

    public boolean a(Item item) {
        return this.b((itemstack) -> {
            return itemstack.a(item);
        });
    }

    public boolean b(Predicate<ItemStack> predicate) {
        return predicate.test(this.getItemInMainHand()) || predicate.test(this.getItemInOffHand());
    }

    public ItemStack b(EnumHand enumhand) {
        if (enumhand == EnumHand.MAIN_HAND) {
            return this.getEquipment(EnumItemSlot.MAINHAND);
        } else if (enumhand == EnumHand.OFF_HAND) {
            return this.getEquipment(EnumItemSlot.OFFHAND);
        } else {
            throw new IllegalArgumentException("Invalid hand " + enumhand);
        }
    }

    public void a(EnumHand enumhand, ItemStack itemstack) {
        if (enumhand == EnumHand.MAIN_HAND) {
            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
        } else {
            if (enumhand != EnumHand.OFF_HAND) {
                throw new IllegalArgumentException("Invalid hand " + enumhand);
            }

            this.setSlot(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    public boolean a(EnumItemSlot enumitemslot) {
        return !this.getEquipment(enumitemslot).isEmpty();
    }

    @Override
    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquipment(EnumItemSlot enumitemslot);

    @Override
    public abstract void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack);

    protected void f(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null) {
            itemstack.getItem().b(nbttagcompound);
        }

    }

    public float en() {
        Iterable<ItemStack> iterable = this.getArmorItems();
        int i = 0;
        int j = 0;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ++i) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (!itemstack.isEmpty()) {
                ++j;
            }
        }

        return i > 0 ? (float) j / (float) i : 0.0F;
    }

    @Override
    public void setSprinting(boolean flag) {
        super.setSprinting(flag);
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributemodifiable.a(EntityLiving.SPEED_MODIFIER_SPRINTING_UUID) != null) {
            attributemodifiable.removeModifier(EntityLiving.SPEED_MODIFIER_SPRINTING);
        }

        if (flag) {
            attributemodifiable.b(EntityLiving.SPEED_MODIFIER_SPRINTING);
        }

    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    public float ep() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isFrozen() {
        return this.dV();
    }

    @Override
    public void collide(Entity entity) {
        if (!this.isSleeping()) {
            super.collide(entity);
        }

    }

    private void a(Entity entity) {
        Vec3D vec3d;

        if (this.isRemoved()) {
            vec3d = this.getPositionVector();
        } else if (!entity.isRemoved() && !this.level.getType(entity.getChunkCoordinates()).a((Tag) TagsBlock.PORTALS)) {
            vec3d = entity.b(this);
        } else {
            double d0 = Math.max(this.locY(), entity.locY());

            vec3d = new Vec3D(this.locX(), d0, this.locZ());
        }

        this.a(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean cn() {
        return this.getCustomNameVisible();
    }

    protected float er() {
        return 0.42F * this.getBlockJumpFactor();
    }

    public double es() {
        return this.hasEffect(MobEffects.JUMP) ? (double) (0.1F * (float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1)) : 0.0D;
    }

    protected void jump() {
        double d0 = (double) this.er() + this.es();
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, d0, vec3d.z);
        if (this.isSprinting()) {
            float f = this.getYRot() * 0.017453292F;

            this.setMot(this.getMot().add((double) (-MathHelper.sin(f) * 0.2F), 0.0D, (double) (MathHelper.cos(f) * 0.2F)));
        }

        this.hasImpulse = true;
    }

    protected void eu() {
        this.setMot(this.getMot().add(0.0D, -0.03999999910593033D, 0.0D));
    }

    protected void c(Tag<FluidType> tag) {
        this.setMot(this.getMot().add(0.0D, 0.03999999910593033D, 0.0D));
    }

    protected float ev() {
        return 0.8F;
    }

    public boolean a(FluidType fluidtype) {
        return false;
    }

    public void g(Vec3D vec3d) {
        if (this.doAITick() || this.cH()) {
            double d0 = 0.08D;
            boolean flag = this.getMot().y <= 0.0D;

            if (flag && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }

            Fluid fluid = this.level.getFluid(this.getChunkCoordinates());
            double d1;
            float f;

            if (this.isInWater() && this.dA() && !this.a(fluid.getType())) {
                d1 = this.locY();
                f = this.isSprinting() ? 0.9F : this.ev();
                float f1 = 0.02F;
                float f2 = (float) EnchantmentManager.e(this);

                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }

                if (!this.onGround) {
                    f2 *= 0.5F;
                }

                if (f2 > 0.0F) {
                    f += (0.54600006F - f) * f2 / 3.0F;
                    f1 += (this.ew() - f1) * f2 / 3.0F;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f = 0.96F;
                }

                this.a(f1, vec3d);
                this.move(EnumMoveType.SELF, this.getMot());
                Vec3D vec3d1 = this.getMot();

                if (this.horizontalCollision && this.isClimbing()) {
                    vec3d1 = new Vec3D(vec3d1.x, 0.2D, vec3d1.z);
                }

                this.setMot(vec3d1.d((double) f, 0.800000011920929D, (double) f));
                Vec3D vec3d2 = this.a(d0, flag, this.getMot());

                this.setMot(vec3d2);
                if (this.horizontalCollision && this.f(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.locY() + d1, vec3d2.z)) {
                    this.setMot(vec3d2.x, 0.30000001192092896D, vec3d2.z);
                }
            } else if (this.aX() && this.dA() && !this.a(fluid.getType())) {
                d1 = this.locY();
                this.a(0.02F, vec3d);
                this.move(EnumMoveType.SELF, this.getMot());
                Vec3D vec3d3;

                if (this.b((Tag) TagsFluid.LAVA) <= this.cN()) {
                    this.setMot(this.getMot().d(0.5D, 0.800000011920929D, 0.5D));
                    vec3d3 = this.a(d0, flag, this.getMot());
                    this.setMot(vec3d3);
                } else {
                    this.setMot(this.getMot().a(0.5D));
                }

                if (!this.isNoGravity()) {
                    this.setMot(this.getMot().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                vec3d3 = this.getMot();
                if (this.horizontalCollision && this.f(vec3d3.x, vec3d3.y + 0.6000000238418579D - this.locY() + d1, vec3d3.z)) {
                    this.setMot(vec3d3.x, 0.30000001192092896D, vec3d3.z);
                }
            } else if (this.isGliding()) {
                Vec3D vec3d4 = this.getMot();

                if (vec3d4.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3D vec3d5 = this.getLookDirection();

                f = this.getXRot() * 0.017453292F;
                double d2 = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double d3 = vec3d4.h();
                double d4 = vec3d5.f();
                float f3 = MathHelper.cos(f);

                f3 = (float) ((double) f3 * (double) f3 * Math.min(1.0D, d4 / 0.4D));
                vec3d4 = this.getMot().add(0.0D, d0 * (-1.0D + (double) f3 * 0.75D), 0.0D);
                double d5;

                if (vec3d4.y < 0.0D && d2 > 0.0D) {
                    d5 = vec3d4.y * -0.1D * (double) f3;
                    vec3d4 = vec3d4.add(vec3d5.x * d5 / d2, d5, vec3d5.z * d5 / d2);
                }

                if (f < 0.0F && d2 > 0.0D) {
                    d5 = d3 * (double) (-MathHelper.sin(f)) * 0.04D;
                    vec3d4 = vec3d4.add(-vec3d5.x * d5 / d2, d5 * 3.2D, -vec3d5.z * d5 / d2);
                }

                if (d2 > 0.0D) {
                    vec3d4 = vec3d4.add((vec3d5.x / d2 * d3 - vec3d4.x) * 0.1D, 0.0D, (vec3d5.z / d2 * d3 - vec3d4.z) * 0.1D);
                }

                this.setMot(vec3d4.d(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                this.move(EnumMoveType.SELF, this.getMot());
                if (this.horizontalCollision && !this.level.isClientSide) {
                    d5 = this.getMot().h();
                    double d6 = d3 - d5;
                    float f4 = (float) (d6 * 10.0D - 3.0D);

                    if (f4 > 0.0F) {
                        this.playSound(this.getSoundFall((int) f4), 1.0F, 1.0F);
                        this.damageEntity(DamageSource.FLY_INTO_WALL, f4);
                    }
                }

                if (this.onGround && !this.level.isClientSide) {
                    this.setFlag(7, false);
                }
            } else {
                BlockPosition blockposition = this.ay();
                float f5 = this.level.getType(blockposition).getBlock().getFrictionFactor();

                f = this.onGround ? f5 * 0.91F : 0.91F;
                Vec3D vec3d6 = this.a(vec3d, f5);
                double d7 = vec3d6.y;

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d7 += (0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2D;
                    this.fallDistance = 0.0F;
                } else if (this.level.isClientSide && !this.level.isLoaded(blockposition)) {
                    if (this.locY() > (double) this.level.getMinBuildHeight()) {
                        d7 = -0.1D;
                    } else {
                        d7 = 0.0D;
                    }
                } else if (!this.isNoGravity()) {
                    d7 -= d0;
                }

                if (this.dL()) {
                    this.setMot(vec3d6.x, d7, vec3d6.z);
                } else {
                    this.setMot(vec3d6.x * (double) f, d7 * 0.9800000190734863D, vec3d6.z * (double) f);
                }
            }
        }

        this.a(this, this instanceof EntityBird);
    }

    public void a(EntityLiving entityliving, boolean flag) {
        entityliving.animationSpeedOld = entityliving.animationSpeed;
        double d0 = entityliving.locX() - entityliving.xo;
        double d1 = flag ? entityliving.locY() - entityliving.yo : 0.0D;
        double d2 = entityliving.locZ() - entityliving.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 4.0F;

        if (f > 1.0F) {
            f = 1.0F;
        }

        entityliving.animationSpeed += (f - entityliving.animationSpeed) * 0.4F;
        entityliving.animationPosition += entityliving.animationSpeed;
    }

    public Vec3D a(Vec3D vec3d, float f) {
        this.a(this.u(f), vec3d);
        this.setMot(this.i(this.getMot()));
        this.move(EnumMoveType.SELF, this.getMot());
        Vec3D vec3d1 = this.getMot();

        if ((this.horizontalCollision || this.jumping) && (this.isClimbing() || this.cS().a(Blocks.POWDER_SNOW) && PowderSnowBlock.a((Entity) this))) {
            vec3d1 = new Vec3D(vec3d1.x, 0.2D, vec3d1.z);
        }

        return vec3d1;
    }

    public Vec3D a(double d0, boolean flag, Vec3D vec3d) {
        if (!this.isNoGravity() && !this.isSprinting()) {
            double d1;

            if (flag && Math.abs(vec3d.y - 0.005D) >= 0.003D && Math.abs(vec3d.y - d0 / 16.0D) < 0.003D) {
                d1 = -0.003D;
            } else {
                d1 = vec3d.y - d0 / 16.0D;
            }

            return new Vec3D(vec3d.x, d1, vec3d.z);
        } else {
            return vec3d;
        }
    }

    private Vec3D i(Vec3D vec3d) {
        if (this.isClimbing()) {
            this.fallDistance = 0.0F;
            float f = 0.15F;
            double d0 = MathHelper.a(vec3d.x, -0.15000000596046448D, 0.15000000596046448D);
            double d1 = MathHelper.a(vec3d.z, -0.15000000596046448D, 0.15000000596046448D);
            double d2 = Math.max(vec3d.y, -0.15000000596046448D);

            if (d2 < 0.0D && !this.cS().a(Blocks.SCAFFOLDING) && this.eN() && this instanceof EntityHuman) {
                d2 = 0.0D;
            }

            vec3d = new Vec3D(d0, d2, d1);
        }

        return vec3d;
    }

    private float u(float f) {
        return this.onGround ? this.ew() * (0.21600002F / (f * f * f)) : this.flyingSpeed;
    }

    public float ew() {
        return this.speed;
    }

    public void r(float f) {
        this.speed = f;
    }

    public boolean attackEntity(Entity entity) {
        this.x(entity);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.B();
        this.F();
        if (!this.level.isClientSide) {
            int i = this.getArrowCount();

            if (i > 0) {
                if (this.removeArrowTime <= 0) {
                    this.removeArrowTime = 20 * (30 - i);
                }

                --this.removeArrowTime;
                if (this.removeArrowTime <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            int j = this.eh();

            if (j > 0) {
                if (this.removeStingerTime <= 0) {
                    this.removeStingerTime = 20 * (30 - j);
                }

                --this.removeStingerTime;
                if (this.removeStingerTime <= 0) {
                    this.r(j - 1);
                }
            }

            this.updateEquipment();
            if (this.tickCount % 20 == 0) {
                this.getCombatTracker().g();
            }

            if (this.isSleeping() && !this.G()) {
                this.entityWakeup();
            }
        }

        this.movementTick();
        double d0 = this.locX() - this.xo;
        double d1 = this.locZ() - this.zo;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.yBodyRot;
        float f2 = 0.0F;

        this.oRun = this.run;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) MathHelper.d(d1, d0) * 57.295776F - 90.0F;
            float f5 = MathHelper.e(MathHelper.g(this.getYRot()) - f4);

            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.attackAnim > 0.0F) {
            f1 = this.getYRot();
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.run += (f3 - this.run) * 0.3F;
        this.level.getMethodProfiler().enter("headTurn");
        f2 = this.e(f1, f2);
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("rangeChecks");

        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }

        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }

        while (this.yBodyRot - this.yBodyRotO < -180.0F) {
            this.yBodyRotO -= 360.0F;
        }

        while (this.yBodyRot - this.yBodyRotO >= 180.0F) {
            this.yBodyRotO += 360.0F;
        }

        while (this.getXRot() - this.xRotO < -180.0F) {
            this.xRotO -= 360.0F;
        }

        while (this.getXRot() - this.xRotO >= 180.0F) {
            this.xRotO += 360.0F;
        }

        while (this.yHeadRot - this.yHeadRotO < -180.0F) {
            this.yHeadRotO -= 360.0F;
        }

        while (this.yHeadRot - this.yHeadRotO >= 180.0F) {
            this.yHeadRotO += 360.0F;
        }

        this.level.getMethodProfiler().exit();
        this.animStep += f2;
        if (this.isGliding()) {
            ++this.fallFlyTicks;
        } else {
            this.fallFlyTicks = 0;
        }

        if (this.isSleeping()) {
            this.setXRot(0.0F);
        }

    }

    public void updateEquipment() {
        Map<EnumItemSlot, ItemStack> map = this.z();

        if (map != null) {
            this.a(map);
            if (!map.isEmpty()) {
                this.b(map);
            }
        }

    }

    @Nullable
    private Map<EnumItemSlot, ItemStack> z() {
        Map<EnumItemSlot, ItemStack> map = null;
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack;

            switch (enumitemslot.a()) {
                case HAND:
                    itemstack = this.e(enumitemslot);
                    break;
                case ARMOR:
                    itemstack = this.d(enumitemslot);
                    break;
                default:
                    continue;
            }

            ItemStack itemstack1 = this.getEquipment(enumitemslot);

            if (!ItemStack.matches(itemstack1, itemstack)) {
                if (map == null) {
                    map = Maps.newEnumMap(EnumItemSlot.class);
                }

                map.put(enumitemslot, itemstack1);
                if (!itemstack.isEmpty()) {
                    this.getAttributeMap().a(itemstack.a(enumitemslot));
                }

                if (!itemstack1.isEmpty()) {
                    this.getAttributeMap().b(itemstack1.a(enumitemslot));
                }
            }
        }

        return map;
    }

    private void a(Map<EnumItemSlot, ItemStack> map) {
        ItemStack itemstack = (ItemStack) map.get(EnumItemSlot.MAINHAND);
        ItemStack itemstack1 = (ItemStack) map.get(EnumItemSlot.OFFHAND);

        if (itemstack != null && itemstack1 != null && ItemStack.matches(itemstack, this.e(EnumItemSlot.OFFHAND)) && ItemStack.matches(itemstack1, this.e(EnumItemSlot.MAINHAND))) {
            ((WorldServer) this.level).getChunkProvider().broadcast(this, new PacketPlayOutEntityStatus(this, (byte) 55));
            map.remove(EnumItemSlot.MAINHAND);
            map.remove(EnumItemSlot.OFFHAND);
            this.c(EnumItemSlot.MAINHAND, itemstack.cloneItemStack());
            this.c(EnumItemSlot.OFFHAND, itemstack1.cloneItemStack());
        }

    }

    private void b(Map<EnumItemSlot, ItemStack> map) {
        List<Pair<EnumItemSlot, ItemStack>> list = Lists.newArrayListWithCapacity(map.size());

        map.forEach((enumitemslot, itemstack) -> {
            ItemStack itemstack1 = itemstack.cloneItemStack();

            list.add(Pair.of(enumitemslot, itemstack1));
            switch (enumitemslot.a()) {
                case HAND:
                    this.c(enumitemslot, itemstack1);
                    break;
                case ARMOR:
                    this.b(enumitemslot, itemstack1);
            }

        });
        ((WorldServer) this.level).getChunkProvider().broadcast(this, new PacketPlayOutEntityEquipment(this.getId(), list));
    }

    private ItemStack d(EnumItemSlot enumitemslot) {
        return (ItemStack) this.lastArmorItemStacks.get(enumitemslot.b());
    }

    private void b(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.lastArmorItemStacks.set(enumitemslot.b(), itemstack);
    }

    private ItemStack e(EnumItemSlot enumitemslot) {
        return (ItemStack) this.lastHandItemStacks.get(enumitemslot.b());
    }

    private void c(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.lastHandItemStacks.set(enumitemslot.b(), itemstack);
    }

    protected float e(float f, float f1) {
        float f2 = MathHelper.g(f - this.yBodyRot);

        this.yBodyRot += f2 * 0.3F;
        float f3 = MathHelper.g(this.getYRot() - this.yBodyRot);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.yBodyRot = this.getYRot() - f3;
        if (f3 * f3 > 2500.0F) {
            this.yBodyRot += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void movementTick() {
        if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }

        if (this.cH()) {
            this.lerpSteps = 0;
            this.d(this.locX(), this.locY(), this.locZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.locX() + (this.lerpX - this.locX()) / (double) this.lerpSteps;
            double d1 = this.locY() + (this.lerpY - this.locY()) / (double) this.lerpSteps;
            double d2 = this.locZ() + (this.lerpZ - this.locZ()) / (double) this.lerpSteps;
            double d3 = MathHelper.f(this.lerpYRot - (double) this.getYRot());

            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.getYRot(), this.getXRot());
        } else if (!this.doAITick()) {
            this.setMot(this.getMot().a(0.98D));
        }

        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float) ((double) this.yHeadRot + MathHelper.f(this.lyHeadRot - (double) this.yHeadRot) / (double) this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }

        Vec3D vec3d = this.getMot();
        double d4 = vec3d.x;
        double d5 = vec3d.y;
        double d6 = vec3d.z;

        if (Math.abs(vec3d.x) < 0.003D) {
            d4 = 0.0D;
        }

        if (Math.abs(vec3d.y) < 0.003D) {
            d5 = 0.0D;
        }

        if (Math.abs(vec3d.z) < 0.003D) {
            d6 = 0.0D;
        }

        this.setMot(d4, d5, d6);
        this.level.getMethodProfiler().enter("ai");
        if (this.isFrozen()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        } else if (this.doAITick()) {
            this.level.getMethodProfiler().enter("newAi");
            this.doTick();
            this.level.getMethodProfiler().exit();
        }

        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("jump");
        if (this.jumping && this.dA()) {
            double d7;

            if (this.aX()) {
                d7 = this.b((Tag) TagsFluid.LAVA);
            } else {
                d7 = this.b((Tag) TagsFluid.WATER);
            }

            boolean flag = this.isInWater() && d7 > 0.0D;
            double d8 = this.cN();

            if (flag && (!this.onGround || d7 > d8)) {
                this.c((Tag) TagsFluid.WATER);
            } else if (this.aX() && (!this.onGround || d7 > d8)) {
                this.c((Tag) TagsFluid.LAVA);
            } else if ((this.onGround || flag && d7 <= d8) && this.noJumpDelay == 0) {
                this.jump();
                this.noJumpDelay = 10;
            }
        } else {
            this.noJumpDelay = 0;
        }

        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("travel");
        this.xxa *= 0.98F;
        this.zza *= 0.98F;
        this.A();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        this.g(new Vec3D((double) this.xxa, (double) this.yya, (double) this.zza));
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("freezing");
        boolean flag1 = this.getEntityType().a((Tag) TagsEntity.FREEZE_HURTS_EXTRA_TYPES);
        int i;

        if (!this.level.isClientSide && !this.dV()) {
            i = this.getTicksFrozen();
            if (this.isInPowderSnow && this.dg()) {
                this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), i + 1));
            } else {
                this.setTicksFrozen(Math.max(0, i - 2));
            }
        }

        this.dx();
        this.dy();
        if (!this.level.isClientSide && this.tickCount % 40 == 0 && this.isFullyFrozen() && this.dg()) {
            i = flag1 ? 5 : 1;
            this.damageEntity(DamageSource.FREEZE, (float) i);
        }

        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.a(axisalignedbb, this.getBoundingBox());
        }

        this.collideNearby();
        this.level.getMethodProfiler().exit();
        if (!this.level.isClientSide && this.ex() && this.aN()) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

    }

    public boolean ex() {
        return false;
    }

    private void A() {
        boolean flag = this.getFlag(7);

        if (flag && !this.onGround && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.CHEST);

            if (itemstack.a(Items.ELYTRA) && ItemElytra.d(itemstack)) {
                flag = true;
                int i = this.fallFlyTicks + 1;

                if (!this.level.isClientSide && i % 10 == 0) {
                    int j = i / 10;

                    if (j % 2 == 0) {
                        itemstack.damage(1, this, (entityliving) -> {
                            entityliving.broadcastItemBreak(EnumItemSlot.CHEST);
                        });
                    }

                    this.a(GameEvent.ELYTRA_FREE_FALL);
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.level.isClientSide) {
            this.setFlag(7, flag);
        }

    }

    protected void doTick() {}

    protected void collideNearby() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            int i = this.level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            int j;

            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                j = 0;

                for (int k = 0; k < list.size(); ++k) {
                    if (!((Entity) list.get(k)).isPassenger()) {
                        ++j;
                    }
                }

                if (j > i - 1) {
                    this.damageEntity(DamageSource.CRAMMING, 6.0F);
                }
            }

            for (j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                this.A(entity);
            }
        }

    }

    protected void a(AxisAlignedBB axisalignedbb, AxisAlignedBB axisalignedbb1) {
        AxisAlignedBB axisalignedbb2 = axisalignedbb.b(axisalignedbb1);
        List<Entity> list = this.level.getEntities(this, axisalignedbb2);

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity instanceof EntityLiving) {
                    this.g((EntityLiving) entity);
                    this.autoSpinAttackTicks = 0;
                    this.setMot(this.getMot().a(-0.2D));
                    break;
                }
            }
        } else if (this.horizontalCollision) {
            this.autoSpinAttackTicks = 0;
        }

        if (!this.level.isClientSide && this.autoSpinAttackTicks <= 0) {
            this.c(4, false);
        }

    }

    protected void A(Entity entity) {
        entity.collide(this);
    }

    protected void g(EntityLiving entityliving) {}

    public void s(int i) {
        this.autoSpinAttackTicks = i;
        if (!this.level.isClientSide) {
            this.c(4, true);
        }

    }

    public boolean isRiptiding() {
        return ((Byte) this.entityData.get(EntityLiving.DATA_LIVING_ENTITY_FLAGS) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();

        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.level.isClientSide) {
            this.a(entity);
        }

    }

    @Override
    public void passengerTick() {
        super.passengerTick();
        this.oRun = this.run;
        this.run = 0.0F;
        this.fallDistance = 0.0F;
    }

    @Override
    public void a(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.lerpX = d0;
        this.lerpY = d1;
        this.lerpZ = d2;
        this.lerpYRot = (double) f;
        this.lerpXRot = (double) f1;
        this.lerpSteps = i;
    }

    @Override
    public void a(float f, int i) {
        this.lyHeadRot = (double) f;
        this.lerpHeadSteps = i;
    }

    public void setJumping(boolean flag) {
        this.jumping = flag;
    }

    public void a(EntityItem entityitem) {
        EntityHuman entityhuman = entityitem.getThrower() != null ? this.level.b(entityitem.getThrower()) : null;

        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.ITEM_PICKED_UP_BY_ENTITY.a((EntityPlayer) entityhuman, entityitem.getItemStack(), this);
        }

    }

    public void receive(Entity entity, int i) {
        if (!entity.isRemoved() && !this.level.isClientSide && (entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityExperienceOrb)) {
            ((WorldServer) this.level).getChunkProvider().broadcast(entity, new PacketPlayOutCollect(entity.getId(), this.getId(), i));
        }

    }

    public boolean hasLineOfSight(Entity entity) {
        if (entity.level != this.level) {
            return false;
        } else {
            Vec3D vec3d = new Vec3D(this.locX(), this.getHeadY(), this.locZ());
            Vec3D vec3d1 = new Vec3D(entity.locX(), entity.getHeadY(), entity.locZ());

            return vec3d1.f(vec3d) > 128.0D ? false : this.level.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this)).getType() == MovingObjectPosition.EnumMovingObjectType.MISS;
        }
    }

    @Override
    public float g(float f) {
        return f == 1.0F ? this.yHeadRot : MathHelper.h(f, this.yHeadRotO, this.yHeadRot);
    }

    public float s(float f) {
        float f1 = this.attackAnim - this.oAttackAnim;

        if (f1 < 0.0F) {
            ++f1;
        }

        return this.oAttackAnim + f1 * f;
    }

    public boolean doAITick() {
        return !this.level.isClientSide;
    }

    @Override
    public boolean isInteractable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable() {
        return this.isAlive() && !this.isSpectator() && !this.isClimbing();
    }

    @Override
    protected void velocityChanged() {
        this.hurtMarked = this.random.nextDouble() >= this.b(GenericAttributes.KNOCKBACK_RESISTANCE);
    }

    @Override
    public float getHeadRotation() {
        return this.yHeadRot;
    }

    @Override
    public void setHeadRotation(float f) {
        this.yHeadRot = f;
    }

    @Override
    public void m(float f) {
        this.yBodyRot = f;
    }

    @Override
    protected Vec3D a(EnumDirection.EnumAxis enumdirection_enumaxis, BlockUtil.Rectangle blockutil_rectangle) {
        return h(super.a(enumdirection_enumaxis, blockutil_rectangle));
    }

    public static Vec3D h(Vec3D vec3d) {
        return new Vec3D(vec3d.x, vec3d.y, 0.0D);
    }

    public float getAbsorptionHearts() {
        return this.absorptionAmount;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.absorptionAmount = f;
    }

    public void enterCombat() {}

    public void exitCombat() {}

    protected void eD() {
        this.effectsDirty = true;
    }

    public abstract EnumMainHand getMainHand();

    public boolean isHandRaised() {
        return ((Byte) this.entityData.get(EntityLiving.DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
    }

    public EnumHand getRaisedHand() {
        return ((Byte) this.entityData.get(EntityLiving.DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    private void B() {
        if (this.isHandRaised()) {
            if (ItemStack.d(this.b(this.getRaisedHand()), this.useItem)) {
                this.useItem = this.b(this.getRaisedHand());
                this.a(this.useItem);
            } else {
                this.clearActiveItem();
            }
        }

    }

    protected void a(ItemStack itemstack) {
        itemstack.b(this.level, this, this.eI());
        if (this.D()) {
            this.b(itemstack, 5);
        }

        if (--this.useItemRemaining == 0 && !this.level.isClientSide && !itemstack.q()) {
            this.s();
        }

    }

    private boolean D() {
        int i = this.eI();
        FoodInfo foodinfo = this.useItem.getItem().getFoodInfo();
        boolean flag = foodinfo != null && foodinfo.e();

        flag |= i <= this.useItem.o() - 7;
        return flag && i % 4 == 0;
    }

    private void F() {
        this.swimAmountO = this.swimAmount;
        if (this.bL()) {
            this.swimAmount = Math.min(1.0F, this.swimAmount + 0.09F);
        } else {
            this.swimAmount = Math.max(0.0F, this.swimAmount - 0.09F);
        }

    }

    protected void c(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityLiving.DATA_LIVING_ENTITY_FLAGS);
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.entityData.set(EntityLiving.DATA_LIVING_ENTITY_FLAGS, (byte) j);
    }

    public void c(EnumHand enumhand) {
        ItemStack itemstack = this.b(enumhand);

        if (!itemstack.isEmpty() && !this.isHandRaised()) {
            this.useItem = itemstack;
            this.useItemRemaining = itemstack.o();
            if (!this.level.isClientSide) {
                this.c(1, true);
                this.c(2, enumhand == EnumHand.OFF_HAND);
            }

        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityLiving.SLEEPING_POS_ID.equals(datawatcherobject)) {
            if (this.level.isClientSide) {
                this.getBedPosition().ifPresent(this::a);
            }
        } else if (EntityLiving.DATA_LIVING_ENTITY_FLAGS.equals(datawatcherobject) && this.level.isClientSide) {
            if (this.isHandRaised() && this.useItem.isEmpty()) {
                this.useItem = this.b(this.getRaisedHand());
                if (!this.useItem.isEmpty()) {
                    this.useItemRemaining = this.useItem.o();
                }
            } else if (!this.isHandRaised() && !this.useItem.isEmpty()) {
                this.useItem = ItemStack.EMPTY;
                this.useItemRemaining = 0;
            }
        }

    }

    @Override
    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        super.a(argumentanchor_anchor, vec3d);
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRot = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot;
    }

    protected void b(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty() && this.isHandRaised()) {
            if (itemstack.p() == EnumAnimation.DRINK) {
                this.playSound(this.d(itemstack), 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }

            if (itemstack.p() == EnumAnimation.EAT) {
                this.a(itemstack, i);
                this.playSound(this.e(itemstack), 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

        }
    }

    private void a(ItemStack itemstack, int i) {
        for (int j = 0; j < i; ++j) {
            Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

            vec3d = vec3d.a(-this.getXRot() * 0.017453292F);
            vec3d = vec3d.b(-this.getYRot() * 0.017453292F);
            double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);

            vec3d1 = vec3d1.a(-this.getXRot() * 0.017453292F);
            vec3d1 = vec3d1.b(-this.getYRot() * 0.017453292F);
            vec3d1 = vec3d1.add(this.locX(), this.getHeadY(), this.locZ());
            this.level.addParticle(new ParticleParamItem(Particles.ITEM, itemstack), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
        }

    }

    protected void s() {
        EnumHand enumhand = this.getRaisedHand();

        if (!this.useItem.equals(this.b(enumhand))) {
            this.releaseActiveItem();
        } else {
            if (!this.useItem.isEmpty() && this.isHandRaised()) {
                this.b(this.useItem, 16);
                ItemStack itemstack = this.useItem.a(this.level, this);

                if (itemstack != this.useItem) {
                    this.a(enumhand, itemstack);
                }

                this.clearActiveItem();
            }

        }
    }

    public ItemStack getActiveItem() {
        return this.useItem;
    }

    public int eI() {
        return this.useItemRemaining;
    }

    public int eJ() {
        return this.isHandRaised() ? this.useItem.o() - this.eI() : 0;
    }

    public void releaseActiveItem() {
        if (!this.useItem.isEmpty()) {
            this.useItem.a(this.level, this, this.eI());
            if (this.useItem.q()) {
                this.B();
            }
        }

        this.clearActiveItem();
    }

    public void clearActiveItem() {
        if (!this.level.isClientSide) {
            this.c(1, false);
        }

        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = 0;
    }

    public boolean isBlocking() {
        if (this.isHandRaised() && !this.useItem.isEmpty()) {
            Item item = this.useItem.getItem();

            return item.c(this.useItem) != EnumAnimation.BLOCK ? false : item.b(this.useItem) - this.useItemRemaining >= 5;
        } else {
            return false;
        }
    }

    public boolean eN() {
        return this.isSneaking();
    }

    public boolean isGliding() {
        return this.getFlag(7);
    }

    @Override
    public boolean bL() {
        return super.bL() || !this.isGliding() && this.getPose() == EntityPose.FALL_FLYING;
    }

    public int eP() {
        return this.fallFlyTicks;
    }

    public boolean a(double d0, double d1, double d2, boolean flag) {
        double d3 = this.locX();
        double d4 = this.locY();
        double d5 = this.locZ();
        double d6 = d1;
        boolean flag1 = false;
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        World world = this.level;

        if (world.isLoaded(blockposition)) {
            boolean flag2 = false;

            while (!flag2 && blockposition.getY() > world.getMinBuildHeight()) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getMaterial().isSolid()) {
                    flag2 = true;
                } else {
                    --d6;
                    blockposition = blockposition1;
                }
            }

            if (flag2) {
                this.enderTeleportTo(d0, d6, d2);
                if (world.getCubes(this) && !world.containsLiquid(this.getBoundingBox())) {
                    flag1 = true;
                }
            }
        }

        if (!flag1) {
            this.enderTeleportTo(d3, d4, d5);
            return false;
        } else {
            if (flag) {
                world.broadcastEntityEffect(this, (byte) 46);
            }

            if (this instanceof EntityCreature) {
                ((EntityCreature) this).getNavigation().o();
            }

            return true;
        }
    }

    public boolean eQ() {
        return true;
    }

    public boolean eR() {
        return true;
    }

    public void a(BlockPosition blockposition, boolean flag) {}

    public boolean g(ItemStack itemstack) {
        return false;
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntityLiving(this);
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return entitypose == EntityPose.SLEEPING ? EntityLiving.SLEEPING_DIMENSIONS : super.a(entitypose).a(this.dz());
    }

    public ImmutableList<EntityPose> eS() {
        return ImmutableList.of(EntityPose.STANDING);
    }

    public AxisAlignedBB f(EntityPose entitypose) {
        EntitySize entitysize = this.a(entitypose);

        return new AxisAlignedBB((double) (-entitysize.width / 2.0F), 0.0D, (double) (-entitysize.width / 2.0F), (double) (entitysize.width / 2.0F), (double) entitysize.height, (double) (entitysize.width / 2.0F));
    }

    public Optional<BlockPosition> getBedPosition() {
        return (Optional) this.entityData.get(EntityLiving.SLEEPING_POS_ID);
    }

    public void e(BlockPosition blockposition) {
        this.entityData.set(EntityLiving.SLEEPING_POS_ID, Optional.of(blockposition));
    }

    public void eU() {
        this.entityData.set(EntityLiving.SLEEPING_POS_ID, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getBedPosition().isPresent();
    }

    public void entitySleep(BlockPosition blockposition) {
        if (this.isPassenger()) {
            this.stopRiding();
        }

        IBlockData iblockdata = this.level.getType(blockposition);

        if (iblockdata.getBlock() instanceof BlockBed) {
            this.level.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBed.OCCUPIED, true), 3);
        }

        this.setPose(EntityPose.SLEEPING);
        this.a(blockposition);
        this.e(blockposition);
        this.setMot(Vec3D.ZERO);
        this.hasImpulse = true;
    }

    private void a(BlockPosition blockposition) {
        this.setPosition((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.6875D, (double) blockposition.getZ() + 0.5D);
    }

    private boolean G() {
        return (Boolean) this.getBedPosition().map((blockposition) -> {
            return this.level.getType(blockposition).getBlock() instanceof BlockBed;
        }).orElse(false);
    }

    public void entityWakeup() {
        Optional optional = this.getBedPosition();
        World world = this.level;

        java.util.Objects.requireNonNull(this.level);
        optional.filter(world::isLoaded).ifPresent((blockposition) -> {
            IBlockData iblockdata = this.level.getType(blockposition);

            if (iblockdata.getBlock() instanceof BlockBed) {
                this.level.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBed.OCCUPIED, false), 3);
                Vec3D vec3d = (Vec3D) BlockBed.a(this.getEntityType(), this.level, blockposition, this.getYRot()).orElseGet(() -> {
                    BlockPosition blockposition1 = blockposition.up();

                    return new Vec3D((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.1D, (double) blockposition1.getZ() + 0.5D);
                });
                Vec3D vec3d1 = Vec3D.c((BaseBlockPosition) blockposition).d(vec3d).d();
                float f = (float) MathHelper.f(MathHelper.d(vec3d1.z, vec3d1.x) * 57.2957763671875D - 90.0D);

                this.setPosition(vec3d.x, vec3d.y, vec3d.z);
                this.setYRot(f);
                this.setXRot(0.0F);
            }

        });
        Vec3D vec3d = this.getPositionVector();

        this.setPose(EntityPose.STANDING);
        this.setPosition(vec3d.x, vec3d.y, vec3d.z);
        this.eU();
    }

    @Nullable
    public EnumDirection eX() {
        BlockPosition blockposition = (BlockPosition) this.getBedPosition().orElse((Object) null);

        return blockposition != null ? BlockBed.a((IBlockAccess) this.level, blockposition) : null;
    }

    @Override
    public boolean inBlock() {
        return !this.isSleeping() && super.inBlock();
    }

    @Override
    protected final float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitypose == EntityPose.SLEEPING ? 0.2F : this.b(entitypose, entitysize);
    }

    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return super.getHeadHeight(entitypose, entitysize);
    }

    public ItemStack h(ItemStack itemstack) {
        return ItemStack.EMPTY;
    }

    public ItemStack a(World world, ItemStack itemstack) {
        if (itemstack.J()) {
            world.a((Entity) this, GameEvent.EAT, this.cT());
            world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), this.e(itemstack), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
            this.a(itemstack, world, this);
            if (!(this instanceof EntityHuman) || !((EntityHuman) this).getAbilities().instabuild) {
                itemstack.subtract(1);
            }

            this.a(GameEvent.EAT);
        }

        return itemstack;
    }

    private void a(ItemStack itemstack, World world, EntityLiving entityliving) {
        Item item = itemstack.getItem();

        if (item.isFood()) {
            List<Pair<MobEffect, Float>> list = item.getFoodInfo().f();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Pair<MobEffect, Float> pair = (Pair) iterator.next();

                if (!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < (Float) pair.getSecond()) {
                    entityliving.addEffect(new MobEffect((MobEffect) pair.getFirst()));
                }
            }
        }

    }

    private static byte f(EnumItemSlot enumitemslot) {
        switch (enumitemslot) {
            case MAINHAND:
                return 47;
            case OFFHAND:
                return 48;
            case HEAD:
                return 49;
            case CHEST:
                return 50;
            case FEET:
                return 52;
            case LEGS:
                return 51;
            default:
                return 47;
        }
    }

    public void broadcastItemBreak(EnumItemSlot enumitemslot) {
        this.level.broadcastEntityEffect(this, f(enumitemslot));
    }

    public void broadcastItemBreak(EnumHand enumhand) {
        this.broadcastItemBreak(enumhand == EnumHand.MAIN_HAND ? EnumItemSlot.MAINHAND : EnumItemSlot.OFFHAND);
    }

    @Override
    public AxisAlignedBB cs() {
        if (this.getEquipment(EnumItemSlot.HEAD).a(Items.DRAGON_HEAD)) {
            float f = 0.5F;

            return this.getBoundingBox().grow(0.5D, 0.5D, 0.5D);
        } else {
            return super.cs();
        }
    }

    public static EnumItemSlot getEquipmentSlotForItem(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return !itemstack.a(Items.CARVED_PUMPKIN) && (!(item instanceof ItemBlock) || !(((ItemBlock) item).getBlock() instanceof BlockSkullAbstract)) ? (item instanceof ItemArmor ? ((ItemArmor) item).b() : (itemstack.a(Items.ELYTRA) ? EnumItemSlot.CHEST : (itemstack.a(Items.SHIELD) ? EnumItemSlot.OFFHAND : EnumItemSlot.MAINHAND))) : EnumItemSlot.HEAD;
    }

    private static SlotAccess a(EntityLiving entityliving, EnumItemSlot enumitemslot) {
        return enumitemslot != EnumItemSlot.HEAD && enumitemslot != EnumItemSlot.MAINHAND && enumitemslot != EnumItemSlot.OFFHAND ? SlotAccess.a(entityliving, enumitemslot, (itemstack) -> {
            return itemstack.isEmpty() || EntityInsentient.getEquipmentSlotForItem(itemstack) == enumitemslot;
        }) : SlotAccess.a(entityliving, enumitemslot);
    }

    @Nullable
    private static EnumItemSlot c(int i) {
        return i == 100 + EnumItemSlot.HEAD.b() ? EnumItemSlot.HEAD : (i == 100 + EnumItemSlot.CHEST.b() ? EnumItemSlot.CHEST : (i == 100 + EnumItemSlot.LEGS.b() ? EnumItemSlot.LEGS : (i == 100 + EnumItemSlot.FEET.b() ? EnumItemSlot.FEET : (i == 98 ? EnumItemSlot.MAINHAND : (i == 99 ? EnumItemSlot.OFFHAND : null)))));
    }

    @Override
    public SlotAccess k(int i) {
        EnumItemSlot enumitemslot = c(i);

        return enumitemslot != null ? a(this, enumitemslot) : super.k(i);
    }

    @Override
    public boolean dg() {
        if (this.isSpectator()) {
            return false;
        } else {
            boolean flag = !this.getEquipment(EnumItemSlot.HEAD).a((Tag) TagsItem.FREEZE_IMMUNE_WEARABLES) && !this.getEquipment(EnumItemSlot.CHEST).a((Tag) TagsItem.FREEZE_IMMUNE_WEARABLES) && !this.getEquipment(EnumItemSlot.LEGS).a((Tag) TagsItem.FREEZE_IMMUNE_WEARABLES) && !this.getEquipment(EnumItemSlot.FEET).a((Tag) TagsItem.FREEZE_IMMUNE_WEARABLES);

            return flag && super.dg();
        }
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return !this.level.isClientSide() && this.hasEffect(MobEffects.GLOWING) || super.isCurrentlyGlowing();
    }

    public void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        double d0 = packetplayoutspawnentityliving.e();
        double d1 = packetplayoutspawnentityliving.f();
        double d2 = packetplayoutspawnentityliving.g();
        float f = (float) (packetplayoutspawnentityliving.k() * 360) / 256.0F;
        float f1 = (float) (packetplayoutspawnentityliving.l() * 360) / 256.0F;

        this.d(d0, d1, d2);
        this.yBodyRot = (float) (packetplayoutspawnentityliving.m() * 360) / 256.0F;
        this.yHeadRot = (float) (packetplayoutspawnentityliving.m() * 360) / 256.0F;
        this.e(packetplayoutspawnentityliving.b());
        this.a_(packetplayoutspawnentityliving.c());
        this.setLocation(d0, d1, d2, f, f1);
        this.setMot((double) ((float) packetplayoutspawnentityliving.h() / 8000.0F), (double) ((float) packetplayoutspawnentityliving.i() / 8000.0F), (double) ((float) packetplayoutspawnentityliving.j() / 8000.0F));
    }
}
