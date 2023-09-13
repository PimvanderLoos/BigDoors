package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsEntity;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StreamAccumulator;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.EnchantmentProtection;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFenceGate;
import net.minecraft.world.level.block.BlockHoney;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.EnumRenderType;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.portal.BlockPortalShape;
import net.minecraft.world.level.portal.ShapeDetectorShape;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements INamableTileEntity, EntityAccess, ICommandListener {

    protected static final Logger LOGGER = LogManager.getLogger();
    public static final String ID_TAG = "id";
    public static final String PASSENGERS_TAG = "Passengers";
    private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
    private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
    public static final int BOARDING_COOLDOWN = 60;
    public static final int TOTAL_AIR_SUPPLY = 300;
    public static final int MAX_ENTITY_TAG_COUNT = 1024;
    public static final double DELTA_AFFECTED_BY_BLOCKS_BELOW = 0.5000001D;
    public static final float BREATHING_DISTANCE_BELOW_EYES = 0.11111111F;
    public static final int BASE_TICKS_REQUIRED_TO_FREEZE = 140;
    public static final int FREEZE_HURT_FREQUENCY = 40;
    private static final AxisAlignedBB INITIAL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static final double WATER_FLOW_SCALE = 0.014D;
    private static final double LAVA_FAST_FLOW_SCALE = 0.007D;
    private static final double LAVA_SLOW_FLOW_SCALE = 0.0023333333333333335D;
    public static final String UUID_TAG = "UUID";
    private static double viewScale = 1.0D;
    private final EntityTypes<?> type;
    private int id;
    public boolean blocksBuilding;
    public ImmutableList<Entity> passengers;
    protected int boardingCooldown;
    @Nullable
    private Entity vehicle;
    public World level;
    public double xo;
    public double yo;
    public double zo;
    private Vec3D position;
    private BlockPosition blockPosition;
    private Vec3D deltaMovement;
    private float yRot;
    private float xRot;
    public float yRotO;
    public float xRotO;
    private AxisAlignedBB bb;
    protected boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean hurtMarked;
    protected Vec3D stuckSpeedMultiplier;
    @Nullable
    private Entity.RemovalReason removalReason;
    public static final float DEFAULT_BB_WIDTH = 0.6F;
    public static final float DEFAULT_BB_HEIGHT = 1.8F;
    public float walkDistO;
    public float walkDist;
    public float moveDist;
    public float flyDist;
    public float fallDistance;
    private float nextStep;
    public double xOld;
    public double yOld;
    public double zOld;
    public float maxUpStep;
    public boolean noPhysics;
    protected final Random random;
    public int tickCount;
    public int remainingFireTicks;
    public boolean wasTouchingWater;
    protected Object2DoubleMap<Tag<FluidType>> fluidHeight;
    protected boolean wasEyeInWater;
    @Nullable
    protected Tag<FluidType> fluidOnEyes;
    public int invulnerableTime;
    protected boolean firstTick;
    protected final DataWatcher entityData;
    protected static final DataWatcherObject<Byte> DATA_SHARED_FLAGS_ID = DataWatcher.a(Entity.class, DataWatcherRegistry.BYTE);
    protected static final int FLAG_ONFIRE = 0;
    private static final int FLAG_SHIFT_KEY_DOWN = 1;
    private static final int FLAG_SPRINTING = 3;
    private static final int FLAG_SWIMMING = 4;
    private static final int FLAG_INVISIBLE = 5;
    protected static final int FLAG_GLOWING = 6;
    protected static final int FLAG_FALL_FLYING = 7;
    private static final DataWatcherObject<Integer> DATA_AIR_SUPPLY_ID = DataWatcher.a(Entity.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Optional<IChatBaseComponent>> DATA_CUSTOM_NAME = DataWatcher.a(Entity.class, DataWatcherRegistry.OPTIONAL_COMPONENT);
    private static final DataWatcherObject<Boolean> DATA_CUSTOM_NAME_VISIBLE = DataWatcher.a(Entity.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_SILENT = DataWatcher.a(Entity.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_NO_GRAVITY = DataWatcher.a(Entity.class, DataWatcherRegistry.BOOLEAN);
    protected static final DataWatcherObject<EntityPose> DATA_POSE = DataWatcher.a(Entity.class, DataWatcherRegistry.POSE);
    private static final DataWatcherObject<Integer> DATA_TICKS_FROZEN = DataWatcher.a(Entity.class, DataWatcherRegistry.INT);
    private EntityInLevelCallback levelCallback;
    private Vec3D packetCoordinates;
    public boolean noCulling;
    public boolean hasImpulse;
    public int portalCooldown;
    protected boolean isInsidePortal;
    protected int portalTime;
    protected BlockPosition portalEntrancePos;
    private boolean invulnerable;
    protected UUID uuid;
    protected String stringUUID;
    private boolean hasGlowingTag;
    private final Set<String> tags;
    private final double[] pistonDeltas;
    private long pistonDeltasGameTime;
    private EntitySize dimensions;
    private float eyeHeight;
    public boolean isInPowderSnow;
    public boolean wasInPowderSnow;
    public boolean wasOnFire;
    private float crystalSoundIntensity;
    private int lastCrystalSoundPlayTick;
    public boolean hasVisualFire;

    public Entity(EntityTypes<?> entitytypes, World world) {
        this.id = Entity.ENTITY_COUNTER.incrementAndGet();
        this.passengers = ImmutableList.of();
        this.deltaMovement = Vec3D.ZERO;
        this.bb = Entity.INITIAL_AABB;
        this.stuckSpeedMultiplier = Vec3D.ZERO;
        this.nextStep = 1.0F;
        this.random = new Random();
        this.remainingFireTicks = -this.getMaxFireTicks();
        this.fluidHeight = new Object2DoubleArrayMap(2);
        this.firstTick = true;
        this.levelCallback = EntityInLevelCallback.NULL;
        this.uuid = MathHelper.a(this.random);
        this.stringUUID = this.uuid.toString();
        this.tags = Sets.newHashSet();
        this.pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
        this.type = entitytypes;
        this.level = world;
        this.dimensions = entitytypes.m();
        this.position = Vec3D.ZERO;
        this.blockPosition = BlockPosition.ZERO;
        this.packetCoordinates = Vec3D.ZERO;
        this.entityData = new DataWatcher(this);
        this.entityData.register(Entity.DATA_SHARED_FLAGS_ID, (byte) 0);
        this.entityData.register(Entity.DATA_AIR_SUPPLY_ID, this.bS());
        this.entityData.register(Entity.DATA_CUSTOM_NAME_VISIBLE, false);
        this.entityData.register(Entity.DATA_CUSTOM_NAME, Optional.empty());
        this.entityData.register(Entity.DATA_SILENT, false);
        this.entityData.register(Entity.DATA_NO_GRAVITY, false);
        this.entityData.register(Entity.DATA_POSE, EntityPose.STANDING);
        this.entityData.register(Entity.DATA_TICKS_FROZEN, 0);
        this.initDatawatcher();
        this.setPosition(0.0D, 0.0D, 0.0D);
        this.eyeHeight = this.getHeadHeight(EntityPose.STANDING, this.dimensions);
    }

    public boolean a(BlockPosition blockposition, IBlockData iblockdata) {
        VoxelShape voxelshape = iblockdata.b((IBlockAccess) this.level, blockposition, VoxelShapeCollision.a(this));
        VoxelShape voxelshape1 = voxelshape.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

        return VoxelShapes.c(voxelshape1, VoxelShapes.a(this.getBoundingBox()), OperatorBoolean.AND);
    }

    public int V() {
        ScoreboardTeamBase scoreboardteambase = this.getScoreboardTeam();

        return scoreboardteambase != null && scoreboardteambase.getColor().e() != null ? scoreboardteambase.getColor().e() : 16777215;
    }

    public boolean isSpectator() {
        return false;
    }

    public final void decouple() {
        if (this.isVehicle()) {
            this.ejectPassengers();
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

    }

    public void d(double d0, double d1, double d2) {
        this.a_(new Vec3D(d0, d1, d2));
    }

    public void a_(Vec3D vec3d) {
        this.packetCoordinates = vec3d;
    }

    public Vec3D X() {
        return this.packetCoordinates;
    }

    public EntityTypes<?> getEntityType() {
        return this.type;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void e(int i) {
        this.id = i;
    }

    public Set<String> getScoreboardTags() {
        return this.tags;
    }

    public boolean addScoreboardTag(String s) {
        return this.tags.size() >= 1024 ? false : this.tags.add(s);
    }

    public boolean removeScoreboardTag(String s) {
        return this.tags.remove(s);
    }

    public void killEntity() {
        this.a(Entity.RemovalReason.KILLED);
    }

    public final void die() {
        this.a(Entity.RemovalReason.DISCARDED);
    }

    protected abstract void initDatawatcher();

    public DataWatcher getDataWatcher() {
        return this.entityData;
    }

    public boolean equals(Object object) {
        return object instanceof Entity ? ((Entity) object).id == this.id : false;
    }

    public int hashCode() {
        return this.id;
    }

    public void a(Entity.RemovalReason entity_removalreason) {
        this.setRemoved(entity_removalreason);
        if (entity_removalreason == Entity.RemovalReason.KILLED) {
            this.a(GameEvent.ENTITY_KILLED);
        }

    }

    public void ae() {}

    public void setPose(EntityPose entitypose) {
        this.entityData.set(Entity.DATA_POSE, entitypose);
    }

    public EntityPose getPose() {
        return (EntityPose) this.entityData.get(Entity.DATA_POSE);
    }

    public boolean a(Entity entity, double d0) {
        double d1 = entity.position.x - this.position.x;
        double d2 = entity.position.y - this.position.y;
        double d3 = entity.position.z - this.position.z;

        return d1 * d1 + d2 * d2 + d3 * d3 < d0 * d0;
    }

    protected void setYawPitch(float f, float f1) {
        this.setYRot(f % 360.0F);
        this.setXRot(f1 % 360.0F);
    }

    public final void b(Vec3D vec3d) {
        this.setPosition(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    public void setPosition(double d0, double d1, double d2) {
        this.setPositionRaw(d0, d1, d2);
        this.a(this.ag());
    }

    protected AxisAlignedBB ag() {
        return this.dimensions.a(this.position);
    }

    protected void ah() {
        this.setPosition(this.position.x, this.position.y, this.position.z);
    }

    public void a(double d0, double d1) {
        float f = (float) d1 * 0.15F;
        float f1 = (float) d0 * 0.15F;

        this.setXRot(this.getXRot() + f);
        this.setYRot(this.getYRot() + f1);
        this.setXRot(MathHelper.a(this.getXRot(), -90.0F, 90.0F));
        this.xRotO += f;
        this.yRotO += f1;
        this.xRotO = MathHelper.a(this.xRotO, -90.0F, 90.0F);
        if (this.vehicle != null) {
            this.vehicle.j(this);
        }

    }

    public void tick() {
        this.entityBaseTick();
    }

    public void entityBaseTick() {
        this.level.getMethodProfiler().enter("entityBaseTick");
        if (this.isPassenger() && this.getVehicle().isRemoved()) {
            this.stopRiding();
        }

        if (this.boardingCooldown > 0) {
            --this.boardingCooldown;
        }

        this.walkDistO = this.walkDist;
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.doPortalTick();
        if (this.aV()) {
            this.aW();
        }

        this.wasInPowderSnow = this.isInPowderSnow;
        this.isInPowderSnow = false;
        this.aR();
        this.l();
        this.aQ();
        if (this.level.isClientSide) {
            this.extinguish();
        } else if (this.remainingFireTicks > 0) {
            if (this.isFireProof()) {
                this.setFireTicks(this.remainingFireTicks - 4);
                if (this.remainingFireTicks < 0) {
                    this.extinguish();
                }
            } else {
                if (this.remainingFireTicks % 20 == 0 && !this.aX()) {
                    this.damageEntity(DamageSource.ON_FIRE, 1.0F);
                }

                this.setFireTicks(this.remainingFireTicks - 1);
            }

            if (this.getTicksFrozen() > 0) {
                this.setTicksFrozen(0);
                this.level.a((EntityHuman) null, 1009, this.blockPosition, 1);
            }
        }

        if (this.aX()) {
            this.burnFromLava();
            this.fallDistance *= 0.5F;
        }

        this.aj();
        if (!this.level.isClientSide) {
            this.a_(this.remainingFireTicks > 0);
        }

        this.firstTick = false;
        this.level.getMethodProfiler().exit();
    }

    public void a_(boolean flag) {
        this.setFlag(0, flag || this.hasVisualFire);
    }

    public void aj() {
        if (this.locY() < (double) (this.level.getMinBuildHeight() - 64)) {
            this.aq();
        }

    }

    public void resetPortalCooldown() {
        this.portalCooldown = this.getDefaultPortalCooldown();
    }

    public boolean al() {
        return this.portalCooldown > 0;
    }

    protected void E() {
        if (this.al()) {
            --this.portalCooldown;
        }

    }

    public int am() {
        return 0;
    }

    public void burnFromLava() {
        if (!this.isFireProof()) {
            this.setOnFire(15);
            if (this.damageEntity(DamageSource.LAVA, 4.0F)) {
                this.playSound(SoundEffects.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

        }
    }

    public void setOnFire(int i) {
        int j = i * 20;

        if (this instanceof EntityLiving) {
            j = EnchantmentProtection.a((EntityLiving) this, j);
        }

        if (this.remainingFireTicks < j) {
            this.setFireTicks(j);
        }

    }

    public void setFireTicks(int i) {
        this.remainingFireTicks = i;
    }

    public int getFireTicks() {
        return this.remainingFireTicks;
    }

    public void extinguish() {
        this.setFireTicks(0);
    }

    protected void aq() {
        this.die();
    }

    public boolean f(double d0, double d1, double d2) {
        return this.b(this.getBoundingBox().d(d0, d1, d2));
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        return this.level.getCubes(this, axisalignedbb) && !this.level.containsLiquid(axisalignedbb);
    }

    public void setOnGround(boolean flag) {
        this.onGround = flag;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (this.noPhysics) {
            this.setPosition(this.locX() + vec3d.x, this.locY() + vec3d.y, this.locZ() + vec3d.z);
        } else {
            this.wasOnFire = this.isBurning();
            if (enummovetype == EnumMoveType.PISTON) {
                vec3d = this.c(vec3d);
                if (vec3d.equals(Vec3D.ZERO)) {
                    return;
                }
            }

            this.level.getMethodProfiler().enter("move");
            if (this.stuckSpeedMultiplier.g() > 1.0E-7D) {
                vec3d = vec3d.h(this.stuckSpeedMultiplier);
                this.stuckSpeedMultiplier = Vec3D.ZERO;
                this.setMot(Vec3D.ZERO);
            }

            vec3d = this.a(vec3d, enummovetype);
            Vec3D vec3d1 = this.g(vec3d);

            if (vec3d1.g() > 1.0E-7D) {
                this.setPosition(this.locX() + vec3d1.x, this.locY() + vec3d1.y, this.locZ() + vec3d1.z);
            }

            this.level.getMethodProfiler().exit();
            this.level.getMethodProfiler().enter("rest");
            this.horizontalCollision = !MathHelper.b(vec3d.x, vec3d1.x) || !MathHelper.b(vec3d.z, vec3d1.z);
            this.verticalCollision = vec3d.y != vec3d1.y;
            this.onGround = this.verticalCollision && vec3d.y < 0.0D;
            BlockPosition blockposition = this.av();
            IBlockData iblockdata = this.level.getType(blockposition);

            this.a(vec3d1.y, this.onGround, iblockdata, blockposition);
            if (this.isRemoved()) {
                this.level.getMethodProfiler().exit();
            } else {
                Vec3D vec3d2 = this.getMot();

                if (vec3d.x != vec3d1.x) {
                    this.setMot(0.0D, vec3d2.y, vec3d2.z);
                }

                if (vec3d.z != vec3d1.z) {
                    this.setMot(vec3d2.x, vec3d2.y, 0.0D);
                }

                Block block = iblockdata.getBlock();

                if (vec3d.y != vec3d1.y) {
                    block.a((IBlockAccess) this.level, this);
                }

                if (this.onGround && !this.bE()) {
                    block.stepOn(this.level, blockposition, iblockdata, this);
                }

                Entity.MovementEmission entity_movementemission = this.aI();

                if (entity_movementemission.a() && !this.isPassenger()) {
                    double d0 = vec3d1.x;
                    double d1 = vec3d1.y;
                    double d2 = vec3d1.z;

                    this.flyDist = (float) ((double) this.flyDist + vec3d1.f() * 0.6D);
                    if (!iblockdata.a((Tag) TagsBlock.CLIMBABLE) && !iblockdata.a(Blocks.POWDER_SNOW)) {
                        d1 = 0.0D;
                    }

                    this.walkDist += (float) vec3d1.h() * 0.6F;
                    this.moveDist += (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6F;
                    if (this.moveDist > this.nextStep && !iblockdata.isAir()) {
                        this.nextStep = this.az();
                        if (this.isInWater()) {
                            if (entity_movementemission.c()) {
                                Entity entity = this.isVehicle() && this.getRidingPassenger() != null ? this.getRidingPassenger() : this;
                                float f = entity == this ? 0.35F : 0.4F;
                                Vec3D vec3d3 = entity.getMot();
                                float f1 = Math.min(1.0F, (float) Math.sqrt(vec3d3.x * vec3d3.x * 0.20000000298023224D + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.20000000298023224D) * f);

                                this.d(f1);
                            }

                            if (entity_movementemission.b()) {
                                this.a(GameEvent.SWIM);
                            }
                        } else {
                            if (entity_movementemission.c()) {
                                this.b(iblockdata);
                                this.b(blockposition, iblockdata);
                            }

                            if (entity_movementemission.b() && !iblockdata.a((Tag) TagsBlock.OCCLUDES_VIBRATION_SIGNALS)) {
                                this.a(GameEvent.STEP);
                            }
                        }
                    } else if (iblockdata.isAir()) {
                        this.au();
                    }
                }

                this.as();
                float f2 = this.getBlockSpeedFactor();

                this.setMot(this.getMot().d((double) f2, 1.0D, (double) f2));
                if (this.level.c(this.getBoundingBox().shrink(1.0E-6D)).noneMatch((iblockdata1) -> {
                    return iblockdata1.a((Tag) TagsBlock.FIRE) || iblockdata1.a(Blocks.LAVA);
                })) {
                    if (this.remainingFireTicks <= 0) {
                        this.setFireTicks(-this.getMaxFireTicks());
                    }

                    if (this.wasOnFire && (this.isInPowderSnow || this.aN())) {
                        this.at();
                    }
                }

                if (this.isBurning() && (this.isInPowderSnow || this.aN())) {
                    this.setFireTicks(-this.getMaxFireTicks());
                }

                this.level.getMethodProfiler().exit();
            }
        }
    }

    protected void as() {
        try {
            this.checkBlockCollisions();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected void at() {
        this.playSound(SoundEffects.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
    }

    protected void au() {
        if (this.aF()) {
            this.aE();
            if (this.aI().b()) {
                this.a(GameEvent.FLAP);
            }
        }

    }

    public BlockPosition av() {
        int i = MathHelper.floor(this.position.x);
        int j = MathHelper.floor(this.position.y - 0.20000000298023224D);
        int k = MathHelper.floor(this.position.z);
        BlockPosition blockposition = new BlockPosition(i, j, k);

        if (this.level.getType(blockposition).isAir()) {
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata = this.level.getType(blockposition1);

            if (iblockdata.a((Tag) TagsBlock.FENCES) || iblockdata.a((Tag) TagsBlock.WALLS) || iblockdata.getBlock() instanceof BlockFenceGate) {
                return blockposition1;
            }
        }

        return blockposition;
    }

    protected float getBlockJumpFactor() {
        float f = this.level.getType(this.getChunkCoordinates()).getBlock().getJumpFactor();
        float f1 = this.level.getType(this.ay()).getBlock().getJumpFactor();

        return (double) f == 1.0D ? f1 : f;
    }

    protected float getBlockSpeedFactor() {
        IBlockData iblockdata = this.level.getType(this.getChunkCoordinates());
        float f = iblockdata.getBlock().getSpeedFactor();

        return !iblockdata.a(Blocks.WATER) && !iblockdata.a(Blocks.BUBBLE_COLUMN) ? ((double) f == 1.0D ? this.level.getType(this.ay()).getBlock().getSpeedFactor() : f) : f;
    }

    protected BlockPosition ay() {
        return new BlockPosition(this.position.x, this.getBoundingBox().minY - 0.5000001D, this.position.z);
    }

    protected Vec3D a(Vec3D vec3d, EnumMoveType enummovetype) {
        return vec3d;
    }

    protected Vec3D c(Vec3D vec3d) {
        if (vec3d.g() <= 1.0E-7D) {
            return vec3d;
        } else {
            long i = this.level.getTime();

            if (i != this.pistonDeltasGameTime) {
                Arrays.fill(this.pistonDeltas, 0.0D);
                this.pistonDeltasGameTime = i;
            }

            double d0;

            if (vec3d.x != 0.0D) {
                d0 = this.a(EnumDirection.EnumAxis.X, vec3d.x);
                return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3D.ZERO : new Vec3D(d0, 0.0D, 0.0D);
            } else if (vec3d.y != 0.0D) {
                d0 = this.a(EnumDirection.EnumAxis.Y, vec3d.y);
                return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3D.ZERO : new Vec3D(0.0D, d0, 0.0D);
            } else if (vec3d.z != 0.0D) {
                d0 = this.a(EnumDirection.EnumAxis.Z, vec3d.z);
                return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3D.ZERO : new Vec3D(0.0D, 0.0D, d0);
            } else {
                return Vec3D.ZERO;
            }
        }
    }

    private double a(EnumDirection.EnumAxis enumdirection_enumaxis, double d0) {
        int i = enumdirection_enumaxis.ordinal();
        double d1 = MathHelper.a(d0 + this.pistonDeltas[i], -0.51D, 0.51D);

        d0 = d1 - this.pistonDeltas[i];
        this.pistonDeltas[i] = d1;
        return d0;
    }

    private Vec3D g(Vec3D vec3d) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.a(this);
        VoxelShape voxelshape = this.level.getWorldBorder().c();
        Stream<VoxelShape> stream = VoxelShapes.c(voxelshape, VoxelShapes.a(axisalignedbb.shrink(1.0E-7D)), OperatorBoolean.AND) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = this.level.c(this, axisalignedbb.b(vec3d), (entity) -> {
            return true;
        });
        StreamAccumulator<VoxelShape> streamaccumulator = new StreamAccumulator<>(Stream.concat(stream1, stream));
        Vec3D vec3d1 = vec3d.g() == 0.0D ? vec3d : a(this, vec3d, axisalignedbb, this.level, voxelshapecollision, streamaccumulator);
        boolean flag = vec3d.x != vec3d1.x;
        boolean flag1 = vec3d.y != vec3d1.y;
        boolean flag2 = vec3d.z != vec3d1.z;
        boolean flag3 = this.onGround || flag1 && vec3d.y < 0.0D;

        if (this.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
            Vec3D vec3d2 = a(this, new Vec3D(vec3d.x, (double) this.maxUpStep, vec3d.z), axisalignedbb, this.level, voxelshapecollision, streamaccumulator);
            Vec3D vec3d3 = a(this, new Vec3D(0.0D, (double) this.maxUpStep, 0.0D), axisalignedbb.b(vec3d.x, 0.0D, vec3d.z), this.level, voxelshapecollision, streamaccumulator);

            if (vec3d3.y < (double) this.maxUpStep) {
                Vec3D vec3d4 = a(this, new Vec3D(vec3d.x, 0.0D, vec3d.z), axisalignedbb.c(vec3d3), this.level, voxelshapecollision, streamaccumulator).e(vec3d3);

                if (vec3d4.i() > vec3d2.i()) {
                    vec3d2 = vec3d4;
                }
            }

            if (vec3d2.i() > vec3d1.i()) {
                return vec3d2.e(a(this, new Vec3D(0.0D, -vec3d2.y + vec3d.y, 0.0D), axisalignedbb.c(vec3d2), this.level, voxelshapecollision, streamaccumulator));
            }
        }

        return vec3d1;
    }

    public static Vec3D a(@Nullable Entity entity, Vec3D vec3d, AxisAlignedBB axisalignedbb, World world, VoxelShapeCollision voxelshapecollision, StreamAccumulator<VoxelShape> streamaccumulator) {
        boolean flag = vec3d.x == 0.0D;
        boolean flag1 = vec3d.y == 0.0D;
        boolean flag2 = vec3d.z == 0.0D;

        if ((!flag || !flag1) && (!flag || !flag2) && (!flag1 || !flag2)) {
            StreamAccumulator<VoxelShape> streamaccumulator1 = new StreamAccumulator<>(Stream.concat(streamaccumulator.a(), world.b(entity, axisalignedbb.b(vec3d))));

            return a(vec3d, axisalignedbb, streamaccumulator1);
        } else {
            return a(vec3d, axisalignedbb, world, voxelshapecollision, streamaccumulator);
        }
    }

    public static Vec3D a(Vec3D vec3d, AxisAlignedBB axisalignedbb, StreamAccumulator<VoxelShape> streamaccumulator) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;

        if (d1 != 0.0D) {
            d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb, streamaccumulator.a(), d1);
            if (d1 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        if (flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, streamaccumulator.a(), d2);
            if (d2 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D) {
            d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb, streamaccumulator.a(), d0);
            if (!flag && d0 != 0.0D) {
                axisalignedbb = axisalignedbb.d(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, streamaccumulator.a(), d2);
        }

        return new Vec3D(d0, d1, d2);
    }

    public static Vec3D a(Vec3D vec3d, AxisAlignedBB axisalignedbb, IWorldReader iworldreader, VoxelShapeCollision voxelshapecollision, StreamAccumulator<VoxelShape> streamaccumulator) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;

        if (d1 != 0.0D) {
            d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb, iworldreader, d1, voxelshapecollision, streamaccumulator.a());
            if (d1 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        if (flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, iworldreader, d2, voxelshapecollision, streamaccumulator.a());
            if (d2 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D) {
            d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb, iworldreader, d0, voxelshapecollision, streamaccumulator.a());
            if (!flag && d0 != 0.0D) {
                axisalignedbb = axisalignedbb.d(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, iworldreader, d2, voxelshapecollision, streamaccumulator.a());
        }

        return new Vec3D(d0, d1, d2);
    }

    protected float az() {
        return (float) ((int) this.moveDist + 1);
    }

    protected SoundEffect getSoundSwim() {
        return SoundEffects.GENERIC_SWIM;
    }

    protected SoundEffect getSoundSplash() {
        return SoundEffects.GENERIC_SPLASH;
    }

    protected SoundEffect getSoundSplashHighSpeed() {
        return SoundEffects.GENERIC_SPLASH;
    }

    protected void checkBlockCollisions() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        BlockPosition blockposition = new BlockPosition(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        BlockPosition blockposition1 = new BlockPosition(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);

        if (this.level.areChunksLoadedBetween(blockposition, blockposition1)) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = blockposition.getX(); i <= blockposition1.getX(); ++i) {
                for (int j = blockposition.getY(); j <= blockposition1.getY(); ++j) {
                    for (int k = blockposition.getZ(); k <= blockposition1.getZ(); ++k) {
                        blockposition_mutableblockposition.d(i, j, k);
                        IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition);

                        try {
                            iblockdata.a(this.level, blockposition_mutableblockposition, this);
                            this.a(iblockdata);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.a(throwable, "Colliding entity with block");
                            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");

                            CrashReportSystemDetails.a(crashreportsystemdetails, this.level, blockposition_mutableblockposition, iblockdata);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }

    }

    protected void a(IBlockData iblockdata) {}

    public void a(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition) {
        this.level.a(entity, gameevent, blockposition);
    }

    public void a(GameEvent gameevent, @Nullable Entity entity) {
        this.a(gameevent, entity, this.blockPosition);
    }

    public void a(GameEvent gameevent, BlockPosition blockposition) {
        this.a(gameevent, this, blockposition);
    }

    public void a(GameEvent gameevent) {
        this.a(gameevent, this.blockPosition);
    }

    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            IBlockData iblockdata1 = this.level.getType(blockposition.up());
            SoundEffectType soundeffecttype = iblockdata1.a((Tag) TagsBlock.INSIDE_STEP_SOUND_BLOCKS) ? iblockdata1.getStepSound() : iblockdata.getStepSound();

            this.playSound(soundeffecttype.getStepSound(), soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
        }
    }

    private void b(IBlockData iblockdata) {
        if (iblockdata.a((Tag) TagsBlock.CRYSTAL_SOUND_BLOCKS) && this.tickCount >= this.lastCrystalSoundPlayTick + 20) {
            this.crystalSoundIntensity = (float) ((double) this.crystalSoundIntensity * Math.pow(0.996999979019165D, (double) (this.tickCount - this.lastCrystalSoundPlayTick)));
            this.crystalSoundIntensity = Math.min(1.0F, this.crystalSoundIntensity + 0.07F);
            float f = 0.5F + this.crystalSoundIntensity * this.random.nextFloat() * 1.2F;
            float f1 = 0.1F + this.crystalSoundIntensity * 1.2F;

            this.playSound(SoundEffects.AMETHYST_BLOCK_CHIME, f1, f);
            this.lastCrystalSoundPlayTick = this.tickCount;
        }

    }

    protected void d(float f) {
        this.playSound(this.getSoundSwim(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
    }

    protected void aE() {}

    protected boolean aF() {
        return false;
    }

    public void playSound(SoundEffect soundeffect, float f, float f1) {
        if (!this.isSilent()) {
            this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), soundeffect, this.getSoundCategory(), f, f1);
        }

    }

    public boolean isSilent() {
        return (Boolean) this.entityData.get(Entity.DATA_SILENT);
    }

    public void setSilent(boolean flag) {
        this.entityData.set(Entity.DATA_SILENT, flag);
    }

    public boolean isNoGravity() {
        return (Boolean) this.entityData.get(Entity.DATA_NO_GRAVITY);
    }

    public void setNoGravity(boolean flag) {
        this.entityData.set(Entity.DATA_NO_GRAVITY, flag);
    }

    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.ALL;
    }

    public boolean aJ() {
        return false;
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (flag) {
            if (this.fallDistance > 0.0F) {
                iblockdata.getBlock().fallOn(this.level, iblockdata, blockposition, this, this.fallDistance);
                if (!iblockdata.a((Tag) TagsBlock.OCCLUDES_VIBRATION_SIGNALS)) {
                    this.a(GameEvent.HIT_GROUND);
                }
            }

            this.fallDistance = 0.0F;
        } else if (d0 < 0.0D) {
            this.fallDistance = (float) ((double) this.fallDistance - d0);
        }

    }

    public boolean isFireProof() {
        return this.getEntityType().d();
    }

    public boolean a(float f, float f1, DamageSource damagesource) {
        if (this.isVehicle()) {
            Iterator iterator = this.getPassengers().iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                entity.a(f, f1, damagesource);
            }
        }

        return false;
    }

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    private boolean isInRain() {
        BlockPosition blockposition = this.getChunkCoordinates();

        return this.level.isRainingAt(blockposition) || this.level.isRainingAt(new BlockPosition((double) blockposition.getX(), this.getBoundingBox().maxY, (double) blockposition.getZ()));
    }

    private boolean j() {
        return this.level.getType(this.getChunkCoordinates()).a(Blocks.BUBBLE_COLUMN);
    }

    public boolean isInWaterOrRain() {
        return this.isInWater() || this.isInRain();
    }

    public boolean aN() {
        return this.isInWater() || this.isInRain() || this.j();
    }

    public boolean aO() {
        return this.isInWater() || this.j();
    }

    public boolean aP() {
        return this.wasEyeInWater && this.isInWater();
    }

    public void aQ() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        } else {
            this.setSwimming(this.isSprinting() && this.aP() && !this.isPassenger() && this.level.getFluid(this.blockPosition).a((Tag) TagsFluid.WATER));
        }

    }

    protected boolean aR() {
        this.fluidHeight.clear();
        this.aS();
        double d0 = this.level.getDimensionManager().isNether() ? 0.007D : 0.0023333333333333335D;
        boolean flag = this.a((Tag) TagsFluid.LAVA, d0);

        return this.isInWater() || flag;
    }

    void aS() {
        if (this.getVehicle() instanceof EntityBoat) {
            this.wasTouchingWater = false;
        } else if (this.a((Tag) TagsFluid.WATER, 0.014D)) {
            if (!this.wasTouchingWater && !this.firstTick) {
                this.aT();
            }

            this.fallDistance = 0.0F;
            this.wasTouchingWater = true;
            this.extinguish();
        } else {
            this.wasTouchingWater = false;
        }

    }

    private void l() {
        this.wasEyeInWater = this.a((Tag) TagsFluid.WATER);
        this.fluidOnEyes = null;
        double d0 = this.getHeadY() - 0.1111111119389534D;
        Entity entity = this.getVehicle();

        if (entity instanceof EntityBoat) {
            EntityBoat entityboat = (EntityBoat) entity;

            if (!entityboat.aP() && entityboat.getBoundingBox().maxY >= d0 && entityboat.getBoundingBox().minY <= d0) {
                return;
            }
        }

        BlockPosition blockposition = new BlockPosition(this.locX(), d0, this.locZ());
        Fluid fluid = this.level.getFluid(blockposition);
        Iterator iterator = TagsFluid.b().iterator();

        Tag tag;

        do {
            if (!iterator.hasNext()) {
                return;
            }

            tag = (Tag) iterator.next();
        } while (!fluid.a(tag));

        double d1 = (double) ((float) blockposition.getY() + fluid.getHeight(this.level, blockposition));

        if (d1 > d0) {
            this.fluidOnEyes = tag;
        }

    }

    protected void aT() {
        Entity entity = this.isVehicle() && this.getRidingPassenger() != null ? this.getRidingPassenger() : this;
        float f = entity == this ? 0.2F : 0.9F;
        Vec3D vec3d = entity.getMot();
        float f1 = Math.min(1.0F, (float) Math.sqrt(vec3d.x * vec3d.x * 0.20000000298023224D + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.20000000298023224D) * f);

        if (f1 < 0.25F) {
            this.playSound(this.getSoundSplash(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        } else {
            this.playSound(this.getSoundSplashHighSpeed(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }

        float f2 = (float) MathHelper.floor(this.locY());

        double d0;
        double d1;
        int i;

        for (i = 0; (float) i < 1.0F + this.dimensions.width * 20.0F; ++i) {
            d0 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
            d1 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
            this.level.addParticle(Particles.BUBBLE, this.locX() + d0, (double) (f2 + 1.0F), this.locZ() + d1, vec3d.x, vec3d.y - this.random.nextDouble() * 0.20000000298023224D, vec3d.z);
        }

        for (i = 0; (float) i < 1.0F + this.dimensions.width * 20.0F; ++i) {
            d0 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
            d1 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
            this.level.addParticle(Particles.SPLASH, this.locX() + d0, (double) (f2 + 1.0F), this.locZ() + d1, vec3d.x, vec3d.y, vec3d.z);
        }

        this.a(GameEvent.SPLASH);
    }

    protected IBlockData aU() {
        return this.level.getType(this.av());
    }

    public boolean aV() {
        return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.aX() && this.isAlive();
    }

    protected void aW() {
        int i = MathHelper.floor(this.locX());
        int j = MathHelper.floor(this.locY() - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ());
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = this.level.getType(blockposition);

        if (iblockdata.h() != EnumRenderType.INVISIBLE) {
            Vec3D vec3d = this.getMot();

            this.level.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX() + (this.random.nextDouble() - 0.5D) * (double) this.dimensions.width, this.locY() + 0.1D, this.locZ() + (this.random.nextDouble() - 0.5D) * (double) this.dimensions.width, vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
        }

    }

    public boolean a(Tag<FluidType> tag) {
        return this.fluidOnEyes == tag;
    }

    public boolean aX() {
        return !this.firstTick && this.fluidHeight.getDouble(TagsFluid.LAVA) > 0.0D;
    }

    public void a(float f, Vec3D vec3d) {
        Vec3D vec3d1 = a(vec3d, f, this.getYRot());

        this.setMot(this.getMot().e(vec3d1));
    }

    private static Vec3D a(Vec3D vec3d, float f, float f1) {
        double d0 = vec3d.g();

        if (d0 < 1.0E-7D) {
            return Vec3D.ZERO;
        } else {
            Vec3D vec3d1 = (d0 > 1.0D ? vec3d.d() : vec3d).a((double) f);
            float f2 = MathHelper.sin(f1 * 0.017453292F);
            float f3 = MathHelper.cos(f1 * 0.017453292F);

            return new Vec3D(vec3d1.x * (double) f3 - vec3d1.z * (double) f2, vec3d1.y, vec3d1.z * (double) f3 + vec3d1.x * (double) f2);
        }
    }

    public float aY() {
        return this.level.e(this.cW(), this.dc()) ? this.level.z(new BlockPosition(this.locX(), this.getHeadY(), this.locZ())) : 0.0F;
    }

    public void setLocation(double d0, double d1, double d2, float f, float f1) {
        this.g(d0, d1, d2);
        this.setYRot(f % 360.0F);
        this.setXRot(MathHelper.a(f1, -90.0F, 90.0F) % 360.0F);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void g(double d0, double d1, double d2) {
        double d3 = MathHelper.a(d0, -3.0E7D, 3.0E7D);
        double d4 = MathHelper.a(d2, -3.0E7D, 3.0E7D);

        this.xo = d3;
        this.yo = d1;
        this.zo = d4;
        this.setPosition(d3, d1, d4);
    }

    public void d(Vec3D vec3d) {
        this.teleportAndSync(vec3d.x, vec3d.y, vec3d.z);
    }

    public void teleportAndSync(double d0, double d1, double d2) {
        this.setPositionRotation(d0, d1, d2, this.getYRot(), this.getXRot());
    }

    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, f, f1);
    }

    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.setPositionRaw(d0, d1, d2);
        this.setYRot(f);
        this.setXRot(f1);
        this.aZ();
        this.ah();
    }

    public final void aZ() {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();

        this.xo = d0;
        this.yo = d1;
        this.zo = d2;
        this.xOld = d0;
        this.yOld = d1;
        this.zOld = d2;
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public float e(Entity entity) {
        float f = (float) (this.locX() - entity.locX());
        float f1 = (float) (this.locY() - entity.locY());
        float f2 = (float) (this.locZ() - entity.locZ());

        return MathHelper.c(f * f + f1 * f1 + f2 * f2);
    }

    public double h(double d0, double d1, double d2) {
        double d3 = this.locX() - d0;
        double d4 = this.locY() - d1;
        double d5 = this.locZ() - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double f(Entity entity) {
        return this.e(entity.getPositionVector());
    }

    public double e(Vec3D vec3d) {
        double d0 = this.locX() - vec3d.x;
        double d1 = this.locY() - vec3d.y;
        double d2 = this.locZ() - vec3d.z;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void pickup(EntityHuman entityhuman) {}

    public void collide(Entity entity) {
        if (!this.isSameVehicle(entity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d0 = entity.locX() - this.locX();
                double d1 = entity.locZ() - this.locZ();
                double d2 = MathHelper.a(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05000000074505806D;
                    d1 *= 0.05000000074505806D;
                    if (!this.isVehicle()) {
                        this.i(-d0, 0.0D, -d1);
                    }

                    if (!entity.isVehicle()) {
                        entity.i(d0, 0.0D, d1);
                    }
                }

            }
        }
    }

    public void i(double d0, double d1, double d2) {
        this.setMot(this.getMot().add(d0, d1, d2));
        this.hasImpulse = true;
    }

    protected void velocityChanged() {
        this.hurtMarked = true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.velocityChanged();
            return false;
        }
    }

    public final Vec3D e(float f) {
        return this.b(this.f(f), this.g(f));
    }

    public float f(float f) {
        return f == 1.0F ? this.getXRot() : MathHelper.h(f, this.xRotO, this.getXRot());
    }

    public float g(float f) {
        return f == 1.0F ? this.getYRot() : MathHelper.h(f, this.yRotO, this.getYRot());
    }

    protected final Vec3D b(float f, float f1) {
        float f2 = f * 0.017453292F;
        float f3 = -f1 * 0.017453292F;
        float f4 = MathHelper.cos(f3);
        float f5 = MathHelper.sin(f3);
        float f6 = MathHelper.cos(f2);
        float f7 = MathHelper.sin(f2);

        return new Vec3D((double) (f5 * f6), (double) (-f7), (double) (f4 * f6));
    }

    public final Vec3D h(float f) {
        return this.c(this.f(f), this.g(f));
    }

    protected final Vec3D c(float f, float f1) {
        return this.b(f - 90.0F, f1);
    }

    public final Vec3D bb() {
        return new Vec3D(this.locX(), this.getHeadY(), this.locZ());
    }

    public final Vec3D i(float f) {
        double d0 = MathHelper.d((double) f, this.xo, this.locX());
        double d1 = MathHelper.d((double) f, this.yo, this.locY()) + (double) this.getHeadHeight();
        double d2 = MathHelper.d((double) f, this.zo, this.locZ());

        return new Vec3D(d0, d1, d2);
    }

    public Vec3D j(float f) {
        return this.i(f);
    }

    public final Vec3D k(float f) {
        double d0 = MathHelper.d((double) f, this.xo, this.locX());
        double d1 = MathHelper.d((double) f, this.yo, this.locY());
        double d2 = MathHelper.d((double) f, this.zo, this.locZ());

        return new Vec3D(d0, d1, d2);
    }

    public MovingObjectPosition a(double d0, float f, boolean flag) {
        Vec3D vec3d = this.i(f);
        Vec3D vec3d1 = this.e(f);
        Vec3D vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);

        return this.level.rayTrace(new RayTrace(vec3d, vec3d2, RayTrace.BlockCollisionOption.OUTLINE, flag ? RayTrace.FluidCollisionOption.ANY : RayTrace.FluidCollisionOption.NONE, this));
    }

    public boolean isInteractable() {
        return false;
    }

    public boolean isCollidable() {
        return false;
    }

    public void a(Entity entity, int i, DamageSource damagesource) {
        if (entity instanceof EntityPlayer) {
            CriterionTriggers.ENTITY_KILLED_PLAYER.a((EntityPlayer) entity, this, damagesource);
        }

    }

    public boolean j(double d0, double d1, double d2) {
        double d3 = this.locX() - d0;
        double d4 = this.locY() - d1;
        double d5 = this.locZ() - d2;
        double d6 = d3 * d3 + d4 * d4 + d5 * d5;

        return this.a(d6);
    }

    public boolean a(double d0) {
        double d1 = this.getBoundingBox().a();

        if (Double.isNaN(d1)) {
            d1 = 1.0D;
        }

        d1 *= 64.0D * Entity.viewScale;
        return d0 < d1 * d1;
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        if (this.removalReason != null && !this.removalReason.b()) {
            return false;
        } else {
            String s = this.getSaveID();

            if (s == null) {
                return false;
            } else {
                nbttagcompound.setString("id", s);
                this.save(nbttagcompound);
                return true;
            }
        }
    }

    public boolean e(NBTTagCompound nbttagcompound) {
        return this.isPassenger() ? false : this.d(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        try {
            if (this.vehicle != null) {
                nbttagcompound.set("Pos", this.newDoubleList(this.vehicle.locX(), this.locY(), this.vehicle.locZ()));
            } else {
                nbttagcompound.set("Pos", this.newDoubleList(this.locX(), this.locY(), this.locZ()));
            }

            Vec3D vec3d = this.getMot();

            nbttagcompound.set("Motion", this.newDoubleList(vec3d.x, vec3d.y, vec3d.z));
            nbttagcompound.set("Rotation", this.newFloatList(this.getYRot(), this.getXRot()));
            nbttagcompound.setFloat("FallDistance", this.fallDistance);
            nbttagcompound.setShort("Fire", (short) this.remainingFireTicks);
            nbttagcompound.setShort("Air", (short) this.getAirTicks());
            nbttagcompound.setBoolean("OnGround", this.onGround);
            nbttagcompound.setBoolean("Invulnerable", this.invulnerable);
            nbttagcompound.setInt("PortalCooldown", this.portalCooldown);
            nbttagcompound.a("UUID", this.getUniqueID());
            IChatBaseComponent ichatbasecomponent = this.getCustomName();

            if (ichatbasecomponent != null) {
                nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
            }

            if (this.getCustomNameVisible()) {
                nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
            }

            if (this.isSilent()) {
                nbttagcompound.setBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                nbttagcompound.setBoolean("NoGravity", this.isNoGravity());
            }

            if (this.hasGlowingTag) {
                nbttagcompound.setBoolean("Glowing", true);
            }

            int i = this.getTicksFrozen();

            if (i > 0) {
                nbttagcompound.setInt("TicksFrozen", this.getTicksFrozen());
            }

            if (this.hasVisualFire) {
                nbttagcompound.setBoolean("HasVisualFire", this.hasVisualFire);
            }

            NBTTagList nbttaglist;
            Iterator iterator;

            if (!this.tags.isEmpty()) {
                nbttaglist = new NBTTagList();
                iterator = this.tags.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    nbttaglist.add(NBTTagString.a(s));
                }

                nbttagcompound.set("Tags", nbttaglist);
            }

            this.saveData(nbttagcompound);
            if (this.isVehicle()) {
                nbttaglist = new NBTTagList();
                iterator = this.getPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    if (entity.d(nbttagcompound1)) {
                        nbttaglist.add(nbttagcompound1);
                    }
                }

                if (!nbttaglist.isEmpty()) {
                    nbttagcompound.set("Passengers", nbttaglist);
                }
            }

            return nbttagcompound;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Saving entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    public void load(NBTTagCompound nbttagcompound) {
        try {
            NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
            NBTTagList nbttaglist1 = nbttagcompound.getList("Motion", 6);
            NBTTagList nbttaglist2 = nbttagcompound.getList("Rotation", 5);
            double d0 = nbttaglist1.h(0);
            double d1 = nbttaglist1.h(1);
            double d2 = nbttaglist1.h(2);

            this.setMot(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
            this.setPositionRaw(nbttaglist.h(0), MathHelper.a(nbttaglist.h(1), -2.0E7D, 2.0E7D), nbttaglist.h(2));
            this.setYRot(nbttaglist2.i(0));
            this.setXRot(nbttaglist2.i(1));
            this.aZ();
            this.setHeadRotation(this.getYRot());
            this.m(this.getYRot());
            this.fallDistance = nbttagcompound.getFloat("FallDistance");
            this.remainingFireTicks = nbttagcompound.getShort("Fire");
            if (nbttagcompound.hasKey("Air")) {
                this.setAirTicks(nbttagcompound.getShort("Air"));
            }

            this.onGround = nbttagcompound.getBoolean("OnGround");
            this.invulnerable = nbttagcompound.getBoolean("Invulnerable");
            this.portalCooldown = nbttagcompound.getInt("PortalCooldown");
            if (nbttagcompound.b("UUID")) {
                this.uuid = nbttagcompound.a("UUID");
                this.stringUUID = this.uuid.toString();
            }

            if (Double.isFinite(this.locX()) && Double.isFinite(this.locY()) && Double.isFinite(this.locZ())) {
                if (Double.isFinite((double) this.getYRot()) && Double.isFinite((double) this.getXRot())) {
                    this.ah();
                    this.setYawPitch(this.getYRot(), this.getXRot());
                    if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
                        String s = nbttagcompound.getString("CustomName");

                        try {
                            this.setCustomName(IChatBaseComponent.ChatSerializer.a(s));
                        } catch (Exception exception) {
                            Entity.LOGGER.warn("Failed to parse entity custom name {}", s, exception);
                        }
                    }

                    this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
                    this.setSilent(nbttagcompound.getBoolean("Silent"));
                    this.setNoGravity(nbttagcompound.getBoolean("NoGravity"));
                    this.setGlowingTag(nbttagcompound.getBoolean("Glowing"));
                    this.setTicksFrozen(nbttagcompound.getInt("TicksFrozen"));
                    this.hasVisualFire = nbttagcompound.getBoolean("HasVisualFire");
                    if (nbttagcompound.hasKeyOfType("Tags", 9)) {
                        this.tags.clear();
                        NBTTagList nbttaglist3 = nbttagcompound.getList("Tags", 8);
                        int i = Math.min(nbttaglist3.size(), 1024);

                        for (int j = 0; j < i; ++j) {
                            this.tags.add(nbttaglist3.getString(j));
                        }
                    }

                    this.loadData(nbttagcompound);
                    if (this.be()) {
                        this.ah();
                    }

                } else {
                    throw new IllegalStateException("Entity has invalid rotation");
                }
            } else {
                throw new IllegalStateException("Entity has invalid position");
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Loading entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean be() {
        return true;
    }

    @Nullable
    public final String getSaveID() {
        EntityTypes<?> entitytypes = this.getEntityType();
        MinecraftKey minecraftkey = EntityTypes.getName(entitytypes);

        return entitytypes.b() && minecraftkey != null ? minecraftkey.toString() : null;
    }

    protected abstract void loadData(NBTTagCompound nbttagcompound);

    protected abstract void saveData(NBTTagCompound nbttagcompound);

    protected NBTTagList newDoubleList(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add(NBTTagDouble.a(d0));
        }

        return nbttaglist;
    }

    protected NBTTagList newFloatList(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            nbttaglist.add(NBTTagFloat.a(f));
        }

        return nbttaglist;
    }

    @Nullable
    public EntityItem a(IMaterial imaterial) {
        return this.a(imaterial, 0);
    }

    @Nullable
    public EntityItem a(IMaterial imaterial, int i) {
        return this.a(new ItemStack(imaterial), (float) i);
    }

    @Nullable
    public EntityItem b(ItemStack itemstack) {
        return this.a(itemstack, 0.0F);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack, float f) {
        if (itemstack.isEmpty()) {
            return null;
        } else if (this.level.isClientSide) {
            return null;
        } else {
            EntityItem entityitem = new EntityItem(this.level, this.locX(), this.locY() + (double) f, this.locZ(), itemstack);

            entityitem.defaultPickupDelay();
            this.level.addEntity(entityitem);
            return entityitem;
        }
    }

    public boolean isAlive() {
        return !this.isRemoved();
    }

    public boolean inBlock() {
        if (this.noPhysics) {
            return false;
        } else {
            float f = this.dimensions.width * 0.8F;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.a(this.bb(), (double) f, 1.0E-6D, (double) f);

            return this.level.b(this, axisalignedbb, (iblockdata, blockposition) -> {
                return iblockdata.o(this.level, blockposition);
            }).findAny().isPresent();
        }
    }

    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public boolean h(Entity entity) {
        return entity.bi() && !this.isSameVehicle(entity);
    }

    public boolean bi() {
        return false;
    }

    public void passengerTick() {
        this.setMot(Vec3D.ZERO);
        this.tick();
        if (this.isPassenger()) {
            this.getVehicle().i(this);
        }
    }

    public void i(Entity entity) {
        this.a(entity, Entity::setPosition);
    }

    private void a(Entity entity, Entity.MoveFunction entity_movefunction) {
        if (this.u(entity)) {
            double d0 = this.locY() + this.bl() + entity.bk();

            entity_movefunction.accept(entity, this.locX(), d0, this.locZ());
        }
    }

    public void j(Entity entity) {}

    public double bk() {
        return 0.0D;
    }

    public double bl() {
        return (double) this.dimensions.height * 0.75D;
    }

    public boolean startRiding(Entity entity) {
        return this.a(entity, false);
    }

    public boolean bm() {
        return this instanceof EntityLiving;
    }

    public boolean a(Entity entity, boolean flag) {
        if (entity == this.vehicle) {
            return false;
        } else {
            for (Entity entity1 = entity; entity1.vehicle != null; entity1 = entity1.vehicle) {
                if (entity1.vehicle == this) {
                    return false;
                }
            }

            if (!flag && (!this.l(entity) || !entity.o(this))) {
                return false;
            } else {
                if (this.isPassenger()) {
                    this.stopRiding();
                }

                this.setPose(EntityPose.STANDING);
                this.vehicle = entity;
                this.vehicle.addPassenger(this);
                entity.n().filter((entity2) -> {
                    return entity2 instanceof EntityPlayer;
                }).forEach((entity2) -> {
                    CriterionTriggers.START_RIDING_TRIGGER.a((EntityPlayer) entity2);
                });
                return true;
            }
        }
    }

    protected boolean l(Entity entity) {
        return !this.isSneaking() && this.boardingCooldown <= 0;
    }

    protected boolean c(EntityPose entitypose) {
        return this.level.getCubes(this, this.d(entitypose).shrink(1.0E-7D));
    }

    public void ejectPassengers() {
        for (int i = this.passengers.size() - 1; i >= 0; --i) {
            ((Entity) this.passengers.get(i)).stopRiding();
        }

    }

    public void bo() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;

            this.vehicle = null;
            entity.removePassenger(this);
        }

    }

    public void stopRiding() {
        this.bo();
    }

    protected void addPassenger(Entity entity) {
        if (entity.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        } else {
            if (this.passengers.isEmpty()) {
                this.passengers = ImmutableList.of(entity);
            } else {
                List<Entity> list = Lists.newArrayList(this.passengers);

                if (!this.level.isClientSide && entity instanceof EntityHuman && !(this.getRidingPassenger() instanceof EntityHuman)) {
                    list.add(0, entity);
                } else {
                    list.add(entity);
                }

                this.passengers = ImmutableList.copyOf(list);
            }

        }
    }

    protected void removePassenger(Entity entity) {
        if (entity.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        } else {
            if (this.passengers.size() == 1 && this.passengers.get(0) == entity) {
                this.passengers = ImmutableList.of();
            } else {
                this.passengers = (ImmutableList) this.passengers.stream().filter((entity1) -> {
                    return entity1 != entity;
                }).collect(ImmutableList.toImmutableList());
            }

            entity.boardingCooldown = 60;
        }
    }

    protected boolean o(Entity entity) {
        return this.passengers.isEmpty();
    }

    public void a(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.setPosition(d0, d1, d2);
        this.setYawPitch(f, f1);
    }

    public void a(float f, int i) {
        this.setHeadRotation(f);
    }

    public float bp() {
        return 0.0F;
    }

    public Vec3D getLookDirection() {
        return this.b(this.getXRot(), this.getYRot());
    }

    public Vec2F br() {
        return new Vec2F(this.getXRot(), this.getYRot());
    }

    public Vec3D bs() {
        return Vec3D.a(this.br());
    }

    public void d(BlockPosition blockposition) {
        if (this.al()) {
            this.resetPortalCooldown();
        } else {
            if (!this.level.isClientSide && !blockposition.equals(this.portalEntrancePos)) {
                this.portalEntrancePos = blockposition.immutableCopy();
            }

            this.isInsidePortal = true;
        }
    }

    protected void doPortalTick() {
        if (this.level instanceof WorldServer) {
            int i = this.am();
            WorldServer worldserver = (WorldServer) this.level;

            if (this.isInsidePortal) {
                MinecraftServer minecraftserver = worldserver.getMinecraftServer();
                ResourceKey<World> resourcekey = this.level.getDimensionKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
                WorldServer worldserver1 = minecraftserver.getWorldServer(resourcekey);

                if (worldserver1 != null && minecraftserver.getAllowNether() && !this.isPassenger() && this.portalTime++ >= i) {
                    this.level.getMethodProfiler().enter("portal");
                    this.portalTime = i;
                    this.resetPortalCooldown();
                    this.b(worldserver1);
                    this.level.getMethodProfiler().exit();
                }

                this.isInsidePortal = false;
            } else {
                if (this.portalTime > 0) {
                    this.portalTime -= 4;
                }

                if (this.portalTime < 0) {
                    this.portalTime = 0;
                }
            }

            this.E();
        }
    }

    public int getDefaultPortalCooldown() {
        return 300;
    }

    public void k(double d0, double d1, double d2) {
        this.setMot(d0, d1, d2);
    }

    public void a(byte b0) {
        switch (b0) {
            case 53:
                BlockHoney.a(this);
            default:
        }
    }

    public void bv() {}

    public Iterable<ItemStack> bw() {
        return Entity.EMPTY_LIST;
    }

    public Iterable<ItemStack> getArmorItems() {
        return Entity.EMPTY_LIST;
    }

    public Iterable<ItemStack> by() {
        return Iterables.concat(this.bw(), this.getArmorItems());
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {}

    public boolean isBurning() {
        boolean flag = this.level != null && this.level.isClientSide;

        return !this.isFireProof() && (this.remainingFireTicks > 0 || flag && this.getFlag(0));
    }

    public boolean isPassenger() {
        return this.getVehicle() != null;
    }

    public boolean isVehicle() {
        return !this.passengers.isEmpty();
    }

    public boolean bC() {
        return true;
    }

    public void setSneaking(boolean flag) {
        this.setFlag(1, flag);
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public boolean bE() {
        return this.isSneaking();
    }

    public boolean bF() {
        return this.isSneaking();
    }

    public boolean bG() {
        return this.isSneaking();
    }

    public boolean bH() {
        return this.isSneaking();
    }

    public boolean isCrouching() {
        return this.getPose() == EntityPose.CROUCHING;
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean flag) {
        this.setFlag(3, flag);
    }

    public boolean isSwimming() {
        return this.getFlag(4);
    }

    public boolean bL() {
        return this.getPose() == EntityPose.SWIMMING;
    }

    public boolean bM() {
        return this.bL() && !this.isInWater();
    }

    public void setSwimming(boolean flag) {
        this.setFlag(4, flag);
    }

    public final boolean hasGlowingTag() {
        return this.hasGlowingTag;
    }

    public final void setGlowingTag(boolean flag) {
        this.hasGlowingTag = flag;
        this.setFlag(6, this.isCurrentlyGlowing());
    }

    public boolean isCurrentlyGlowing() {
        return this.level.isClientSide() ? this.getFlag(6) : this.hasGlowingTag;
    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    public boolean c(EntityHuman entityhuman) {
        if (entityhuman.isSpectator()) {
            return false;
        } else {
            ScoreboardTeamBase scoreboardteambase = this.getScoreboardTeam();

            return scoreboardteambase != null && entityhuman != null && entityhuman.getScoreboardTeam() == scoreboardteambase && scoreboardteambase.canSeeFriendlyInvisibles() ? false : this.isInvisible();
        }
    }

    @Nullable
    public GameEventListenerRegistrar bQ() {
        return null;
    }

    @Nullable
    public ScoreboardTeamBase getScoreboardTeam() {
        return this.level.getScoreboard().getPlayerTeam(this.getName());
    }

    public boolean p(Entity entity) {
        return this.a(entity.getScoreboardTeam());
    }

    public boolean a(ScoreboardTeamBase scoreboardteambase) {
        return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isAlly(scoreboardteambase) : false;
    }

    public void setInvisible(boolean flag) {
        this.setFlag(5, flag);
    }

    public boolean getFlag(int i) {
        return ((Byte) this.entityData.get(Entity.DATA_SHARED_FLAGS_ID) & 1 << i) != 0;
    }

    public void setFlag(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(Entity.DATA_SHARED_FLAGS_ID);

        if (flag) {
            this.entityData.set(Entity.DATA_SHARED_FLAGS_ID, (byte) (b0 | 1 << i));
        } else {
            this.entityData.set(Entity.DATA_SHARED_FLAGS_ID, (byte) (b0 & ~(1 << i)));
        }

    }

    public int bS() {
        return 300;
    }

    public int getAirTicks() {
        return (Integer) this.entityData.get(Entity.DATA_AIR_SUPPLY_ID);
    }

    public void setAirTicks(int i) {
        this.entityData.set(Entity.DATA_AIR_SUPPLY_ID, i);
    }

    public int getTicksFrozen() {
        return (Integer) this.entityData.get(Entity.DATA_TICKS_FROZEN);
    }

    public void setTicksFrozen(int i) {
        this.entityData.set(Entity.DATA_TICKS_FROZEN, i);
    }

    public float bV() {
        int i = this.getTicksRequiredToFreeze();

        return (float) Math.min(this.getTicksFrozen(), i) / (float) i;
    }

    public boolean isFullyFrozen() {
        return this.getTicksFrozen() >= this.getTicksRequiredToFreeze();
    }

    public int getTicksRequiredToFreeze() {
        return 140;
    }

    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {
        this.setFireTicks(this.remainingFireTicks + 1);
        if (this.remainingFireTicks == 0) {
            this.setOnFire(8);
        }

        this.damageEntity(DamageSource.LIGHTNING_BOLT, 5.0F);
    }

    public void k(boolean flag) {
        Vec3D vec3d = this.getMot();
        double d0;

        if (flag) {
            d0 = Math.max(-0.9D, vec3d.y - 0.03D);
        } else {
            d0 = Math.min(1.8D, vec3d.y + 0.1D);
        }

        this.setMot(vec3d.x, d0, vec3d.z);
    }

    public void l(boolean flag) {
        Vec3D vec3d = this.getMot();
        double d0;

        if (flag) {
            d0 = Math.max(-0.3D, vec3d.y - 0.03D);
        } else {
            d0 = Math.min(0.7D, vec3d.y + 0.06D);
        }

        this.setMot(vec3d.x, d0, vec3d.z);
        this.fallDistance = 0.0F;
    }

    public void a(WorldServer worldserver, EntityLiving entityliving) {}

    protected void l(double d0, double d1, double d2) {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        Vec3D vec3d = new Vec3D(d0 - (double) blockposition.getX(), d1 - (double) blockposition.getY(), d2 - (double) blockposition.getZ());
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection enumdirection = EnumDirection.UP;
        double d3 = Double.MAX_VALUE;
        EnumDirection[] aenumdirection = new EnumDirection[]{EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.EAST, EnumDirection.UP};
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection1);
            if (!this.level.getType(blockposition_mutableblockposition).r(this.level, blockposition_mutableblockposition)) {
                double d4 = vec3d.a(enumdirection1.n());
                double d5 = enumdirection1.e() == EnumDirection.EnumAxisDirection.POSITIVE ? 1.0D - d4 : d4;

                if (d5 < d3) {
                    d3 = d5;
                    enumdirection = enumdirection1;
                }
            }
        }

        float f = this.random.nextFloat() * 0.2F + 0.1F;
        float f1 = (float) enumdirection.e().a();
        Vec3D vec3d1 = this.getMot().a(0.75D);

        if (enumdirection.n() == EnumDirection.EnumAxis.X) {
            this.setMot((double) (f1 * f), vec3d1.y, vec3d1.z);
        } else if (enumdirection.n() == EnumDirection.EnumAxis.Y) {
            this.setMot(vec3d1.x, (double) (f1 * f), vec3d1.z);
        } else if (enumdirection.n() == EnumDirection.EnumAxis.Z) {
            this.setMot(vec3d1.x, vec3d1.y, (double) (f1 * f));
        }

    }

    public void a(IBlockData iblockdata, Vec3D vec3d) {
        this.fallDistance = 0.0F;
        this.stuckSpeedMultiplier = vec3d;
    }

    private static IChatBaseComponent b(IChatBaseComponent ichatbasecomponent) {
        IChatMutableComponent ichatmutablecomponent = ichatbasecomponent.g().setChatModifier(ichatbasecomponent.getChatModifier().setChatClickable((ChatClickable) null));
        Iterator iterator = ichatbasecomponent.getSiblings().iterator();

        while (iterator.hasNext()) {
            IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) iterator.next();

            ichatmutablecomponent.addSibling(b(ichatbasecomponent1));
        }

        return ichatmutablecomponent;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return ichatbasecomponent != null ? b(ichatbasecomponent) : this.bY();
    }

    protected IChatBaseComponent bY() {
        return this.type.h();
    }

    public boolean q(Entity entity) {
        return this == entity;
    }

    public float getHeadRotation() {
        return 0.0F;
    }

    public void setHeadRotation(float f) {}

    public void m(float f) {}

    public boolean ca() {
        return true;
    }

    public boolean r(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getDisplayName().getString(), this.id, this.level == null ? "~NULL~" : this.level.toString(), this.locX(), this.locY(), this.locZ());
    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return this.isRemoved() || this.invulnerable && damagesource != DamageSource.OUT_OF_WORLD && !damagesource.B();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean flag) {
        this.invulnerable = flag;
    }

    public void s(Entity entity) {
        this.setPositionRotation(entity.locX(), entity.locY(), entity.locZ(), entity.getYRot(), entity.getXRot());
    }

    public void t(Entity entity) {
        NBTTagCompound nbttagcompound = entity.save(new NBTTagCompound());

        nbttagcompound.remove("Dimension");
        this.load(nbttagcompound);
        this.portalCooldown = entity.portalCooldown;
        this.portalEntrancePos = entity.portalEntrancePos;
    }

    @Nullable
    public Entity b(WorldServer worldserver) {
        if (this.level instanceof WorldServer && !this.isRemoved()) {
            this.level.getMethodProfiler().enter("changeDimension");
            this.decouple();
            this.level.getMethodProfiler().enter("reposition");
            ShapeDetectorShape shapedetectorshape = this.a(worldserver);

            if (shapedetectorshape == null) {
                return null;
            } else {
                this.level.getMethodProfiler().exitEnter("reloading");
                Entity entity = this.getEntityType().a((World) worldserver);

                if (entity != null) {
                    entity.t(this);
                    entity.setPositionRotation(shapedetectorshape.pos.x, shapedetectorshape.pos.y, shapedetectorshape.pos.z, shapedetectorshape.yRot, entity.getXRot());
                    entity.setMot(shapedetectorshape.speed);
                    worldserver.addEntityTeleport(entity);
                    if (worldserver.getDimensionKey() == World.END) {
                        WorldServer.a(worldserver);
                    }
                }

                this.cc();
                this.level.getMethodProfiler().exit();
                ((WorldServer) this.level).resetEmptyTime();
                worldserver.resetEmptyTime();
                this.level.getMethodProfiler().exit();
                return entity;
            }
        } else {
            return null;
        }
    }

    protected void cc() {
        this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
    }

    @Nullable
    protected ShapeDetectorShape a(WorldServer worldserver) {
        boolean flag = this.level.getDimensionKey() == World.END && worldserver.getDimensionKey() == World.OVERWORLD;
        boolean flag1 = worldserver.getDimensionKey() == World.END;

        if (!flag && !flag1) {
            boolean flag2 = worldserver.getDimensionKey() == World.NETHER;

            if (this.level.getDimensionKey() != World.NETHER && !flag2) {
                return null;
            } else {
                WorldBorder worldborder = worldserver.getWorldBorder();
                double d0 = Math.max(-2.9999872E7D, worldborder.e() + 16.0D);
                double d1 = Math.max(-2.9999872E7D, worldborder.f() + 16.0D);
                double d2 = Math.min(2.9999872E7D, worldborder.g() - 16.0D);
                double d3 = Math.min(2.9999872E7D, worldborder.h() - 16.0D);
                double d4 = DimensionManager.a(this.level.getDimensionManager(), worldserver.getDimensionManager());
                BlockPosition blockposition = new BlockPosition(MathHelper.a(this.locX() * d4, d0, d2), this.locY(), MathHelper.a(this.locZ() * d4, d1, d3));

                return (ShapeDetectorShape) this.findOrCreatePortal(worldserver, blockposition, flag2).map((blockutil_rectangle) -> {
                    IBlockData iblockdata = this.level.getType(this.portalEntrancePos);
                    EnumDirection.EnumAxis enumdirection_enumaxis;
                    Vec3D vec3d;

                    if (iblockdata.b(BlockProperties.HORIZONTAL_AXIS)) {
                        enumdirection_enumaxis = (EnumDirection.EnumAxis) iblockdata.get(BlockProperties.HORIZONTAL_AXIS);
                        BlockUtil.Rectangle blockutil_rectangle1 = BlockUtil.a(this.portalEntrancePos, enumdirection_enumaxis, 21, EnumDirection.EnumAxis.Y, 21, (blockposition1) -> {
                            return this.level.getType(blockposition1) == iblockdata;
                        });

                        vec3d = this.a(enumdirection_enumaxis, blockutil_rectangle1);
                    } else {
                        enumdirection_enumaxis = EnumDirection.EnumAxis.X;
                        vec3d = new Vec3D(0.5D, 0.0D, 0.0D);
                    }

                    return BlockPortalShape.a(worldserver, blockutil_rectangle, enumdirection_enumaxis, vec3d, this.a(this.getPose()), this.getMot(), this.getYRot(), this.getXRot());
                }).orElse((Object) null);
            }
        } else {
            BlockPosition blockposition1;

            if (flag1) {
                blockposition1 = WorldServer.END_SPAWN_POINT;
            } else {
                blockposition1 = worldserver.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, worldserver.getSpawn());
            }

            return new ShapeDetectorShape(new Vec3D((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D), this.getMot(), this.getYRot(), this.getXRot());
        }
    }

    protected Vec3D a(EnumDirection.EnumAxis enumdirection_enumaxis, BlockUtil.Rectangle blockutil_rectangle) {
        return BlockPortalShape.a(blockutil_rectangle, enumdirection_enumaxis, this.getPositionVector(), this.a(this.getPose()));
    }

    protected Optional<BlockUtil.Rectangle> findOrCreatePortal(WorldServer worldserver, BlockPosition blockposition, boolean flag) {
        return worldserver.getTravelAgent().findPortal(blockposition, flag);
    }

    public boolean canPortal() {
        return true;
    }

    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return f;
    }

    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }

    public int ce() {
        return 3;
    }

    public boolean isIgnoreBlockTrigger() {
        return false;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Entity Type", () -> {
            MinecraftKey minecraftkey = EntityTypes.getName(this.getEntityType());

            return minecraftkey + " (" + this.getClass().getCanonicalName() + ")";
        });
        crashreportsystemdetails.a("Entity ID", (Object) this.id);
        crashreportsystemdetails.a("Entity Name", () -> {
            return this.getDisplayName().getString();
        });
        crashreportsystemdetails.a("Entity's Exact location", (Object) String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.locX(), this.locY(), this.locZ()));
        crashreportsystemdetails.a("Entity's Block location", (Object) CrashReportSystemDetails.a(this.level, MathHelper.floor(this.locX()), MathHelper.floor(this.locY()), MathHelper.floor(this.locZ())));
        Vec3D vec3d = this.getMot();

        crashreportsystemdetails.a("Entity's Momentum", (Object) String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
        crashreportsystemdetails.a("Entity's Passengers", () -> {
            return this.getPassengers().toString();
        });
        crashreportsystemdetails.a("Entity's Vehicle", () -> {
            return String.valueOf(this.getVehicle());
        });
    }

    public boolean cg() {
        return this.isBurning() && !this.isSpectator();
    }

    public void a_(UUID uuid) {
        this.uuid = uuid;
        this.stringUUID = this.uuid.toString();
    }

    @Override
    public UUID getUniqueID() {
        return this.uuid;
    }

    public String getUniqueIDString() {
        return this.stringUUID;
    }

    public String getName() {
        return this.stringUUID;
    }

    public boolean ck() {
        return true;
    }

    public static double cl() {
        return Entity.viewScale;
    }

    public static void b(double d0) {
        Entity.viewScale = d0;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return ScoreboardTeam.a(this.getScoreboardTeam(), this.getDisplayName()).format((chatmodifier) -> {
            return chatmodifier.setChatHoverable(this.cq()).setInsertion(this.getUniqueIDString());
        });
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.entityData.set(Entity.DATA_CUSTOM_NAME, Optional.ofNullable(ichatbasecomponent));
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return (IChatBaseComponent) ((Optional) this.entityData.get(Entity.DATA_CUSTOM_NAME)).orElse((Object) null);
    }

    @Override
    public boolean hasCustomName() {
        return ((Optional) this.entityData.get(Entity.DATA_CUSTOM_NAME)).isPresent();
    }

    public void setCustomNameVisible(boolean flag) {
        this.entityData.set(Entity.DATA_CUSTOM_NAME_VISIBLE, flag);
    }

    public boolean getCustomNameVisible() {
        return (Boolean) this.entityData.get(Entity.DATA_CUSTOM_NAME_VISIBLE);
    }

    public final void enderTeleportAndLoad(double d0, double d1, double d2) {
        if (this.level instanceof WorldServer) {
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(d0, d1, d2));

            ((WorldServer) this.level).getChunkProvider().addTicket(TicketType.POST_TELEPORT, chunkcoordintpair, 0, this.getId());
            this.level.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);
            this.enderTeleportTo(d0, d1, d2);
        }
    }

    public void a(double d0, double d1, double d2) {
        this.enderTeleportTo(d0, d1, d2);
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        if (this.level instanceof WorldServer) {
            this.setPositionRotation(d0, d1, d2, this.getYRot(), this.getXRot());
            this.recursiveStream().forEach((entity) -> {
                UnmodifiableIterator unmodifiableiterator = entity.passengers.iterator();

                while (unmodifiableiterator.hasNext()) {
                    Entity entity1 = (Entity) unmodifiableiterator.next();

                    entity.a(entity1, Entity::teleportAndSync);
                }

            });
        }
    }

    public boolean cn() {
        return this.getCustomNameVisible();
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (Entity.DATA_POSE.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public void updateSize() {
        EntitySize entitysize = this.dimensions;
        EntityPose entitypose = this.getPose();
        EntitySize entitysize1 = this.a(entitypose);

        this.dimensions = entitysize1;
        this.eyeHeight = this.getHeadHeight(entitypose, entitysize1);
        this.ah();
        boolean flag = (double) entitysize1.width <= 4.0D && (double) entitysize1.height <= 4.0D;

        if (!this.level.isClientSide && !this.firstTick && !this.noPhysics && flag && (entitysize1.width > entitysize.width || entitysize1.height > entitysize.height) && !(this instanceof EntityHuman)) {
            Vec3D vec3d = this.getPositionVector().add(0.0D, (double) entitysize.height / 2.0D, 0.0D);
            double d0 = (double) Math.max(0.0F, entitysize1.width - entitysize.width) + 1.0E-6D;
            double d1 = (double) Math.max(0.0F, entitysize1.height - entitysize.height) + 1.0E-6D;
            VoxelShape voxelshape = VoxelShapes.a(AxisAlignedBB.a(vec3d, d0, d1, d0));

            this.level.a(this, voxelshape, vec3d, (double) entitysize1.width, (double) entitysize1.height, (double) entitysize1.width).ifPresent((vec3d1) -> {
                this.b(vec3d1.add(0.0D, (double) (-entitysize1.height) / 2.0D, 0.0D));
            });
        }

    }

    public EnumDirection getDirection() {
        return EnumDirection.fromAngle((double) this.getYRot());
    }

    public EnumDirection getAdjustedDirection() {
        return this.getDirection();
    }

    protected ChatHoverable cq() {
        return new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ENTITY, new ChatHoverable.b(this.getEntityType(), this.getUniqueID(), this.getDisplayName()));
    }

    public boolean a(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public final AxisAlignedBB getBoundingBox() {
        return this.bb;
    }

    public AxisAlignedBB cs() {
        return this.getBoundingBox();
    }

    protected AxisAlignedBB d(EntityPose entitypose) {
        EntitySize entitysize = this.a(entitypose);
        float f = entitysize.width / 2.0F;
        Vec3D vec3d = new Vec3D(this.locX() - (double) f, this.locY(), this.locZ() - (double) f);
        Vec3D vec3d1 = new Vec3D(this.locX() + (double) f, this.locY() + (double) entitysize.height, this.locZ() + (double) f);

        return new AxisAlignedBB(vec3d, vec3d1);
    }

    public final void a(AxisAlignedBB axisalignedbb) {
        this.bb = axisalignedbb;
    }

    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.85F;
    }

    public float e(EntityPose entitypose) {
        return this.getHeadHeight(entitypose, this.a(entitypose));
    }

    public final float getHeadHeight() {
        return this.eyeHeight;
    }

    public Vec3D cu() {
        return new Vec3D(0.0D, (double) this.getHeadHeight(), (double) (this.getWidth() * 0.4F));
    }

    public SlotAccess k(int i) {
        return SlotAccess.NULL;
    }

    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid) {}

    public World getWorld() {
        return this.level;
    }

    @Nullable
    public MinecraftServer getMinecraftServer() {
        return this.level.getMinecraftServer();
    }

    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public boolean cx() {
        return false;
    }

    public void a(EntityLiving entityliving, Entity entity) {
        if (entity instanceof EntityLiving) {
            EnchantmentManager.a((EntityLiving) entity, (Entity) entityliving);
        }

        EnchantmentManager.b(entityliving, entity);
    }

    public void c(EntityPlayer entityplayer) {}

    public void d(EntityPlayer entityplayer) {}

    public float a(EnumBlockRotation enumblockrotation) {
        float f = MathHelper.g(this.getYRot());

        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return f + 180.0F;
            case COUNTERCLOCKWISE_90:
                return f + 270.0F;
            case CLOCKWISE_90:
                return f + 90.0F;
            default:
                return f;
        }
    }

    public float a(EnumBlockMirror enumblockmirror) {
        float f = MathHelper.g(this.getYRot());

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return -f;
            case FRONT_BACK:
                return 180.0F - f;
            default:
                return f;
        }
    }

    public boolean cy() {
        return false;
    }

    @Nullable
    public Entity getRidingPassenger() {
        return null;
    }

    public final List<Entity> getPassengers() {
        return this.passengers;
    }

    @Nullable
    public Entity cB() {
        return this.passengers.isEmpty() ? null : (Entity) this.passengers.get(0);
    }

    public boolean u(Entity entity) {
        return this.passengers.contains(entity);
    }

    public boolean a(Predicate<Entity> predicate) {
        UnmodifiableIterator unmodifiableiterator = this.passengers.iterator();

        Entity entity;

        do {
            if (!unmodifiableiterator.hasNext()) {
                return false;
            }

            entity = (Entity) unmodifiableiterator.next();
        } while (!predicate.test(entity));

        return true;
    }

    private Stream<Entity> n() {
        return this.passengers.stream().flatMap(Entity::recursiveStream);
    }

    @Override
    public Stream<Entity> recursiveStream() {
        return Stream.concat(Stream.of(this), this.n());
    }

    @Override
    public Stream<Entity> cD() {
        return Stream.concat(this.passengers.stream().flatMap(Entity::cD), Stream.of(this));
    }

    public Iterable<Entity> getAllPassengers() {
        return () -> {
            return this.n().iterator();
        };
    }

    public boolean hasSinglePlayerPassenger() {
        return this.n().filter((entity) -> {
            return entity instanceof EntityHuman;
        }).count() == 1L;
    }

    public Entity getRootVehicle() {
        Entity entity;

        for (entity = this; entity.isPassenger(); entity = entity.getVehicle()) {
            ;
        }

        return entity;
    }

    public boolean isSameVehicle(Entity entity) {
        return this.getRootVehicle() == entity.getRootVehicle();
    }

    public boolean w(Entity entity) {
        return this.n().anyMatch((entity1) -> {
            return entity1 == entity;
        });
    }

    public boolean cH() {
        Entity entity = this.getRidingPassenger();

        return entity instanceof EntityHuman ? ((EntityHuman) entity).fi() : !this.level.isClientSide;
    }

    protected static Vec3D a(double d0, double d1, float f) {
        double d2 = (d0 + d1 + 9.999999747378752E-6D) / 2.0D;
        float f1 = -MathHelper.sin(f * 0.017453292F);
        float f2 = MathHelper.cos(f * 0.017453292F);
        float f3 = Math.max(Math.abs(f1), Math.abs(f2));

        return new Vec3D((double) f1 * d2 / (double) f3, 0.0D, (double) f2 * d2 / (double) f3);
    }

    public Vec3D b(EntityLiving entityliving) {
        return new Vec3D(this.locX(), this.getBoundingBox().maxY, this.locZ());
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.NORMAL;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    public int getMaxFireTicks() {
        return 1;
    }

    public CommandListenerWrapper getCommandListener() {
        return new CommandListenerWrapper(this, this.getPositionVector(), this.br(), this.level instanceof WorldServer ? (WorldServer) this.level : null, this.y(), this.getDisplayName().getString(), this.getScoreboardDisplayName(), this.level.getMinecraftServer(), this);
    }

    protected int y() {
        return 0;
    }

    public boolean l(int i) {
        return this.y() >= i;
    }

    @Override
    public boolean shouldSendSuccess() {
        return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
    }

    @Override
    public boolean shouldSendFailure() {
        return true;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return true;
    }

    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        Vec3D vec3d1 = argumentanchor_anchor.a(this);
        double d0 = vec3d.x - vec3d1.x;
        double d1 = vec3d.y - vec3d1.y;
        double d2 = vec3d.z - vec3d1.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        this.setXRot(MathHelper.g((float) (-(MathHelper.d(d1, d3) * 57.2957763671875D))));
        this.setYRot(MathHelper.g((float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F));
        this.setHeadRotation(this.getYRot());
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
    }

    public boolean a(Tag<FluidType> tag, double d0) {
        if (this.cM()) {
            return false;
        } else {
            AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
            int i = MathHelper.floor(axisalignedbb.minX);
            int j = MathHelper.e(axisalignedbb.maxX);
            int k = MathHelper.floor(axisalignedbb.minY);
            int l = MathHelper.e(axisalignedbb.maxY);
            int i1 = MathHelper.floor(axisalignedbb.minZ);
            int j1 = MathHelper.e(axisalignedbb.maxZ);
            double d1 = 0.0D;
            boolean flag = this.ck();
            boolean flag1 = false;
            Vec3D vec3d = Vec3D.ZERO;
            int k1 = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = k; i2 < l; ++i2) {
                    for (int j2 = i1; j2 < j1; ++j2) {
                        blockposition_mutableblockposition.d(l1, i2, j2);
                        Fluid fluid = this.level.getFluid(blockposition_mutableblockposition);

                        if (fluid.a(tag)) {
                            double d2 = (double) ((float) i2 + fluid.getHeight(this.level, blockposition_mutableblockposition));

                            if (d2 >= axisalignedbb.minY) {
                                flag1 = true;
                                d1 = Math.max(d2 - axisalignedbb.minY, d1);
                                if (flag) {
                                    Vec3D vec3d1 = fluid.c(this.level, blockposition_mutableblockposition);

                                    if (d1 < 0.4D) {
                                        vec3d1 = vec3d1.a(d1);
                                    }

                                    vec3d = vec3d.e(vec3d1);
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }

            if (vec3d.f() > 0.0D) {
                if (k1 > 0) {
                    vec3d = vec3d.a(1.0D / (double) k1);
                }

                if (!(this instanceof EntityHuman)) {
                    vec3d = vec3d.d();
                }

                Vec3D vec3d2 = this.getMot();

                vec3d = vec3d.a(d0 * 1.0D);
                double d3 = 0.003D;

                if (Math.abs(vec3d2.x) < 0.003D && Math.abs(vec3d2.z) < 0.003D && vec3d.f() < 0.0045000000000000005D) {
                    vec3d = vec3d.d().a(0.0045000000000000005D);
                }

                this.setMot(this.getMot().e(vec3d));
            }

            this.fluidHeight.put(tag, d1);
            return flag1;
        }
    }

    public boolean cM() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().g(1.0D);
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.e(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minZ);
        int l = MathHelper.e(axisalignedbb.maxZ);

        return !this.level.b(i, k, j, l);
    }

    public double b(Tag<FluidType> tag) {
        return this.fluidHeight.getDouble(tag);
    }

    public double cN() {
        return (double) this.getHeadHeight() < 0.4D ? 0.0D : 0.4D;
    }

    public final float getWidth() {
        return this.dimensions.width;
    }

    public final float getHeight() {
        return this.dimensions.height;
    }

    public abstract Packet<?> getPacket();

    public EntitySize a(EntityPose entitypose) {
        return this.type.m();
    }

    public Vec3D getPositionVector() {
        return this.position;
    }

    @Override
    public BlockPosition getChunkCoordinates() {
        return this.blockPosition;
    }

    public IBlockData cS() {
        return this.level.getType(this.getChunkCoordinates());
    }

    public BlockPosition cT() {
        return new BlockPosition(this.i(1.0F));
    }

    public ChunkCoordIntPair cU() {
        return new ChunkCoordIntPair(this.blockPosition);
    }

    public Vec3D getMot() {
        return this.deltaMovement;
    }

    public void setMot(Vec3D vec3d) {
        this.deltaMovement = vec3d;
    }

    public void setMot(double d0, double d1, double d2) {
        this.setMot(new Vec3D(d0, d1, d2));
    }

    public final int cW() {
        return this.blockPosition.getX();
    }

    public final double locX() {
        return this.position.x;
    }

    public double c(double d0) {
        return this.position.x + (double) this.getWidth() * d0;
    }

    public double d(double d0) {
        return this.c((2.0D * this.random.nextDouble() - 1.0D) * d0);
    }

    public final int cY() {
        return this.blockPosition.getY();
    }

    public final double locY() {
        return this.position.y;
    }

    public double e(double d0) {
        return this.position.y + (double) this.getHeight() * d0;
    }

    public double da() {
        return this.e(this.random.nextDouble());
    }

    public double getHeadY() {
        return this.position.y + (double) this.eyeHeight;
    }

    public final int dc() {
        return this.blockPosition.getZ();
    }

    public final double locZ() {
        return this.position.z;
    }

    public double f(double d0) {
        return this.position.z + (double) this.getWidth() * d0;
    }

    public double g(double d0) {
        return this.f((2.0D * this.random.nextDouble() - 1.0D) * d0);
    }

    public final void setPositionRaw(double d0, double d1, double d2) {
        if (this.position.x != d0 || this.position.y != d1 || this.position.z != d2) {
            this.position = new Vec3D(d0, d1, d2);
            int i = MathHelper.floor(d0);
            int j = MathHelper.floor(d1);
            int k = MathHelper.floor(d2);

            if (i != this.blockPosition.getX() || j != this.blockPosition.getY() || k != this.blockPosition.getZ()) {
                this.blockPosition = new BlockPosition(i, j, k);
            }

            this.levelCallback.a();
            GameEventListenerRegistrar gameeventlistenerregistrar = this.bQ();

            if (gameeventlistenerregistrar != null) {
                gameeventlistenerregistrar.b(this.level);
            }
        }

    }

    public void checkDespawn() {}

    public Vec3D n(float f) {
        return this.k(f).add(0.0D, (double) this.eyeHeight * 0.7D, 0.0D);
    }

    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        int i = packetplayoutspawnentity.b();
        double d0 = packetplayoutspawnentity.d();
        double d1 = packetplayoutspawnentity.e();
        double d2 = packetplayoutspawnentity.f();

        this.d(d0, d1, d2);
        this.teleportAndSync(d0, d1, d2);
        this.setXRot((float) (packetplayoutspawnentity.j() * 360) / 256.0F);
        this.setYRot((float) (packetplayoutspawnentity.k() * 360) / 256.0F);
        this.e(i);
        this.a_(packetplayoutspawnentity.c());
    }

    @Nullable
    public ItemStack df() {
        return null;
    }

    public void o(boolean flag) {
        this.isInPowderSnow = flag;
    }

    public boolean dg() {
        return !TagsEntity.FREEZE_IMMUNE_ENTITY_TYPES.isTagged(this.getEntityType());
    }

    public float getYRot() {
        return this.yRot;
    }

    public void setYRot(float f) {
        if (!Float.isFinite(f)) {
            SystemUtils.a("Invalid entity rotation: " + f + ", discarding.");
        } else {
            this.yRot = f;
        }
    }

    public float getXRot() {
        return this.xRot;
    }

    public void setXRot(float f) {
        if (!Float.isFinite(f)) {
            SystemUtils.a("Invalid entity rotation: " + f + ", discarding.");
        } else {
            this.xRot = f;
        }
    }

    public final boolean isRemoved() {
        return this.removalReason != null;
    }

    @Nullable
    public Entity.RemovalReason getRemovalReason() {
        return this.removalReason;
    }

    @Override
    public final void setRemoved(Entity.RemovalReason entity_removalreason) {
        if (this.removalReason == null) {
            this.removalReason = entity_removalreason;
        }

        if (this.removalReason.a()) {
            this.stopRiding();
        }

        this.getPassengers().forEach(Entity::stopRiding);
        this.levelCallback.a(entity_removalreason);
    }

    public void unsetRemoved() {
        this.removalReason = null;
    }

    @Override
    public void a(EntityInLevelCallback entityinlevelcallback) {
        this.levelCallback = entityinlevelcallback;
    }

    @Override
    public boolean dm() {
        return this.removalReason != null && !this.removalReason.b() ? false : (this.isPassenger() ? false : !this.isVehicle() || !this.hasSinglePlayerPassenger());
    }

    @Override
    public boolean dn() {
        return false;
    }

    public boolean a(World world, BlockPosition blockposition) {
        return true;
    }

    public static enum RemovalReason {

        KILLED(true, false), DISCARDED(true, false), UNLOADED_TO_CHUNK(false, true), UNLOADED_WITH_PLAYER(false, false), CHANGED_DIMENSION(false, false);

        private final boolean destroy;
        private final boolean save;

        private RemovalReason(boolean flag, boolean flag1) {
            this.destroy = flag;
            this.save = flag1;
        }

        public boolean a() {
            return this.destroy;
        }

        public boolean b() {
            return this.save;
        }
    }

    public static enum MovementEmission {

        NONE(false, false), SOUNDS(true, false), EVENTS(false, true), ALL(true, true);

        final boolean sounds;
        final boolean events;

        private MovementEmission(boolean flag, boolean flag1) {
            this.sounds = flag;
            this.events = flag1;
        }

        public boolean a() {
            return this.events || this.sounds;
        }

        public boolean b() {
            return this.events;
        }

        public boolean c() {
            return this.sounds;
        }
    }

    @FunctionalInterface
    public interface MoveFunction {

        void accept(Entity entity, double d0, double d1, double d2);
    }
}
